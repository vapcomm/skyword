/*
 * (c) VAP Communications Group, 2020
 */

package online.vapcom.skyword.data

interface DictRepository {
    suspend fun searchWord(word: String): RepoReply
    suspend fun getMeaningDetails(meaningID: String): RepoReply
}
