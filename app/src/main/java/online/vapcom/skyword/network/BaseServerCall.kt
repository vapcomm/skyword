/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.network

import android.util.Log
import online.vapcom.skyword.BuildConfig
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.FormBody
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import online.vapcom.skyword.data.ErrorDescription
import online.vapcom.skyword.data.UIErrno
import online.vapcom.skyword.data.errCode
import online.vapcom.skyword.data.stackTraceToString
import java.io.IOException
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.concurrent.TimeUnit

private const val TAG = "BaseSrCall"

private const val ERROR_MODULE_NUM = 50     // номер модуля в кодах ошибок, см. ErrorDescription


/**
 * Базовый класс запросов к серверу
 */
abstract class BaseServerCall {

    companion object {

        //------- адреса сервиса словаря -------
        private const val BASE_DICT_URL: String = BuildConfig.SERVER_DICT

        const val SEARCH_WORD_URL = "$BASE_DICT_URL/words/search"    // запрос поиска слова в словаре
        const val MEANING_DETAILS_URL = "$BASE_DICT_URL/meanings"    // запрос подробных данных значения слова

        val MEDIA_JSON = "application/json; charset=utf-8".toMediaType()

        private const val WRITE_TIMEOUT_MS = 15000L     // таймаут TCP-сокета на установление соединения и запись данных
        private const val READ_TIMEOUT_MS = 15000L      // таймаут TCP-сокета на чтение данных

        // один OkHttpClient на все запросы
        @Volatile private var httpClient: OkHttpClient? = null

        fun getHttpClient() =
            httpClient ?: synchronized(this) {
                httpClient ?:
                    (if(BuildConfig.HTTP_LOGS_ON) { // логи HTTP включаются только в debug сборке
                        val logging = HttpLoggingInterceptor()
                        logging.level = (HttpLoggingInterceptor.Level.BODY)
                        OkHttpClient.Builder()
                            .addInterceptor(logging)
                            .connectTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .writeTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .build()
                    } else {
                        OkHttpClient.Builder()
                            .connectTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .readTimeout(READ_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .writeTimeout(WRITE_TIMEOUT_MS, TimeUnit.MILLISECONDS)
                            .build()
                    }
                    ).also { httpClient = it}
            }

        /**
         * Добавляет схему к URL, который приходит с сервера, если serverUrl без схемы
         */
        fun addSchemeToURL(serverUrl: String): String {
            return if(serverUrl.startsWith("//")) BASE_DICT_URL.substringBefore("//") + serverUrl
                   else serverUrl
        }


    }   // companion

    var success: Boolean = false
    var errorDescription: ErrorDescription = ErrorDescription.EMPTY

    /**
     * Выполняет POST - запрос на сервер с JSON, должна запускаться в IO-нити
     *
     */
    protected fun executePostRequest(url: String, json: String, authToken: String = "") {
        val builder = Request.Builder().post(json.toRequestBody(MEDIA_JSON))

        executeHTTPRequest(builder, url, authToken) { rsp ->
            parseResponse(rsp)
        }
    }

    /**
     * Выполняет POST - запрос на сервер с данными формы. application/x-www-form-urlencoded
     * Должна запускаться в IO-нити
     *
     */
    fun executePostRequest(url: String, body: FormBody, authToken: String = "") {
        val builder = Request.Builder().post(body)

        executeHTTPRequest(builder, url, authToken) { rsp ->
            parseResponse(rsp)
        }
    }

    /**
     * Выполняет GET - запрос на сервер, в ответ ожидает JSON
     * должна запускаться в IO-нити
     *
     */
    protected fun executeGetRequest(url: String, authToken: String = "") {
        val builder = Request.Builder().get()

        executeHTTPRequest(builder, url, authToken) { rsp ->
            parseResponse(rsp)
        }
    }

    /**
     * Делает GET-запрос, обработчик успешного ответа задаётся в responseProc
     */
    protected fun executeRawGetRequest(url: String, authToken: String, responseProc: (rsp: Response) -> Unit ) {
        val builder = Request.Builder().get()

        executeHTTPRequest(builder, url, authToken, responseProc)
    }


    /**
     * Выполняет запрос на сервер, в builder уже должны быть добавлены .post/.get
     *
     * @param authToken токен, полученный ранее через запрос GetToken, строка вида "<тип> <значение>",
     *                  для неаутентифицированных запросов может отсутствовать
     * @param responseProc функция по обработке HTTP-ответа
     */
    private fun executeHTTPRequest(builder: Request.Builder, url: String, authToken: String = "",
                                   responseProc: (rsp: Response) -> Unit) {
        success = false

        var method = "UNKNOWN"

        try {
            builder.url(url)

            if(authToken.isNotEmpty())
                builder.header("Authorization", authToken)

            val request = builder.build()
            method = request.method

            getHttpClient().newCall(request).execute().use { rsp ->
                if(rsp.isSuccessful) {
                    responseProc(rsp)
                }
                else {
                    success = false
                    errorDescription = when(rsp.code) {
                        in 400..499 -> ErrorDescription(false, UIErrno.CLIENT_ERROR.errno, "${rsp.code} ${rsp.message}")
                        in 500..599 -> ErrorDescription(false, UIErrno.SERVER_ERROR.errno, "${rsp.code} ${rsp.message}")
                        else -> ErrorDescription(false, UIErrno.SERVER_ERROR.errno, "${rsp.code} ${rsp.message}")
                    }
                }
            }
        } catch (ex: Exception) {
            Log.e(TAG, "$method Request to '$url' error: $ex")

            errorDescription = when(ex) {
                is SocketTimeoutException -> {
                    ErrorDescription(false, UIErrno.TIMEOUT.errno, ex.localizedMessage ?: "")
                }
                is UnknownHostException,    // хост не найден в DNS
                is ConnectException -> {    // сеть не доступна, здесь же Connection Refused
                    ErrorDescription(false, UIErrno.CONNECTION_ERROR.errno, ex.localizedMessage ?: "")
                }
                else -> ErrorDescription(true, errCode(ERROR_MODULE_NUM, 10),
                    "ServerCall fatal error", ex.localizedMessage ?: "", stackTraceToString(ex))
            }
        }

    }

    /**
     * Разбор JSON-ответа от сервера
     */
    private fun parseResponse(rsp: Response) {
        val json = rsp.body!!.string()
        success = false

        Log.i(TAG, ">>>>> parseResponse: json: '$json'")

        if (json.isBlank()) {
            Log.e(TAG, "Error: Empty server response: '$json'")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 20),
                "Empty server response: '$json'")
            return
        }

        try {
            val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            parseJson(moshi, json)              // потомки здесь разбирают конкретный JSON
        } catch (ex : JsonDataException) {
            Log.e(TAG, "Error: Bad format of server response: '$json', ex: ${ex.message}")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 21),
                "Bad format of server response", ex.message ?: "")
        } catch (ex : IOException) {
            Log.e(TAG, "Error: Malformed server response: '$json', ex: ${ex.message}")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 22),
                "Malformed server response: ${ex.localizedMessage}", ex.message ?: "")
        } catch (ex : Exception) {
            Log.e(TAG, "Error: Unable to parse server response: '$json', ex: ${ex.message}")
            errorDescription = ErrorDescription(false, errCode(ERROR_MODULE_NUM, 23),
                "Unable to parse server response: ${ex.localizedMessage}", ex.message ?: "")
        }

}

    /**
     * Производит окончательную разборку пришедшего JSON-ответа, исключения отлавливает вызывающая функция
     */
    abstract fun parseJson(moshi: Moshi, json: String)

}
