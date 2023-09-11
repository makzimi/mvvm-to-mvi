package ru.otus.tomvi.data

import retrofit2.http.GET
import retrofit2.http.Query
import ru.otus.tomvi.data.dto.RAMResponseDto

interface RAMRetrofitService {
    @GET("character/")
    suspend fun getCharacters(@Query("page") page: Int = 0): RAMResponseDto
}
