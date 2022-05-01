package com.su.sakuraanimeplugin.plugin.deprecateds

import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.IRankListComponent
import com.su.sakuraanimeplugin.plugin.components.CustomConst
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.ParseHtmlUtil
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomRankListModel : IRankListComponent {
    private var bgTimes = 0
    var rankList: MutableList<AnimeCoverBean> = ArrayList()

    override suspend fun getRankListData(partUrl: String): Pair<List<AnimeCoverBean>, PageNumberBean?> {
        rankList.clear()
        if (partUrl == "/" || partUrl == "") getWeekRankData()
        else getAllRankData(partUrl)
        return Pair(rankList, null)
    }

    private suspend fun getAllRankData(partUrl: String) {
        val const = CustomConst
        val document = JsoupUtil.getDocument(CustomConst.host + CustomConst.ANIME_RANK)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "topli" -> {
                    rankList.addAll(ParseHtmlUtil.parseTopli(areaChildren[i], ""))
                }
            }
        }
    }

    private suspend fun getWeekRankData() {
        bgTimes = 0
        val url = CustomConst.host
        val document = JsoupUtil.getDocument(url)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "side r" -> {
                    val sideRChildren = areaChildren[i].children()
                    for (j in sideRChildren.indices) {
                        when (sideRChildren[j].className()) {
                            "bg" -> {
                                if (bgTimes++ == 0) continue

                                val bgChildren = sideRChildren[j].children()
                                for (k in bgChildren.indices) {
                                    when (bgChildren[k].className()) {
                                        "pics" ->
                                            rankList.addAll(ParseHtmlUtil.parsePics(bgChildren[k]))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
