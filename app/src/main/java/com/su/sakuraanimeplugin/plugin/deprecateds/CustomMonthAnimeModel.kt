package com.su.sakuraanimeplugin.plugin.deprecateds

import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.IMonthAnimeComponent
import com.su.sakuraanimeplugin.plugin.components.CustomConst
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.ParseHtmlUtil
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomMonthAnimeModel : IMonthAnimeComponent {
    override suspend fun getMonthAnimeData(partUrl: String): Pair<ArrayList<AnimeCoverBean>, PageNumberBean?> {
        val monthAnimeList: ArrayList<AnimeCoverBean> = ArrayList()
        val url = CustomConst.host + partUrl
        val document = JsoupUtil.getDocument(url)
        val areaElements: Elements = document.getElementsByClass("area")
        for (i in areaElements.indices) {
            val areaChildren: Elements = areaElements[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "lpic" -> {
                        monthAnimeList.addAll(ParseHtmlUtil.parseLpic(areaChildren[j], url))
                    }
                }
            }
        }
        return Pair(monthAnimeList, null)
    }
}