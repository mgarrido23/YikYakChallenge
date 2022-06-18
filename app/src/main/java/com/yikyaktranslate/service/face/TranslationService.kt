package com.yikyaktranslate.service.face

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import com.yikyaktranslate.model.Language
import com.yikyaktranslate.model.Translation
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface TranslationService {

    @GET("/languages")
    suspend fun getLanguages() : List<Language>

    @FormUrlEncoded
    @POST("/translate")
    suspend fun translate(
        @Field("q") textToTranslate:String,
        @Field("source") source:String,
        @Field("target") target:String,
        @Field("format") format:String
    ):Translation

    companion object {
        private const val BASE_URL = "https://libretranslate.de/" // this official mirror site doesn't require api key

        fun create() : TranslationService {
            val moshi = Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create(TranslationService::class.java)
        }
    }

}