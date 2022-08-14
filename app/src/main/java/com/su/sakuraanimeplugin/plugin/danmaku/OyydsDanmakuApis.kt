package com.su.sakuraanimeplugin.plugin.danmaku

import retrofit2.http.*

interface OyydsDanmakuApis {
    @GET("https://api.danmu.oyyds.top/api/message/getSome?type=1")
    suspend fun getDanmakuData(
        @Query("name") name: String,
        @Query("number") number: String
    ): OyydsDanmaku

    @FormUrlEncoded
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @POST("https://api.danmu.oyyds.top/api/message/addOne")
    suspend fun addDanmaku(
        @Field("content") content: String,
        @Field("time") time: String,
        @Field("episodeId") episodeId: String,
        @Field("type") type: String,
        @Field("color") color: String
    )
}