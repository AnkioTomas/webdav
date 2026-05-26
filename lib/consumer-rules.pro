# Ankio WebDAV — consumer ProGuard/R8 rules
# 随 AAR 合并到宿主 release 构建，开启 minifyEnabled 时自动生效。

# ---------- WebDAV 对外 API ----------
-keep class net.ankio.webdav.lib.** { *; }
-keep interface net.ankio.webdav.lib.** { *; }
-keep enum net.ankio.webdav.lib.** { *; }

# Compose 设置页（顶层 @Composable 编译为 *Kt）
-keepclassmembers class net.ankio.webdav.lib.ui.** {
    @androidx.compose.runtime.Composable <methods>;
}

# ---------- Ankio Theme（api 传递，配置页 / Toast / 主题依赖）----------
-keep class net.ankio.theme.** { *; }
-keep interface net.ankio.theme.** { *; }
-keep enum net.ankio.theme.** { *; }
-dontwarn net.ankio.theme.**

# ThemeToast 悬浮窗 Compose 生命周期
-keep class net.ankio.theme.compose.OverlayLifecycleOwner { *; }

# Miuix（Theme api 传递）
-keep class top.yukonga.miuix.** { *; }
-dontwarn top.yukonga.miuix.**

# Material 动态色
-keep class com.google.android.material.color.** { *; }
-dontwarn com.google.android.material.color.**

# ---------- Sardine / OkHttp / SimpleXML ----------
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.xml.stream.**
-dontwarn org.simpleframework.xml.**
-dontwarn org.xmlpull.v1.**

-keep class com.thegrizzlylabs.sardineandroid.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }
-keep class org.simpleframework.xml.** { *; }
-keepattributes ElementList,Root,Default

# ---------- Kotlin / 协程 / 堆栈 ----------
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.**

-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.** {
    volatile <fields>;
}
-dontwarn kotlinx.coroutines.**

-keepattributes Signature,InnerClasses,EnclosingMethod,Exceptions,*Annotation*,RuntimeVisibleAnnotations
