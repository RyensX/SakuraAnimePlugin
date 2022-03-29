package com.su.mediabox.plugin

import android.net.Uri
import com.su.mediabox.pluginapi.v2.action.ClassifyAction
import com.su.mediabox.pluginapi.v2.action.DetailAction
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.been.TagData
import com.su.mediabox.pluginapi.v2.been.VideoInfoItemData
import com.su.mediabox.pluginapi.v2.components.IVideoSearchDataComponent
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class CustomVideoSearchDataComponent : IVideoSearchDataComponent {

    fun parseSearch(
        element: Element,
        imageReferer: String
    ): List<BaseData> {
        val animeCover3List = mutableListOf<BaseData>()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            var cover = results[i].select("a").select("img").attr("src")
            cover = ParseHtmlUtil.getCoverUrl(
                cover,
                imageReferer
            )
            val title = results[i].select("h2").select("a").attr("title")
            val url = results[i].select("h2").select("a").attr("href")
            val episode = results[i].select("span").select("font").text()
            val types = results[i].select("span")[1].select("a")
            val tags = mutableListOf<TagData>()
            for (j in types.indices) {
                tags.add(TagData(types[j].text()).apply {
                    action = ClassifyAction.obtain(
                        types[j].attr("href"),
                        "", types[j].text()
                    )
                })
            }
            val describe = results[i].select("p").text()
            val item = VideoInfoItemData(
                title, cover, CustomConst.host + url,
                episode, describe, tags
            )
                .apply {
                    action = DetailAction.obtain(url)
                }
            animeCover3List.add(item)
        }
        return animeCover3List
    }

    override suspend fun getSearchData(keyWord: String, page: Int): List<BaseData> {
        val const = CustomConst
        val searchResultList = mutableListOf<BaseData>()
        val url =
            "${const.host}${const.ANIME_SEARCH}${Uri.encode(keyWord, ":/-![].,%?&=")}/$page"
        val document = JsoupUtil.getDocument(url)
        val lpic: Elements = document.getElementsByClass("area")
            .select("[class=fire l]").select("[class=lpic]")
        searchResultList.addAll(parseSearch(lpic[0], url))
        return searchResultList
    }

}