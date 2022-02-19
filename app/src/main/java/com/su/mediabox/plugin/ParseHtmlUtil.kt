package com.su.mediabox.plugin

import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.util.ArrayList
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.Text.buildRouteActionUrl
import com.su.mediabox.pluginapi.been.*
import java.net.URL

object ParseHtmlUtil {

    fun parseIframeSrc(element: Element): String {
        return element.attr("src")
    }

    fun parseHeroWrap(      //banner
        element: Element,
        type: String = Constant.ViewHolderTypeString.ANIME_COVER_6
    ): List<AnimeCoverBean> {
        val list: MutableList<AnimeCoverBean> = ArrayList()
        val liElements: Elements = element.select("[class=heros]").select("li")
        for (i in liElements.indices) {
            var episodeTitle = ""
            var title = ""
            var describe = ""
            var url = ""
            var cover = ""
            val liChildren: Elements = liElements[i].children()
            for (j in liChildren.indices) {
                when (liChildren[j].tagName()) {
                    "a" -> {
                        url = liChildren[j].attr("href")
                        cover = liChildren[j].select("img").attr("src")
                        title = liChildren[j].select("p").first()!!.ownText()
                        describe = liChildren[j].select("p").select("span").text()
                    }
                    "em" -> {
                        episodeTitle = liChildren[j].text()
                    }
                }
            }
            list.add(
                AnimeCoverBean(
                    type,
                    buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, url),
                    CustomConst.host + url,
                    title, cover,
                    "", null, describe,
                    AnimeEpisodeDataBean("", "", episodeTitle),
                    null,
                    null
                )
            )
        }
        return list
    }

    fun parseTers(
        element: Element,
        type: String = Constant.ViewHolderTypeString.EMPTY_STRING
    ): List<ClassifyBean> {
        val list: MutableList<ClassifyBean> = ArrayList()
        val pElements: Elements = element.select("p")
        for (i in pElements.indices) {
            val pChildren: Elements = pElements[i].children()
            for (j in pChildren.indices) {
                when (pChildren[j].tagName()) {
                    "label" -> {
                        list.add(
                            ClassifyBean(
                                type,
                                "",
                                pChildren[j].text().replace(":", "").replace("：", ""),
                                ArrayList()
                            )
                        )
                    }
                    "a" -> {
                        if (list.size > 0) {
                            list[list.size - 1].classifyDataList.add(
                                ClassifyDataBean(
                                    "",
                                    //TODO
                                    pChildren[j].attr("href"),
                                    CustomConst.host + pChildren[j].attr("href"),
                                    pChildren[j].text()
                                )
                            )
                        }
                    }
                }
            }


        }
        return list
    }

    fun parseTlist(
        element: Element,
        type: String = Constant.ViewHolderTypeString.ANIME_COVER_5
    ): List<List<AnimeCoverBean>> {
        val ulList: MutableList<List<AnimeCoverBean>> = ArrayList()
        val ulElements: Elements = element.select("ul")
        for (i in ulElements.indices) {
            val liList: MutableList<AnimeCoverBean> = ArrayList()
            val liElements: Elements = ulElements[i].select("li")
            for (j in liElements.indices) {
                val episodeTitle = liElements[j].select("span").select("a").text()
                val title = liElements[j].select("a")[1].text()
                val url = liElements[j].select("a")[1].attr("href")
                val episodeUrl = liElements[j].select("span").select("a").attr("href")
                liList.add(
                    AnimeCoverBean(
                        type,
                        buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, url),
                        CustomConst.host + url,
                        title, null, "", null, null,
                        AnimeEpisodeDataBean(
                            "",
                            buildRouteActionUrl(
                                Constant.ActionUrl.ANIME_PLAY,
                                episodeUrl,
                                episodeUrl
                            ),
                            episodeTitle
                        ),
                        null,
                        null
                    )
                )
            }
            ulList.add(liList)
        }
        return ulList
    }

    fun parseTopli(
        element: Element,
        type: String = Constant.ViewHolderTypeString.ANIME_COVER_5
    ): List<AnimeCoverBean> {
        val animeShowList: MutableList<AnimeCoverBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            var url: String
            var title: String
            if (elements[i].select("a").size >= 2) {    //最近更新，显示地区的情况
                url = elements[i].select("a")[1].attr("href")
                title = elements[i].select("a")[1].text()
                if (elements[i].select("span")[0].children().size == 0) {     //最近更新，不显示地区的情况
                    url = elements[i].select("a")[0].attr("href")
                    title = elements[i].select("a")[0].text()
                }
            } else {                                            //总排行榜
                url = elements[i].select("a")[0].attr("href")
                title = elements[i].select("a")[0].text()
            }

            val areaUrl = elements[i].select("span").select("a")
                .attr("href")
            val areaTitle = elements[i].select("span").select("a").text()
            var episodeUrl = elements[i].select("b").select("a")
                .attr("href")
            val episodeTitle = elements[i].select("b").select("a").text()
            val date = elements[i].select("em").text()
            if (episodeUrl == "") {
                episodeUrl = url
            }
            animeShowList.add(
                AnimeCoverBean(
                    type,
                    buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, url),
                    CustomConst.host + url,
                    title, null, "", null, null,
                    AnimeEpisodeDataBean(
                        "",
                        buildRouteActionUrl(
                            Constant.ActionUrl.ANIME_PLAY,
                            episodeUrl,
                            CustomConst.host + url
                        ),
                        episodeTitle
                    ),
                    AnimeAreaBean("", areaUrl, CustomConst.host + areaUrl, areaTitle),
                    date
                )
            )
        }
        return animeShowList
    }

    fun parseDnews(
        element: Element,
        imageReferer: String,
        type: String = Constant.ViewHolderTypeString.ANIME_COVER_4
    ): List<AnimeCoverBean> {
        val animeShowList: MutableList<AnimeCoverBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            var cover = elements[i].select("a").select("img").attr("src")
            cover = getCoverUrl(
                cover,
                imageReferer
            )
            val title = elements[i].select("p").select("a").text()
            animeShowList.add(
                AnimeCoverBean(
                    type,
                    buildRouteActionUrl(Constant.ActionUrl.ANIME_MONTH_NEW_ANIME, url),
                    CustomConst.host + url,
                    title, cover, ""
                )
            )
        }
        return animeShowList
    }

    fun parsePics(      //一周动漫排行榜
        element: Element,
        type: String = Constant.ViewHolderTypeString.ANIME_COVER_3
    ): List<AnimeCoverBean> {
        val animeCover3List: MutableList<AnimeCoverBean> = ArrayList()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            val cover = results[i].select("a")
                .select("img").attr("src")
            val title = results[i].select("h2")
                .select("a").text()
            val url = results[i].select("h2")
                .select("a").attr("href")
            val episode = results[i].select("span")
                .select("font").text()
            val types = results[i].select("span")[1].select("a")
            val animeType: MutableList<AnimeTypeBean> = ArrayList()
            for (j in types.indices) {
                animeType.add(
                    AnimeTypeBean(
                        type,
                        buildRouteActionUrl(
                            Constant.ActionUrl.ANIME_CLASSIFY,
                            types[j].attr("href"),
                            "",
                            types[j].text()
                        ),
                        CustomConst.host + types[j].attr("href"),
                        types[j].text()
                    )
                )
            }
            val describe = results[i].select("p").text()
            animeCover3List.add(
                AnimeCoverBean(
                    type,
                    buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, url),
                    CustomConst.host + url,
                    title,
                    cover,
                    episode,
                    animeType,
                    describe
                )
            )
        }
        return animeCover3List
    }

    fun parseLpic(          //搜索
        element: Element,
        imageReferer: String,
        type: String = Constant.ViewHolderTypeString.ANIME_COVER_3
    ): List<AnimeCoverBean> {
        val animeCover3List: MutableList<AnimeCoverBean> = ArrayList()
        val results: Elements = element.select("ul").select("li")
        for (i in results.indices) {
            var cover = results[i].select("a").select("img").attr("src")
            cover = getCoverUrl(
                cover,
                imageReferer
            )
            val title = results[i].select("h2").select("a").attr("title")
            val url = results[i].select("h2").select("a").attr("href")
            val episode = results[i].select("span").select("font").text()
            val types = results[i].select("span")[1].select("a")
            val animeType: MutableList<AnimeTypeBean> = ArrayList()
            for (j in types.indices) {
                animeType.add(
                    AnimeTypeBean(
                        type,
                        buildRouteActionUrl(
                            Constant.ActionUrl.ANIME_CLASSIFY,
                            types[j].attr("href"),
                            "",
                            types[j].text()
                        ),
                        CustomConst.host + types[j].attr("href"),
                        types[j].text()
                    )
                )
            }
            val describe = results[i].select("p").text()
            animeCover3List.add(
                AnimeCoverBean(
                    type,
                    buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, url),
                    CustomConst.host + url,
                    title,
                    cover,
                    episode,
                    animeType,
                    describe
                )
            )
        }
        return animeCover3List
    }

    /**
     * 只获取下一页的地址，没有下一页则返回null
     */
    fun parseNextPages(
        element: Element,
        type: String = "pageNumber1"
    ): PageNumberBean? {
        val results: Elements = element.children()
        var findCurrentPage = false
        for (i in results.indices) {
            if (findCurrentPage) {
                if (results[i].className() == "a1") return null
                val url = results[i].attr("href")
                val title = results[i].text()
                return PageNumberBean(type, url, CustomConst.host + url, title)
            }
            if (results[i].tagName() == "span") findCurrentPage = true
        }
        return null
    }

    fun parseDtit(
        element: Element
    ): String {
        return element.children()[0].text()
    }

    fun parseBotit(
        element: Element
    ): String {
        return element.select("h2").text()
    }

    fun parseMovurls(
        element: Element,
        selected: AnimeEpisodeDataBean? = null,
        type: String = Constant.ViewHolderTypeString.ANIME_EPISODE_2
    ): List<AnimeEpisodeDataBean> {
        val animeEpisodeList: MutableList<AnimeEpisodeDataBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (k in elements.indices) {
            val episodeUrl = elements[k].select("a").attr("href")
            if (selected != null && elements[k].className() == "sel") {
                selected.title = elements[k].select("a").text()
                //FIX_TODO 2022/2/17 23:08 0 这里被直接用于存储播放地址了
                selected.actionUrl = episodeUrl
            }
            animeEpisodeList.add(
                AnimeEpisodeDataBean(
                    type,
                    buildRouteActionUrl(
                        Constant.ActionUrl.ANIME_PLAY,
                        episodeUrl
                    ),
                    elements[k].select("a").text()
                )
            )
        }
        return animeEpisodeList
    }

    //UP_TODO 2022/2/19 18:28 0 parseMovurls被详情页和播放页混同了，导致AnimeEpisodeDataBean::actionUrl没有按照路由使用，需要重新改造播放机制
    fun parsePlayMovurls(
        element: Element,
        selected: AnimeEpisodeDataBean? = null,
        type: String = Constant.ViewHolderTypeString.ANIME_EPISODE_2
    ): List<AnimeEpisodeDataBean> {
        val animeEpisodeList: MutableList<AnimeEpisodeDataBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (k in elements.indices) {
            val episodeUrl = elements[k].select("a").attr("href")
            if (selected != null && elements[k].className() == "sel") {
                selected.title = elements[k].select("a").text()
                //FIX_TODO 2022/2/17 23:08 0 这里被直接用于存储播放地址了
                selected.actionUrl = episodeUrl
            }
            animeEpisodeList.add(
                AnimeEpisodeDataBean(
                    type, episodeUrl,
                    elements[k].select("a").text()
                )
            )
        }
        return animeEpisodeList
    }

    fun parseImg(
        element: Element,
        imageReferer: String,
        type: String = Constant.ViewHolderTypeString.ANIME_COVER_1
    ): List<AnimeCoverBean> {
        val animeShowList: MutableList<AnimeCoverBean> = ArrayList()
        val elements: Elements = element.select("ul").select("li")
        for (i in elements.indices) {
            val url = elements[i].select("a").attr("href")
            var cover = elements[i].select("a").select("img").attr("src")
            cover = getCoverUrl(
                cover,
                imageReferer
            )
            val title = elements[i].select("[class=tname]").select("a").text()
            var episode = ""
            if (elements[i].select("p").size > 1) {
                episode = elements[i].select("p")[1].select("a").text()
            }
            animeShowList.add(
                AnimeCoverBean(
                    type,
                    buildRouteActionUrl(Constant.ActionUrl.ANIME_DETAIL, url),
                    CustomConst.host + url,
                    title, cover, episode
                )
            )
        }
        return animeShowList
    }

    fun getCoverUrl(cover: String, imageReferer: String): String {
        return when {
            cover.startsWith("//") -> {
                try {
                    "${URL(imageReferer).protocol}:$cover"
                } catch (e: Exception) {
                    e.printStackTrace()
                    cover
                }
            }
            cover.startsWith("/") -> {
                //url不全的情况
                CustomConst.host + cover
            }
            else -> cover
        }
    }
}