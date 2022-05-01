package com.su.sakuraanimeplugin.plugin.deprecateds

import android.net.Uri
import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.ISearchComponent
import com.su.sakuraanimeplugin.plugin.components.CustomConst
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.ParseHtmlUtil
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomSearchModel : ISearchComponent {
    override suspend fun getSearchData(
        keyWord: String,
        partUrl: String
    ): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val const = CustomConst
        var pageNumberBean: PageNumberBean? = null
        val searchResultList: ArrayList<AnimeCoverBean> = ArrayList()
        val url =
            "${CustomConst.host}${CustomConst.ANIME_SEARCH}${Uri.encode(keyWord, ":/-![].,%?&=")}/$partUrl"
        val document = JsoupUtil.getDocument(url)
        val lpic: Elements = document.getElementsByClass("area")
            .select("[class=fire l]").select("[class=lpic]")
        searchResultList.addAll(ParseHtmlUtil.parseLpic(lpic[0], url))
        val pages = lpic[0].select("[class=pages]")
        if (pages.size > 0) pageNumberBean = ParseHtmlUtil.parseNextPages(pages[0])
        return Pair(searchResultList, pageNumberBean)
    }
}