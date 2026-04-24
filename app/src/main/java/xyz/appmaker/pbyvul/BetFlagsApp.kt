package xyz.appmaker.pbyvul

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.util.DebugLogger
import xyz.appmaker.pbyvul.util.resolveApplicationUserAgent
import dagger.hilt.android.HiltAndroidApp
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

@HiltAndroidApp
class BetFlagsApp : Application(), ImageLoaderFactory {

    override fun newImageLoader(): ImageLoader {
        val httpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header("User-Agent", applicationContext.resolveApplicationUserAgent())
                    .header("Accept", "image/avif,image/webp,image/apng,image/*,*/*;q=0.8")
                    .build()
                chain.proceed(request)
            }
            .build()

        return ImageLoader.Builder(this)
            .okHttpClient(httpClient)
            .crossfade(true)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizePercent(0.05)
                    .build()
            }
            .apply {
                if (applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE != 0) {
                    logger(DebugLogger())
                }
            }
            .build()
    }
}
