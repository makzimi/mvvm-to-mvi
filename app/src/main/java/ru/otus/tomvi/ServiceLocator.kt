package ru.otus.tomvi

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import ru.otus.tomvi.data.CharactersRepository
import ru.otus.tomvi.data.FavoritesDataSource
import ru.otus.tomvi.data.FavoritesRepository
import ru.otus.tomvi.data.RAMRetrofitService
import ru.otus.tomvi.presentation.CharacterStateFactory
import ru.otus.tomvi.presentation.CustomViewModelFactory

private const val BASE_URL = "https://rickandmortyapi.com/api/"

class ServiceLocator(private val applicationContext: Context) {

    private fun provideOkHttpClient(): OkHttpClient {
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    private fun provideRAMService(): RAMRetrofitService {
        val contentType = "application/json".toMediaType()
        val retrofit =
            Retrofit.Builder()
                .client(provideOkHttpClient())
                .baseUrl(BASE_URL)
                .addConverterFactory(provideJson().asConverterFactory(contentType))
                .build()
        return retrofit.create(RAMRetrofitService::class.java)
    }

    @OptIn(ExperimentalSerializationApi::class)
    private fun provideJson(): Json {
        return Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    private fun provideCharactersRepository(): CharactersRepository {
        return CharactersRepository(provideRAMService())
    }

    private fun provideFavoritesRepository(): FavoritesRepository {
        return FavoritesRepository(provideFavoritesDataSource())
    }

    private fun provideFavoritesDataSource(): FavoritesDataSource {
        return FavoritesDataSource(dataStore = applicationContext.appDataStore)
    }

    private fun provideCharacterStateFactory(): CharacterStateFactory {
        return CharacterStateFactory()
    }

    fun provideViewModelFactory(): ViewModelProvider.Factory {
        return CustomViewModelFactory(
            charactersRepository = provideCharactersRepository(),
            favoritesRepository = provideFavoritesRepository(),
            characterStateFactory = provideCharacterStateFactory(),
        )
    }

    private val Context.appDataStore: DataStore<Preferences> by preferencesDataStore(name = "app")
}

fun Fragment.getServiceLocator(): ServiceLocator {
    return (requireActivity().applicationContext as RaMApplication).serviceLocator
}
