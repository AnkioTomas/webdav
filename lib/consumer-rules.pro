# WebDAV 库对外 API
-keep class net.ankio.webdav.lib.WebDav { *; }
-keep class net.ankio.webdav.lib.WebDavClient { *; }
-keep class net.ankio.webdav.lib.WebDavConfig { *; }
-keep class net.ankio.webdav.lib.WebDavResource { *; }
-keep class net.ankio.webdav.lib.WebDavConfigStore { *; }
-keep class net.ankio.webdav.lib.WebDavTest { *; }
-keep class net.ankio.webdav.lib.WebDavPaths { *; }
-keep class net.ankio.webdav.lib.WebDavListing { *; }
-keep class net.ankio.webdav.lib.WebDavTestResult { *; }
-keep class net.ankio.webdav.lib.WebDavTestResult$* { *; }
-keep class net.ankio.webdav.lib.ui.WebDavSettingsState { *; }
-keep class net.ankio.webdav.lib.ui.WebDavTestUiState { *; }
-keep class net.ankio.webdav.lib.ui.WebDavTestUiState$* { *; }

# Sardine / OkHttp（WebDAV 底层）
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.xml.stream.**
-dontwarn org.simpleframework.xml.**
-dontwarn org.xmlpull.v1.**
-keep class com.thegrizzlylabs.sardineandroid.** { *; }
-keep class okhttp3.** { *; }
-keep class okio.** { *; }

# 堆栈与泛型
-keepattributes Signature
-keepattributes *Annotation*
