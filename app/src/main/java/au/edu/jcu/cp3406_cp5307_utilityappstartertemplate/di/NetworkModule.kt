package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.di

import android.content.Context
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.MerriamWebsterApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.NewsApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.RandomWordApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.WikipediaApi
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.WordApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }

    @Provides
    @Singleton
    fun provideCache(@ApplicationContext context: Context): Cache {
        val cacheSize = (5 * 1024 * 1024).toLong() // 5 MB
        return Cache(File(context.cacheDir, "http_cache"), cacheSize)
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(
        loggingInterceptor: HttpLoggingInterceptor,
        cache: Cache,
        @ApplicationContext context: Context
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .cache(cache)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                var request = chain.request()
                // Strategy: Prefer cache when offline
                if (!isNetworkAvailable(context)) {
                    request = request.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + 60 * 60 * 24 * 7) // 1 week
                        .build()
                }
                chain.proceed(request)
            }
            .build()
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as android.net.ConnectivityManager
        val activeNetwork = connectivityManager.activeNetworkInfo
        return activeNetwork != null && activeNetwork.isConnected
    }

    @Provides
    @Singleton
    @Named("WordRetrofit")
    fun provideWordRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.dictionaryapi.dev/api/v2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("NewsRetrofit")
    fun provideNewsRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("RandomWordRetrofit")
    fun provideRandomWordRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://random-word-api.herokuapp.com/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("WikipediaRetrofit")
    fun provideWikipediaRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://en.wikipedia.org/w/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("MerriamWebsterRetrofit")
    fun provideMerriamWebsterRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://www.dictionaryapi.com/api/v3/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideWordApi(@Named("WordRetrofit") retrofit: Retrofit): WordApi {
        return retrofit.create(WordApi::class.java)
    }

    @Provides
    @Singleton
    fun provideNewsApi(@Named("NewsRetrofit") retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    @Provides
    @Singleton
    fun provideRandomWordApi(@Named("RandomWordRetrofit") retrofit: Retrofit): RandomWordApi {
        return retrofit.create(RandomWordApi::class.java)
    }

    @Provides
    @Singleton
    fun provideWikipediaApi(@Named("WikipediaRetrofit") retrofit: Retrofit): WikipediaApi {
        return retrofit.create(WikipediaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideMerriamWebsterApi(@Named("MerriamWebsterRetrofit") retrofit: Retrofit): MerriamWebsterApi {
        return retrofit.create(MerriamWebsterApi::class.java)
    }
}
