package com.su.sakuraanimeplugin.plugin.danmaku

import android.graphics.Color
import com.kuaishou.akdanmaku.data.DanmakuItemData


object OyydsDanmakuParser {
    fun convert(raw: OyydsDanmaku.DataX): DanmakuItemData? {
        return try {
            DanmakuItemData(
                raw.id.hashCode().toLong(),
                //这个弹幕系统时间是秒
                (raw.time.toFloat() * 1000).toLong(),
                raw.content,
                danmakuTypeMap[raw.type] ?: DanmakuItemData.DANMAKU_MODE_ROLLING,
                25,
                try {
                    Color.parseColor(raw.color)
                } catch (e: Exception) {
                    Color.WHITE
                }
            )
        } catch (e: Exception) {
            //Log.d("丢弃错误弹幕", raw.toString())
            //e.printStackTrace()
            null
        }
    }

    val danmakuTypeMap = mapOf(
        "scroll" to DanmakuItemData.DANMAKU_MODE_ROLLING,
        "top" to DanmakuItemData.DANMAKU_MODE_CENTER_TOP,
        "bottom" to DanmakuItemData.DANMAKU_MODE_CENTER_BOTTOM
    )
}