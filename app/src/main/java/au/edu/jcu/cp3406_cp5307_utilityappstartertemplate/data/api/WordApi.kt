package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api

import retrofit2.http.GET
import retrofit2.http.Path

interface WordApi {
    @GET("entries/en/{word}")
    suspend fun getWordDefinition(@Path("word") word: String): List<WordResponse>
}

data class WordResponse(
    val word: String,
    val meanings: List<Meaning>
)

data class Meaning(
    val partOfSpeech: String,
    val definitions: List<Definition>
)

data class Definition(
    val definition: String
)