package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface WikipediaApi {
    @GET("api.php")
    suspend fun search(
        @Query("action") action: String = "query",
        @Query("list") list: String = "search",
        @Query("srsearch") query: String,
        @Query("format") format: String = "json",
        @Query("utf8") utf8: Int = 1,
        @Query("origin") origin: String = "*"
    ): WikipediaResponse
}

data class WikipediaResponse(
    @SerializedName("query")
    val query: WikipediaQuery? = null
)

data class WikipediaQuery(
    @SerializedName("search")
    val search: List<WikiSearchItem> = emptyList()
)

data class WikiSearchItem(
    @SerializedName("title")
    val title: String,
    @SerializedName("snippet")
    val snippet: String,
    @SerializedName("timestamp")
    val timestamp: String,
    @SerializedName("pageid")
    val pageid: Int
)