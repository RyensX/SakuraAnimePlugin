package com.su.mediabox.plugin

import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text
import com.su.mediabox.pluginapi.been.AnimeShowBean
import com.su.mediabox.pluginapi.been.IAnimeShowBean
import com.su.mediabox.pluginapi.been.PageNumberBean
import com.su.mediabox.pluginapi.components.IAnimeShowComponent
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomAnimeShowModel : IAnimeShowComponent {
    override suspend fun getAnimeShowData(
        partUrl: String
    ): Pair<ArrayList<IAnimeShowBean>, PageNumberBean?> {
        val url = CustomConst.host + partUrl
        var pageNumberBean: PageNumberBean? = null
        val document = JsoupUtil.getDocument(url)
        val animeShowList: ArrayList<IAnimeShowBean> = ArrayList()
        //banner
        val foucsBgElements: Elements = document.getElementsByClass("foucs bg")
        for (i in foucsBgElements.indices) {
            val foucsBgChildren: Elements = foucsBgElements[i].children()
            for (j in foucsBgChildren.indices) {
                when (foucsBgChildren[j].className()) {
                    "hero-wrap" -> {
                        animeShowList.add(
                            AnimeShowBean(
                                Constant.ViewHolderTypeString.BANNER_1, "",
                                "", "", "", null, "",
                                ParseHtmlUtil.parseHeroWrap(foucsBgChildren[j])
                            )
                        )
                    }
                }
            }
        }
        //area
        var area: Elements = document.getElementsByClass("area")
        if (partUrl == "/") //首页，有右边栏
            area = document.getElementsByClass("area").select("[class=firs l]")
        for (i in area.indices) {
            val elements: Elements = area[i].children()
            for (j in elements.indices) {
                when (elements[j].className()) {
                    "dtit" -> {
                        val a = elements[j].select("h2").select("a")
                        if (a.size == 0) {      //只有一个标题
                            animeShowList.add(
                                AnimeShowBean(
                                    Constant.ViewHolderTypeString.HEADER_1,
                                    "",
                                    "",
                                    elements[j].select("h2").text(),
                                    "",
                                    null,
                                    ""
                                )
                            )
                        } else {        //有右侧“更多”
                            animeShowList.add(
                                AnimeShowBean(
                                    Constant.ViewHolderTypeString.HEADER_1,
                                    Text.buildRouteActionUrl(
                                        Constant.ActionUrl.ANIME_DETAIL,
                                        a.attr("href")
                                    ),
                                    CustomConst.host + a.attr("href"),
                                    a.text(),
                                    elements[j].select("span").select("a").text(),
                                    null,
                                    ""
                                )
                            )
                        }
                    }
                    "img", "imgs" -> {
                        animeShowList.addAll(
                            ParseHtmlUtil.parseImg(
                                elements[j],
                                url
                            )
                        )
                    }
                    "fire l" -> {       //右侧前半tab内容
                        val firsLChildren = elements[j].children()
                        for (k in firsLChildren.indices) {
                            when (firsLChildren[k].className()) {
                                "lpic" -> {
                                    animeShowList.addAll(
                                        ParseHtmlUtil.parseLpic(
                                            firsLChildren[k],
                                            url
                                        )
                                    )
                                }
                                "pages" -> {
                                    pageNumberBean =
                                        ParseHtmlUtil.parseNextPages(
                                            firsLChildren[k]
                                        )
                                }
                            }
                        }
                    }
                    "dnews" -> {       //右侧后半tab内容，cover4
                        animeShowList.addAll(
                            ParseHtmlUtil.parseDnews(
                                elements[j],
                                url
                            )
                        )
                    }
                    "topli" -> {       //右侧后半tab内容，cover5
                        animeShowList.addAll(
                            ParseHtmlUtil.parseTopli(
                                elements[j], ""
                            )
                        )
                    }
                    "pages" -> {
                        pageNumberBean =
                            ParseHtmlUtil.parseNextPages(
                                elements[j]
                            )
                    }
                }
            }
        }
        return Pair(animeShowList, pageNumberBean)
    }
}