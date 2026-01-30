package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api

import retrofit2.http.GET
import retrofit2.http.Query

interface RandomWordApi {
    @GET("word")
    suspend fun getRandomWords(@Query("number") number: Int): List<String>
}