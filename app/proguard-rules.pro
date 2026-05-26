# Demo App — R8 / ProGuard
# lib 模块的 consumer-rules.pro 会在打包时自动合并

# Compose（保留行号便于崩溃定位）
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Kotlin
-keep class kotlin.Metadata { *; }
-dontwarn kotlin.reflect.**

# 入口
-keep class net.ankio.webdav.demo.WebDavApp { *; }
-keep class net.ankio.webdav.demo.MainActivity { *; }

# ViewModel
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# theme 库 Toast（反射/Compose）
-keep class net.ankio.theme.** { *; }
-dontwarn net.ankio.theme.**
