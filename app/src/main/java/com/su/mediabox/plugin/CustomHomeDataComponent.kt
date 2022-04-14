package com.su.mediabox.plugin

import android.graphics.Typeface
import android.util.Log
import com.su.mediabox.pluginapi.Constant
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.been.AnimeShowBean
import com.su.mediabox.pluginapi.been.TabBean
import com.su.mediabox.pluginapi.v2.action.CustomDataAction
import com.su.mediabox.pluginapi.v2.action.DetailAction
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.pluginapi.v2.components.IHomeDataComponent
import org.jsoup.select.Elements
import java.lang.StringBuilder

class CustomHomeDataComponent : IHomeDataComponent {

    override suspend fun getData(page: Int): List<BaseData>? {
        if (page != 1)
            return null
        val url = CustomConst.host
        val doc = JsoupUtil.getDocument(url)
        val data = mutableListOf<BaseData>()

        //一周排行榜
        val weekRank =
            doc.getElementsByClass("pics")
                .first()?.let {
                    object : ViewPagerData.PageLoader {
                        override fun pageName(page: Int): String {
                            return "一周排行"
                        }

                        override suspend fun loadData(page: Int): List<BaseData> {
                            return ParseHtmlUtil.parseSearchEm(it, url)
                        }
                    }
                }
        //动漫排行榜
        val totalRank = object : ViewPagerData.PageLoader {
            override fun pageName(page: Int): String {
                return "动漫排行榜"
            }

            override suspend fun loadData(page: Int): List<BaseData> {
                return getTotalRankData()
            }
        }
        data.add(TextData("🏅排行榜", fontSize = 20F, fontStyle = Typeface.BOLD).apply {
            paddingTop = 16.dp
            action = CustomDataAction.obtain("排行榜", object : CustomDataAction.Loader {
                override suspend fun loadData(page: Int): List<BaseData>? {
                    if (page != 1)
                        return null
                    return listOf(ViewPagerData(mutableListOf(weekRank!!, totalRank)))
                }
            })
        })

        //横幅
        doc.getElementsByClass("foucs bg").first()?.apply {
            val bannerItems = mutableListOf<BannerData.BannerItemData>()
            for (em in children()) {
                when (em.className()) {
                    "hero-wrap" -> {
                        em.select("[class=heros]").select("li").forEach { bannerItem ->
                            val nameEm = bannerItem.select("p").first()
                            val ext = StringBuilder()
                            bannerItem.getElementsByTag("em").first()?.text()?.also {
                                ext.append(it)
                            }
                            nameEm?.getElementsByTag("span")?.text()?.also {
                                ext.append(" ").append(it)
                            }
                            val videoUrl = bannerItem.getElementsByTag("a").first()?.attr("href")
                            val bannerImage = bannerItem.select("img").attr("src")
                            if (bannerImage.isNotBlank()) {
                                Log.d("添加横幅项", "封面：$bannerImage 链接：$videoUrl")
                                bannerItems.add(
                                    BannerData.BannerItemData(
                                        bannerImage, nameEm?.ownText() ?: "", ext.toString(), 8.dp
                                    ).apply {
                                        if (!videoUrl.isNullOrBlank())
                                            action = DetailAction.obtain(videoUrl)
                                    }
                                )
                            }
                        }
                        break
                    }
                }
            }
            if (bannerItems.isNotEmpty())
                data.add(BannerData(bannerItems).apply {
                    paddingTop = 0
                })
        }

        //分离推荐
        val types = doc.getElementsByClass("firs l").first() ?: return null
        for (em in types.children()) {
            Log.d("元素", em.className())
            when (em.className()) {
                //分类
                "dtit" -> {
                    val typeName = em.select("h2").text()
                    if (!typeName.isNullOrBlank()) {
                        data.add(TextData(typeName, fontSize = 18F, fontStyle = Typeface.BOLD))
                        Log.d("视频分类", typeName)
                    }
                }
                //分类下的视频
                "img" -> {
                    val typeVideos = mutableListOf<VideoGridItemData>()
                    for (video in em.select("li")) {
                        video.getElementsByClass("tname").first()?.select("a")?.first()?.apply {
                            val name = text()
                            val videoUrl = attr("href")
                            val coverUrl = video.select("img").first()?.attr("src")
                            val episode = video.select("[target]").first()?.text()

                            if (!name.isNullOrBlank() && !videoUrl.isNullOrBlank() && !coverUrl.isNullOrBlank()) {
                                typeVideos
                                    .add(VideoGridItemData(name, coverUrl, videoUrl, episode ?: "")
                                        .apply {
                                            action = DetailAction.obtain(videoUrl)
                                        })
                                Log.d("添加视频", "($name) ($videoUrl) ($coverUrl) ($episode)")
                            }
                        }
                    }
                    if (!typeVideos.isNullOrEmpty()) {
                        data.add(GridData(typeVideos))
                        Log.d("视频数量", "${typeVideos.size}")
                    }
                }
            }
        }

        return data
    }

    private suspend fun getTotalRankData(): List<BaseData> {
        val const = CustomConst
        val document = JsoupUtil.getDocument(const.host + const.ANIME_RANK)
        val areaChildren: Elements = document.select("[class=area]")[0].children()
        val rankList = mutableListOf<BaseData>()
        for (i in areaChildren.indices) {
            when (areaChildren[i].className()) {
                "topli" -> {
                    rankList.addAll(ParseHtmlUtil.parseTopli(areaChildren[i]))
                }
            }
        }
        return rankList
    }
}