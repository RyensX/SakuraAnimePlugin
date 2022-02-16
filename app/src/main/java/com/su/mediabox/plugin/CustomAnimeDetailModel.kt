package com.su.mediabox.plugin

import com.su.mediabox.plugin.ParseHtmlUtil.parseBotit
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text.buildRouteActionUrl
import com.su.mediabox.pluginapi.been.AnimeDetailBean
import com.su.mediabox.pluginapi.been.AnimeInfoBean
import com.su.mediabox.pluginapi.been.AnimeTypeBean
import com.su.mediabox.pluginapi.been.IAnimeDetailBean
import com.su.mediabox.pluginapi.components.IAnimeDetailComponent
import org.jsoup.select.Elements

@Deprecated("将在下一个Release重新整理实现")
class CustomAnimeDetailModel : IAnimeDetailComponent {
    override suspend fun getAnimeDetailData(
        partUrl: String
    ): Triple<String, String, ArrayList<IAnimeDetailBean>> {
        val animeDetailList: ArrayList<IAnimeDetailBean> = ArrayList()
        var cover = ""
        var title = ""
        val url = CustomConst.host + partUrl
        val document = JsoupUtil.getDocument(url)
        //番剧头部信息
        val area: Elements = document.getElementsByClass("area")
        for (i in area.indices) {
            val areaChildren = area[i].children()
            for (j in areaChildren.indices) {
                when (areaChildren[j].className()) {
                    "fire l" -> {
                        var alias = ""
                        var info = ""
                        var year = ""
                        var index = ""
                        var animeArea = ""
                        val animeType: MutableList<AnimeTypeBean> = ArrayList()
                        val tag: MutableList<AnimeTypeBean> = ArrayList()

                        val fireLChildren =
                            areaChildren[j].select("[class=fire l]")[0].children()
                        for (k in fireLChildren.indices) {
                            when (fireLChildren[k].className()) {
                                "thumb l" -> {
                                    cover = fireLChildren[k]
                                        .select("img").attr("src")
                                }
                                "rate r" -> {   //其他信息，如标签、地区等
                                    val rateR = fireLChildren[k]
                                    title = rateR.select("h1").text()
                                    val sinfo: Elements = rateR.select("[class=sinfo]")
                                    val span: Elements = sinfo.select("span")
                                    val p: Elements = sinfo.select("p")
                                    if (p.size == 1) {
                                        alias = p[0].text()
                                    } else if (p.size == 2) {
                                        alias = p[0].text()
                                        info = p[1].text()
                                    }
                                    year = span[0].text()
                                    animeArea = span[1].select("a").text()
                                    index = span[3].select("a").text()
                                    //类型
                                    val typeElements: Elements = span[2].select("a")
                                    for (l in typeElements.indices) {
                                        animeType.add(
                                            AnimeTypeBean(
                                                "",
                                                buildRouteActionUrl(
                                                    Constant.ActionUrl.ANIME_CLASSIFY,
                                                    typeElements[l].attr("href"),
                                                    "",
                                                    typeElements[l].text()
                                                ),
                                                CustomConst.host + typeElements[l].attr("href"),
                                                typeElements[l].text()
                                            )
                                        )
                                    }
                                    //标签
                                    val tagElements: Elements = span[4].select("a")
                                    for (l in tagElements.indices) {
                                        tag.add(
                                            AnimeTypeBean(
                                                "",
                                                buildRouteActionUrl(
                                                    Constant.ActionUrl.ANIME_CLASSIFY,
                                                    tagElements[l].attr("href"),
                                                    "",
                                                    tagElements[l].text()
                                                ),
                                                CustomConst.host + tagElements[l].attr("href"),
                                                tagElements[l].text()
                                            )
                                        )
                                    }
                                }
                                "tabs", "tabs noshow" -> {     //播放列表+header
                                    animeDetailList.add(
                                        AnimeDetailBean(
                                            Constant.ViewHolderTypeString.HEADER_1, "",
                                            fireLChildren[k].select("[class=menu0]")
                                                .select("li").text(),
                                            "", null
                                        )
                                    )

                                    animeDetailList.add(
                                        AnimeDetailBean(
                                            Constant.ViewHolderTypeString.HORIZONTAL_RECYCLER_VIEW_1,
                                            "",
                                            "",
                                            "",
                                            ParseHtmlUtil.parseMovurls(
                                                fireLChildren[k].select("[class=main0]")
                                                    .select("[class=movurl]")[0]
                                            )
                                        )
                                    )
                                }
                                "botit" -> {     //其它header
                                    animeDetailList.add(
                                        AnimeDetailBean(
                                            Constant.ViewHolderTypeString.HEADER_1, "",
                                            parseBotit(fireLChildren[k]),
                                            "", null
                                        )
                                    )
                                }
                                "dtit" -> {     //其它header
                                    animeDetailList.add(
                                        AnimeDetailBean(
                                            Constant.ViewHolderTypeString.HEADER_1, "",
                                            ParseHtmlUtil.parseDtit(fireLChildren[k]),
                                            "", null
                                        )
                                    )
                                }
                                "info" -> {         //动漫介绍
                                    animeDetailList.add(
                                        AnimeDetailBean(
                                            Constant.ViewHolderTypeString.ANIME_DESCRIBE_1, "",
                                            "",
                                            fireLChildren[k].select("[class=info]").text(),
                                            null
                                        )
                                    )
                                }
                                "img" -> {         //系列动漫推荐
                                    animeDetailList.addAll(
                                        ParseHtmlUtil.parseImg(fireLChildren[k], url)
                                    )
                                }
                            }
                        }
                        val animeInfoBean = AnimeInfoBean(
                            "", "",
                            title, cover, alias, animeArea, year, index, animeType, tag, info
                        )
                        animeDetailList.add(
                            0,
                            AnimeDetailBean(
                                Constant.ViewHolderTypeString.ANIME_INFO_1, "", "", "",
                                headerInfo = animeInfoBean
                            )
                        )
                    }
                }
            }
        }
        return Triple(cover, title, animeDetailList)
    }
}