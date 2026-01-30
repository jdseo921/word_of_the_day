package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MerriamWebsterApi {
    @GET("references/collegiate/json/{word}")
    suspend fun getWordDefinition(
        @Path("word") word: String,
        @Query("key") apiKey: String
    ): List<MerriamWebsterResponse>
}

data class MerriamWebsterResponse(
    @SerializedName("meta")
    val meta: Meta,
    @SerializedName("fl")
    val functionalLabel: String,
    @SerializedName("shortdef")
    val shortDefinitions: List<String>
)

data class Meta(
    @SerializedName("id")
    val id: String,
    @SerializedName("uuid")
    val uuid: String,
    @SerializedName("stems")
    val stems: List<String>
)