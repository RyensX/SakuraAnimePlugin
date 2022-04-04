package com.su.mediabox.plugin

import android.net.Uri
import com.su.mediabox.pluginapi.v2.been.BaseData
import com.su.mediabox.pluginapi.v2.components.IVideoSearchDataComponent
import org.jsoup.select.Elements

class CustomVideoSearchDataComponent : IVideoSearchDataComponent {

    override suspend fun getSearchData(keyWord: String, page: Int): List<BaseData> {
        val const = CustomConst
        val searchResultList = mutableListOf<BaseData>()
        val url =
            "${const.host}${const.ANIME_SEARCH}${Uri.encode(keyWord, ":/-![].,%?&=")}/$page"
        val document = JsoupUtil.getDocument(url)
        val lpic: Elements = document.getElementsByClass("area")
            .select("[class=fire l]").select("[class=lpic]")
        searchResultList.addAll(ParseHtmlUtil.parseSearchEm(lpic[0], url))
        return searchResultList
    }

}