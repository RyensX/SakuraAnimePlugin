package com.su.mediabox.plugin

import android.graphics.Color
import android.graphics.Typeface
import android.util.Log
import com.su.mediabox.pluginapi.UI.dp
import com.su.mediabox.pluginapi.v2.action.DetailAction
import com.su.mediabox.pluginapi.v2.been.*
import com.su.mediabox.pluginapi.v2.components.IHomeDataComponent

class CustomHomeDataComponent : IHomeDataComponent {

    override suspend fun getData(page: Int): List<BaseData>? {
        if (page != 1)
            return null
        val url = CustomConst.host
        val doc = JsoupUtil.getDocument(url)
        val types = doc.getElementsByClass("firs l").first() ?: return null
        val data = mutableListOf<BaseData>()

        //TODO 由于还没轮播图视图组件所以暂未提供轮播图解析

        for (em in types.children()) {
            Log.d("元素", em.className())
            when (em.className()) {
                //分类
                "dtit" -> {
                    val typeName = em.select("h2").text()
                    if (!typeName.isNullOrBlank()) {
                        data.add(
                            TextData(
                                typeName,
                                fontSize = 18F,
                                fontStyle = Typeface.BOLD,
                                paddingLeft = 16.dp,
                                paddingRight = 16.dp
                            )
                        )
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
}