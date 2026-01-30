package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.repository

import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.Article
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.MerriamWebsterApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.MerriamWebsterResponse
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.NewsApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.RandomWordApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.WordApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.WordResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WordRepository @Inject constructor(
    private val wordApi: WordApi,
    private val randomWordApi: RandomWordApi,
    private val newsApi: NewsApi,
    private val merriamWebsterApi: MerriamWebsterApi
) {
    suspend fun getWordDefinition(word: String): Result<WordResponse> {
        return try {
            val response = wordApi.getWordDefinition(word)
            if (response.isNotEmpty()) {
                Result.success(response[0])
            } else {
                Result.failure(Exception("Word not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMerriamWebsterDefinition(word: String, apiKey: String): Result<List<MerriamWebsterResponse>> {
        return try {
            val response = merriamWebsterApi.getWordDefinition(word, apiKey)
            Result.success(response)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRandomWords(count: Int): Result<List<String>> {
        return try {
            val words = randomWordApi.getRandomWords(count)
            Result.success(words)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNewsUsage(word: String, stems: List<String> = emptyList(), apiKey: String): Result<List<Article>> {
        return try {
            // Strategy 1: Exact word search (most reliable context)
            var response = newsApi.getEverything(query = "\"$word\"", apiKey = apiKey, pageSize = 5)
            if (response.articles.isNotEmpty()) return Result.success(response.articles)

            // Strategy 2: Search for variations/stems if provided
            for (stem in stems.take(3)) {
                if (stem.equals(word, ignoreCase = true)) continue
                response = newsApi.getEverything(query = "\"$stem\"", apiKey = apiKey, pageSize = 5)
                if (response.articles.isNotEmpty()) return Result.success(response.articles)
            }

            // Strategy 3: Broad search (without quotes)
            response = newsApi.getEverything(query = word, apiKey = apiKey, pageSize = 5)
            Result.success(response.articles)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}