package com.su.sakuraanimeplugin.plugin.deprecateds

import com.su.mediabox.pluginapi.been.TabBean
import com.su.mediabox.pluginapi.components.IRankComponent
import com.su.sakuraanimeplugin.plugin.components.CustomConst
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import com.su.sakuraanimeplugin.plugin.util.ParseHtmlUtil
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomRankModel : IRankComponent {
    private var bgTimes = 0
    private var tabList: ArrayList<TabBean> = ArrayList()

    override suspend fun getRankTabData(): ArrayList<TabBean> {
        tabList.clear()
        getWeekRankData()
        getAllRankData()
        return tabList
    }

    private suspend fun getAllRankData() {
        val const = CustomConst
        val document = JsoupUtil.getDocument(CustomConst.host + CustomConst.ANIME_RANK)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "gohome" -> {
                    tabList.add(
                        tabList.size, TabBean(
                            "",
                            CustomConst.ANIME_RANK,
                            "",
                            areaChildren[i].select("h1").select("a").text()
                        )
                    )
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
                                        "dtit" -> {
                                            tabList.add(
                                                0,
                                                TabBean(
                                                    "",
                                                    "/",
                                                    "",
                                                    ParseHtmlUtil.parseDtit(bgChildren[k])
                                                )
                                            )
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
}
