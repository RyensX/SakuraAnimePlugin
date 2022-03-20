package com.su.mediabox.plugin

import android.net.Uri
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.AnimeTypeBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.ISearchComponent
import com.su.mediabox.pluginapi.v2.been.TagData
import com.su.mediabox.pluginapi.v2.been.VideoLinearItemData
import com.su.mediabox.pluginapi.v2.components.IVideoSearchDataComponent
import org.jsoup.nodes.Element
import org.jsoup.select.Elements

class CustomVideoSearchDataComponent : IVideoSearchDataComponent {

    fun parseSearch(
        element: Element,
        imageReferer: String
    ): List<VideoLinearItemData> {
        val animeCover3List: MutableList<VideoLinearItemData> = java.util.ArrayList()
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
                    actionUrl = Text.buildRouteActionUrl(
                        Constant.ActionUrl.ANIME_CLASSIFY,
                        types[j].attr("href"),
                        "",
                        types[j].text()
                    )
                })
            }
            val describe = results[i].select("p").text()
            val item = VideoLinearItemData(
                title, cover, CustomConst.host + url,
                episode, describe, tags
            )
                .apply {
                    actionUrl = Text.buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, url)
                }
            animeCover3List.add(item)
        }
        return animeCover3List
    }

    override suspend fun getSearchData(keyWord: String, page: Int): List<VideoLinearItemData> {
        val const = CustomConst
        val searchResultList: ArrayList<VideoLinearItemData> = ArrayList()
        val url =
            "${const.host}${const.ANIME_SEARCH}${Uri.encode(keyWord, ":/-![].,%?&=")}/$page"
        val document = JsoupUtil.getDocument(url)
        val lpic: Elements = document.getElementsByClass("area")
            .select("[class=fire l]").select("[class=lpic]")
        searchResultList.addAll(parseSearch(lpic[0], url))
        return searchResultList
    }

}