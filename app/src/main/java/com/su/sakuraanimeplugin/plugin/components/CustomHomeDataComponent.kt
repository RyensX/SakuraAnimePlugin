package com.su.sakuraanimeplugin.plugin.components

import android.graphics.Typeface
import android.util.Log
import android.view.Gravity
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.action.CustomPageAction
import com.su.mediabox.pluginapi.v2.action.DetailAction
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.pluginapi.v2.components.IHomeDataComponent
import com.su.sakuraanimeplugin.plugin.util.JsoupUtil
import java.lang.StringBuilder

class CustomHomeDataComponent : IHomeDataComponent {

    override suspend fun getData(page: Int): List<BaseData>? {
        if (page != 1)
            return null
        val url = CustomConst.host
        val doc = JsoupUtil.getDocument(url)
        val data = mutableListOf<BaseData>()

        //1.排行榜
        data.add(
            SimpleTextData("🏅排行榜").apply {
                spanSize = 4
                fontSize = 16F
                fontStyle = Typeface.BOLD
                gravity = Gravity.CENTER
                paddingTop = 4.dp
                paddingBottom = 4.dp
                action = CustomPageAction.obtain(RankPageDataComponent::class.java)
            })

        //2.更新表
        data.add(
            SimpleTextData("📅更新表").apply {
                spanSize = 4
                fontSize = 16F
                fontStyle = Typeface.BOLD
                gravity = Gravity.CENTER
                paddingTop = 4.dp
                paddingBottom = 4.dp
                action = CustomPageAction.obtain(UpdateTablePageDataComponent::class.java)
            })

        //3.横幅
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
                                        bannerImage, nameEm?.ownText() ?: "", ext.toString()
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
                data.add(BannerData(bannerItems, 6.dp).apply {
                    paddingTop = 0
                    paddingBottom = 6.dp
                })
        }

        //4.各类推荐
        val types = doc.getElementsByClass("firs l").first() ?: return null
        for (em in types.children()) {
            Log.d("元素", em.className())
            when (em.className()) {
                //分类
                "dtit" -> {
                    val typeName = em.select("h2").text()
                    if (!typeName.isNullOrBlank()) {
                        data.add(SimpleTextData(typeName).apply {
                            fontSize = 18F
                            fontStyle = Typeface.BOLD
                        })
                        Log.d("视频分类", typeName)
                    }
                }
                //分类下的视频
                "img" -> {
                    for (video in em.select("li")) {
                        video.getElementsByClass("tname").first()?.select("a")?.first()?.apply {
                            val name = text()
                            val videoUrl = attr("href")
                            val coverUrl = video.select("img").first()?.attr("src")
                            val episode = video.select("[target]").first()?.text()

                            if (!name.isNullOrBlank() && !videoUrl.isNullOrBlank() && !coverUrl.isNullOrBlank()) {
                                data.add(VideoGridItemData(name, coverUrl, videoUrl, episode ?: "")
                                    .apply {
                                        action = DetailAction.obtain(videoUrl)
                                    })
                                Log.d("添加视频", "($name) ($videoUrl) ($coverUrl) ($episode)")
                            }
                        }
                    }
                }
            }
        }

        return data
    }
}