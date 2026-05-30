# Ankio WebDAV

Android WebDAV 客户端库：轻量 Kotlin API、Compose 配置页、连接测试。底层基于 [sardine-android](https://github.com/thegrizzlylabs/sardine-android)，仅在 `SardineWebDavEngine` 包装层接触第三方实现。

[![JitPack](https://jitpack.io/v/AnkioTomas/webdav.svg)](https://jitpack.io/#AnkioTomas/webdav)

## 特性

- **WebDavClient**：`list` / `read` / `write` / `delete` / `mkdir` / `mkdirs` / `move` / `copy` 等，协程 + `Dispatchers.IO`
- **WebDavSettingsScreen**：基于 [Ankio Theme](https://github.com/AnkioTomas/theme) `1.1.5+` 的设置 UI（`ThemeSettingTextField` + `SettingInputMode`）；内部管理输入、测试与 `WebDavConfigStore` 持久化，可选 `onSaved` 接收已保存配置；文案支持 `values` / `values-zh`
- **WebDavConfigStore**：SharedPreferences 持久化，`OnConfigSaved` 监听
- **目录列表**：过滤 `.` / `..` 与当前目录项；文件夹识别增强（路径尾 `/`、`httpd/unix-directory` 等）
- **R8**：`consumer-rules.pro` 随 AAR 合并，覆盖 WebDAV API、Theme、Miuix、Sardine / OkHttp / SimpleXML

## 环境要求

| 项 | 值 |
|---|---|
| minSdk | 30 |
| Java | 17 |
| Compose | BOM 2026.05+ |
| Theme | `com.github.AnkioTomas:theme:1.1.5+`（`lib` 以 `api` 传递） |

## 接入

### 1. 添加 JitPack 仓库

`settings.gradle.kts`：

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
}
```

### 2. 添加依赖

```kotlin
dependencies {
    implementation("com.github.AnkioTomas:webdav:1.0.0")
}
```

将 `1.0.0` 替换为 [JitPack](https://jitpack.io/#AnkioTomas/webdav) 上对应 **Git Tag**。

### 3. 初始化

在 `Application.onCreate` 中调用（初始化 Theme 与 ThemeToast，配置页依赖此项）：

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        WebDav.init(this)
    }
}
```

### 4. 配置页

`WebDavSettingsScreen`：页面为**单张设置卡片**（服务器、账号、密码、操作按钮、测试结果区）。输入与测试状态、以及点击「保存」或连接测试成功时的配置写入，均在组件内部完成。

`state` 为可选：
- 传入 `state`：按传入值作为初始状态。
- 不传 `state`：组件内部自动从 `WebDavConfigStore.load(context)` 读取初始值。

写入成功后可选 `onSaved(config)`，便于宿主刷新界面或联动逻辑（全局监听仍可用 `WebDavConfigStore.addOnSaveListener`）。

```kotlin
WebDavSettingsScreen(
    onSaved = { config ->
        // 可选：配置已写入 SharedPreferences，与 Store 监听一并触发
    },
)
```

| 参数 | 说明 |
|------|------|
| `state` | 可选；传入时使用传入值作为初始输入，不传时内部自动从 `WebDavConfigStore` 读取 |
| `modifier` | 可选；布局修饰 |
| `onSaved` | 可选；内部 `WebDavConfigStore.save` 完成后回调，参数为刚持久化的 `WebDavConfig` |

### 5. 客户端 API

```kotlin
val client = WebDav.client(context) // 从 WebDavConfigStore 读取配置

// 目录
val items: List<WebDavResource> = client.list("photos")
client.mkdir("backup")
client.mkdirs("a/b/c")

// 读写
client.writeText("demo/hello.txt", "hello")
val text = client.readText("demo/hello.txt")
val bytes = client.readBytes("demo/data.bin")
client.open("large.bin").use { stream -> /* ... */ }

// 其它
client.exists("demo")
client.delete("demo/hello.txt")
client.move("a.txt", "b.txt")
client.copy("a.txt", "copy/a.txt")
```

也可直接传入配置：

```kotlin
val client = WebDav.client(WebDavConfig(serverUrl, username, password))
```

所有 `WebDavClient` 方法均为 `suspend`，请在协程中调用。

### 6. 配置持久化与监听

```kotlin
WebDavConfigStore.addOnSaveListener { context, config ->
    // 任意处 save() 后都会触发
}
WebDavConfigStore.save(context, config)
val config = WebDavConfigStore.load(context)
```

默认服务器：`https://dav.jianguoyun.com/dav/`。

### 7. HTTP 明文

`WebDavConfig.isCleartextHttp` 判断是否为 `http://`。库不配置网络安全策略；使用 HTTP 时宿主需自行设置 `networkSecurityConfig` / `usesCleartextTraffic`（见 `app` Demo）。

### 8. Release 混淆

`lib` 通过 `consumerProguardFiles` 提供 `consumer-rules.pro`，宿主 `minifyEnabled true` 时自动合并，一般**无需**再手写 Theme / Miuix / Sardine 规则。

已覆盖范围：

| 类别 | 说明 |
|------|------|
| `net.ankio.webdav.lib.**` | 对外 API 与 Compose 设置页 |
| `net.ankio.theme.**` | Theme、ThemeToast 悬浮窗 |
| `top.yukonga.miuix.**` | Theme 传递的 Miuix 组件 |
| Sardine / OkHttp / SimpleXML | WebDAV 底层 |

若仍有 R8 报错，可参考 `lib/consumer-rules.pro` 与 Demo 的 `app/proguard-rules.pro`。

## Demo

`:app` 模块示例：双 Tab（设置 / 文件）、面包屑、上传测试文件、MediaStore 下载、删除；Release 已开启混淆验证。

```bash
./gradlew :app:assembleDebug
./gradlew :app:assembleRelease   # 验证 R8
```

## 模块结构

```
webdav/
├── lib/
│   ├── src/main/          # 发布代码（WebDavClient、WebDavSettingsScreen 等）
│   └── src/debug/         # Compose Preview（不进入 release AAR）
└── app/                   # Demo
```


## License

Apache License 2.0
