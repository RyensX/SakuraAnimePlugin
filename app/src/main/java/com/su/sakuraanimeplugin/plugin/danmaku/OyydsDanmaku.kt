package com.su.sakuraanimeplugin.plugin.danmaku

data class OyydsDanmaku(
    val code: Int,
    val `data`: Data?,
    val msg: String
) {

    companion object {
        const val OYYDS_DANMAKU_ENABLE = "OYYDS弹幕"
    }

    data class Data(
        val `data`: List<DataX>?,
        val episode: Episode,
        val total: Int
    )

    data class DataX(
        val color: String,
        val content: String,
        val createdAt: String,
        val episodeId: String,
        val id: String,
        val ip: String,
        val time: String,
        val type: String,
        val updatedAt: String,
        val userId: String
    )

    data class Episode(
        val createdAt: String,
        val goodsId: String,
        val id: String,
        val number: String,
        val updatedAt: String
    )
}