package com.su.sakuraanimeplugin.plugin.util

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import com.su.mediabox.pluginapi.action.ClassifyAction
import com.su.mediabox.pluginapi.action.DetailAction
import com.su.mediabox.pluginapi.data.*
import com.su.sakuraanimeplugin.plugin.components.Const.host
import java.net.URL

object ParseHtmlUtil {

    fun parseTopli(
        element: Element
    ): List<SimpleTextData> {
        val animeShowList = mutableListOf<SimpleTextData>()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            var url: String
            var title: String
            if (elements[i].select("a").size >= 2) {    //最近更新，显示地区的情况
                url = elements[i].select("a")[1].attr("href")
                title = elements[i].select("a")[1].text()
                if (elements[i].select("span")[0].children().size == 0) {     //最近更新，不显示地区的情况
                    url = elements[i].select("a")[0].attr("href")
                    title = elements[i].select("a")[0].text()
                }
            } else {                                            //总排行榜
                url = elements[i].select("a")[0].attr("href")
                title = elements[i].select("a")[0].text()
            }

            val areaUrl = elements[i].select("span").select("a")
                .attr("href")
            val areaTitle = elements[i].select("span").select("a").text()
            var episodeUrl = elements[i].select("b").select("a")
                .attr("href")
            val episodeTitle = elements[i].select("b").select("a").text()
            val date = elements[i].select("em").text()
            if (episodeUrl == "") {
                episodeUrl = url
            }
            animeShowList.add(SimpleTextData(title).apply {
                action = DetailAction.obtain(url)
            })
        }
        return animeShowList
    }

    fun getCoverUrl(cover: String, imageReferer: String): String {
        return when {
            cover.startsWith("//") -> {
                try {
                    "${URL(imageReferer).protocol}:$cover"
                } catch (e: Exception) {
                    e.printStackTrace()
                    cover
                }
            }
            cover.startsWith("/") -> {
                //url不全的情况
                host + cover
            }
            else -> cover
        }
    }

    /**
     * 解析搜索/分类下的元素
     *
     * @param element ul的父元素
     */
    fun parseSearchEm(
        element: Element,
        imageReferer: String
    ): List<BaseData> {
        val videoInfoItemDataList = mutableListOf<BaseData>()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            var cover = results[i].select("a").select("img").attr("src")
            cover = getCoverUrl(cover, imageReferer)
            val title = results[i].select("h2").select("a").text()
            val url = results[i].select("h2").select("a").attr("href")
            val episode = results[i].select("span").select("font").text()
            val types = results[i].select("span")[1].select("a")
            val tags = mutableListOf<TagData>()
            for (type in types)
                tags.add(TagData(type.text()).apply {
                    action = ClassifyAction.obtain(
                        type.attr("href"),
                        "", type.text()
                    )
                })
            val describe = results[i].select("p").text()
            val item = MediaInfo2Data(
                title, cover, host + url,
                episode, describe, tags
            )
                .apply {
                    action = DetailAction.obtain(url)
                }
            videoInfoItemDataList.add(item)
        }
        return videoInfoItemDataList
    }

    /**
     * 解析分类元素
     */
    fun parseClassifyEm(element: Element): List<ClassifyItemData> {
        val classifyItemDataList = mutableListOf<ClassifyItemData>()
        var classifyCategory = ""
        for (em in element.select("p"))
            for (target in em.children())
                when (target.tagName()) {
                    //分类类别
                    "label" -> classifyCategory =
                        target.text().replace(":", "").replace("：", "").trim()
                    //分类项
                    "a" -> classifyItemDataList.add(ClassifyItemData().apply {
                        action = ClassifyAction.obtain(
                            target.attr("href"),
                            classifyCategory,
                            target.text()
                        )
                    })
                }
        return classifyItemDataList
    }
}