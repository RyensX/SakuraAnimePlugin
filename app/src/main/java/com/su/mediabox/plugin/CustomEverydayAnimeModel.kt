package com.su.mediabox.plugin

import com.su.mediabox.pluginapi.been.AnimeCoverBean
import com.su.mediabox.pluginapi.been.TabBean
import com.su.mediabox.pluginapi.components.IEverydayAnimeComponent
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomEverydayAnimeModel : IEverydayAnimeComponent {
    override suspend fun getEverydayAnimeData(): Triple<ArrayList<TabBean>, ArrayList<List<AnimeCoverBean>>, String> {
        val tabList = ArrayList<TabBean>()
        var header = ""
        val everydayAnimeList: ArrayList<List<AnimeCoverBean>> = ArrayList()
        val document = JsoupUtil.getDocument(CustomConst.host)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "side r" -> {
                    val sideRChildren = areaChildren[i].children()
                    out@ for (j in sideRChildren.indices) {
                        when (sideRChildren[j].className()) {
                            "bg" -> {
                                val bgChildren = sideRChildren[j].children()
                                for (k in bgChildren.indices) {
                                    when (bgChildren[k].className()) {
                                        "dtit" -> {
                                            header = ParseHtmlUtil.parseDtit(bgChildren[k])
                                        }
                                        "tag" -> {
                                            val tagChildren = bgChildren[k].children()
                                            for (l in tagChildren.indices) {
                                                tabList.add(
                                                    TabBean(
                                                        "",
                                                        "",
                                                        "",
                                                        tagChildren[l].text()
                                                    )
                                                )
                                            }
                                        }
                                        "tlist" -> {
                                            everydayAnimeList.addAll(
                                                ParseHtmlUtil.parseTlist(
                                                    bgChildren[k]
                                                )
                                            )
                                        }
                                    }
                                }
                                break@out
                            }
                        }
                    }
                }
            }
        }
        return Triple(tabList, everydayAnimeList, header)
    }
}