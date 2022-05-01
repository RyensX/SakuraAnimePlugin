package com.su.sakuraanimeplugin.plugin.components

import com.su.mediabox.pluginapi.v2.been.VideoPlayMedia
import com.su.mediabox.pluginapi.v2.components.IVideoPlayComponent
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import org.jsoup.nodes.Element

class CustomVideoPlayComponent : IVideoPlayComponent {

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
        val url = CustomConst.host + episodeUrl
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