/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import kotlinx.android.synthetic.main.fatal_error.view.*
import kotlinx.android.synthetic.main.fragment_search.view.*
import online.vapcom.skyword.R
import online.vapcom.skyword.SkywordApplication
import online.vapcom.skyword.data.ErrorDescription
import online.vapcom.skyword.data.Meaning
import online.vapcom.skyword.databinding.FragmentSearchBinding
import online.vapcom.skyword.ui.states.ScreenState
import online.vapcom.skyword.ui.states.SearchState
import online.vapcom.skyword.viewmodels.SearchViewModel

private const val TAG = "SearchFrag"

/**
 * Фрагмент строки поиска перевода и списка результатов
 */
class SearchFragment: BaseFragment() {

    private lateinit var viewModel: SearchViewModel
    private lateinit var binding: FragmentSearchBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.i(TAG, "-- onCreate")

        val repo = (requireContext().applicationContext as SkywordApplication).dictRepository
        viewModel = ViewModelProvider(this, SearchViewModel.FACTORY(repo)).get(SearchViewModel::class.java)
        subscribeUI()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        root = binding.root

        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.wordClickListener = View.OnClickListener { v ->  // тап на чипе значения
            val meaning = v.tag as Meaning

            Log.i(TAG, "-- MEANING ONCLICK on: ${meaning.id}:${meaning.translation}")

            // переход на фрагмент подробного описания значения слова
            val args = bundleOf(ArgsTags.ARG_MEANING_ID to meaning.id)
            findNavController().navigate(R.id.action_searchFragment_to_wordDetailsFragment, args)
        }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun subscribeUI() {
        viewModel.state.observe(::getLifecycle, ::updateUI)
    }

    private fun setupUI() {

        root.search.setOnClickListener {
            hideKeyboard()
            viewModel.searchWord()
        }


        // по кнопке Подтв. на всплывающей клавиатуре сразу делаем переход на подтверждение
        root.word_text.setOnEditorActionListener { _, actionId, _ ->
            Log.i(TAG, "-- IME_ACTION: '$actionId'")

            when (actionId) {
                EditorInfo.IME_ACTION_DONE, EditorInfo.IME_ACTION_UNSPECIFIED -> {
                    Log.i(TAG, "-- IME_ACTION: '$actionId'")

                    hideKeyboard()
                    viewModel.searchWord()
                    true
                }
                else -> false
            }
        }

    }

    /**
     * Обработчик индикаций и ответов от viewModel
     */
    private fun updateUI(screenState: ScreenState<SearchState>?) {
        Log.i(TAG, "-- updateUI: $screenState")

        when (screenState) {
            ScreenState.Loading -> showProgress()
            is ScreenState.Render -> processRenderState(screenState.renderState)
        }
    }


    private fun showProgress() {
        Log.i(TAG, "-- SHOW Progress")
        root.progress.visibility = View.VISIBLE
        root.fatal_error.visibility = View.GONE
    }

    private fun hideProgress() {
        Log.i(TAG, "-- HIDE Progress")
        root.progress.visibility = View.GONE
        root.fatal_error.visibility = View.GONE
    }

    /**
     * Обработка индикаций от viewModel.
     *
     */
    private fun processRenderState(renderState: SearchState) {
        Log.i(TAG, "-- processRenderState: $renderState")

        hideProgress()
        when (renderState) {
            is SearchState.FoundTranslations -> {
                // здесь пока ничего не делаем, список значений отобразиться через байндинг
                Log.i(TAG, "-- processRenderState: FoundTranslations: ${renderState.translations}")
            }
            is SearchState.ShowError -> showError(renderState.error)
        }
    }


    override fun onBackPressed() = true     // из стартового фрагмента не выходим

    override fun onShowFatalError() {
        root.progress.visibility = View.GONE
        root.content.visibility = View.GONE
        root.fatal_error.visibility = View.VISIBLE
    }

    override fun showCommonError(error: ErrorDescription): Boolean = false

}