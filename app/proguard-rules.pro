# Demo App — R8 / ProGuard
# lib 模块 consumer-rules.pro 会自动合并（WebDAV / Theme / Sardine / Miuix）

# 崩溃堆栈行号
-keepattributes SourceFile,LineNumberTable
-renamesourcefileattribute SourceFile

# Demo 入口
-keep class net.ankio.webdav.demo.WebDavApp { *; }
-keep class net.ankio.webdav.demo.MainActivity { *; }

# ViewModel（反射构造）
-keep class * extends androidx.lifecycle.ViewModel {
    <init>(...);
}
-keep class * extends androidx.lifecycle.AndroidViewModel {
    <init>(android.app.Application);
}

# Compose Preview（仅 debug 存在，release 无害）
-dontwarn androidx.compose.ui.tooling.**
