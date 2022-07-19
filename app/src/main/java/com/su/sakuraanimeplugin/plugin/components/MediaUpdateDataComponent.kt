package com.su.sakuraanime2plugin.plugin.components

import com.su.mediabox.pluginapi.components.IMediaUpdateDataComponent
import com.su.sakuraanimeplugin.plugin.components.Const
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil

object MediaUpdateDataComponent : IMediaUpdateDataComponent {

    private val updateRegex = Regex("(?<=更新至)(.*)")

    override suspend fun getUpdateTag(detailUrl: String): String? {
        val doc = JsoupUtil.getDocument(Const.host + detailUrl)
        val sinfo = doc.select("[class=sinfo]")
        return try {
            val rawText = sinfo.select("p")
                .run { if (size == 1) get(0) else get(1) }
                .text()
            updateRegex.find(rawText)?.value ?: rawText
        } catch (_: Exception) {
            null
        }
    }

}