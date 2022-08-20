package com.su.sakuraanimeplugin.plugin.components

import android.util.Log
import com.kuaishou.akdanmaku.data.DanmakuItemData
import com.su.mediabox.pluginapi.components.IVideoPlayPageDataComponent
import com.su.mediabox.pluginapi.data.VideoPlayMedia
import com.su.mediabox.pluginapi.util.PluginPreferenceIns
import com.su.sakuraanimeplugin.plugin.components.Const.host
import com.su.sakuraanimeplugin.plugin.danmaku.OyydsDanmaku
import com.su.sakuraanimeplugin.plugin.danmaku.OyydsDanmakuParser
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.Text.trimAll
import com.su.sakuraanimeplugin.plugin.util.oyydsDanmakuApis
import org.jsoup.nodes.Element

class CustomVideoPlayPageDataComponent : IVideoPlayPageDataComponent {

    private var episodeDanmakuId = ""
    override suspend fun getDanmakuData(
        videoName: String,
        episodeName: String,
        episodeUrl: String
    ): List<DanmakuItemData>? {
        try {
            val config = PluginPreferenceIns.get(OyydsDanmaku.OYYDS_DANMAKU_ENABLE, true)
            if (!config)
                return null
            val name = videoName.trimAll()
            var episode = episodeName.trimAll()
            //剧集对集去除所有额外字符，增大弹幕适应性
            val episodeIndex = episode.indexOf("集")
            if (episodeIndex > -1 && episodeIndex != episode.length - 1) {
                episode = episode.substring(0, episodeIndex + 1)
            }
            Log.d("请求Oyyds弹幕", "媒体:$name 剧集:$episode")
            return oyydsDanmakuApis.getDanmakuData(name, episode).data.let { danmukuData ->
                val data = mutableListOf<DanmakuItemData>()
                danmukuData?.data?.forEach { dataX ->
                    OyydsDanmakuParser.convert(dataX)?.also { data.add(it) }
                }
                episodeDanmakuId = danmukuData?.episode?.id ?: ""
                data
            }
        } catch (e: Exception) {
            throw RuntimeException("弹幕加载错误：${e.message}")
        }
    }

    override suspend fun putDanmaku(
        videoName: String,
        episodeName: String,
        episodeUrl: String,
        danmaku: String,
        time: Long,
        color: Int,
        type: Int
    ): Boolean = try {
        Log.d("发送弹幕到Oyyds", "内容:$danmaku 剧集id:$episodeDanmakuId")
        oyydsDanmakuApis.addDanmaku(
            danmaku,
            //Oyyds弹幕标准时间是秒
            (time / 1000F).toString(),
            episodeDanmakuId,
            OyydsDanmakuParser.danmakuTypeMap.entries.find { it.value == type }?.key ?: "scroll",
            String.format("#%02X", color)
        )
        true
    } catch (e: Exception) {
        e.printStackTrace()
        false
    }

    private suspend fun getVideoRawUrl(e: Element): String {
        val div = e.select("[class=area]").select("[class=bofang]")[0].children()
        val rawUrl = div.attr("data-vid")
        return when {
            rawUrl.endsWith("\$mp4", true) -> rawUrl.replace("\$mp4", "").replace("\\", "/")
            rawUrl.endsWith("\$url", true) -> rawUrl.replace("\$url", "")
            rawUrl.endsWith("\$hp", true) -> {
                JsoupUtil.getDocument("http://tup.yhdm.so/hp.php?url=${rawUrl.substringBefore("\$hp")}")
                    .body().select("script")[0].toString()
                    .substringAfter("video: {")
                    .substringBefore("}")
                    .split(",")[0]
                    .substringAfter("url: \"")
                    .substringBefore("\"")
            }
            rawUrl.endsWith("\$qzz", true) -> rawUrl
            else -> ""
        }
    }

    override suspend fun getVideoPlayMedia(episodeUrl: String): VideoPlayMedia {
        var name = ""
        var videoUrl = ""
        val url = host + episodeUrl
        val document = JsoupUtil.getDocument(url)
        document.allElements.forEach {
            when (it.className()) {
                //播放链接
                "play" -> {
                    videoUrl = getVideoRawUrl(it)
                }
                "area" -> {
                    val areaChildren = it.children()
                    for (j in areaChildren.indices) {
                        when (areaChildren[j].className()) {
                            "gohome l" -> {
                                //剧集名
                                name = areaChildren[j].select("h1")
                                    .select("span").text().replace("：", "")
                            }
                        }
                    }
                }
            }
        }
        return VideoPlayMedia(name, videoUrl)
    }

}