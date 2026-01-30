package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api

import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Interface for the News API (newsapi.org).
 * Used to provide real-world context for vocabulary learning.
 */
interface NewsApi {
    @GET("everything")
    suspend fun getEverything(
        @Query("q") query: String,
        @Query("apiKey") apiKey: String,
        @Query("pageSize") pageSize: Int = 10,
        @Query("sortBy") sortBy: String = "relevancy",
        @Query("language") language: String = "en"
    ): NewsResponse
}

data class NewsResponse(
    val status: String,
    val totalResults: Int,
    val articles: List<Article>,
    val code: String?,
    val message: String?
)

data class Article(
    val source: Source,
    val author: String?,
    val title: String,
    val description: String?,
    val url: String,
    val publishedAt: String,
    val content: String?,
    val urlToImage: String? // Added to provide visual context
)

data class Source(
    val name: String
)