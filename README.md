# Ankio WebDAV

Android WebDAV 客户端库：轻量 Kotlin API、Compose 配置页、连接测试。底层基于 [sardine-android](https://github.com/thegrizzlylabs/sardine-android)，仅在引擎包装层接触第三方实现。

[![JitPack](https://jitpack.io/v/AnkioTomas/webdav.svg)](https://jitpack.io/#AnkioTomas/webdav)

## 特性

- **WebDavClient**：`list` / `read` / `write` / `delete` / `mkdir` / `move` / `copy` 等常用操作，协程 + IO 调度
- **WebDavSettingsScreen**：基于 [Ankio Theme](https://github.com/AnkioTomas/theme) 的 Compose 配置 UI；连接测试成功后会自动调用 `onSave()`
- **WebDavConfigStore**：SharedPreferences 持久化，支持 `OnConfigSaved` 监听
- **目录列表**：过滤 `.` / `..` 与当前目录项，文件夹识别增强（路径尾 `/`、`httpd/unix-directory` 等）
- **R8**：`consumer-rules.pro` 已包含 Sardine / OkHttp 保留规则

## 环境要求

| 项 | 值 |
|---|---|
| minSdk | 30 |
| Java | 17 |
| Compose | BOM 2026.05+ |
| Theme | `com.github.AnkioTomas:theme:1.0.6+` |

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
    implementation("com.github.AnkioTomas.webdav:lib:1.0.0")
}
```

版本号与 [JitPack](https://jitpack.io/#AnkioTomas/webdav) 上的 **Git Tag** 一致（如 `1.0.0`）。

### 3. 初始化

在 `Application.onCreate` 中调用（会初始化 Theme 与 ThemeToast，配置页依赖此项）：

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        WebDav.init(this)
    }
}
```

### 4. 配置页

`WebDavSettingsScreen` 为受控组件，由宿主管理表单状态；**保存**与**测试成功**时都会触发 `onSave()`，请在回调里写入 `WebDavConfigStore`：

```kotlin
var serverUrl by rememberSaveable { mutableStateOf(saved.serverUrl) }
var username by rememberSaveable { mutableStateOf(saved.username) }
var password by rememberSaveable { mutableStateOf(saved.password) }
var testState by remember { mutableStateOf<WebDavTestUiState>(WebDavTestUiState.Idle) }

WebDavSettingsScreen(
    state = WebDavSettingsState(
        serverUrl = serverUrl,
        username = username,
        password = password,
        testState = testState,
    ),
    onServerChange = { serverUrl = it },
    onUsernameChange = { username = it },
    onPasswordChange = { password = it },
    onSave = {
        WebDavConfigStore.save(
            context,
            WebDavConfig(serverUrl, username, password),
        ) { _, config ->
            // 配置已持久化
        }
    },
    onTestStateChange = { testState = it },
    configValidator = { config ->
        // 可选：返回非 null 字符串则阻止保存/测试
        if (config.isCleartextHttp) "请使用 HTTPS" else null
    },
)
```

| 参数 | 说明 |
|------|------|
| `state` | `WebDavSettingsState`，含 `testState` |
| `onSave` | 用户点保存，或连接测试成功时调用 |
| `onTestStateChange` | 测试状态：`Idle` / `Running` / `Success` / `Failure` / `Saved` |
| `configValidator` | 保存/测试前校验，返回错误文案则中止 |

### 5. 客户端 API

```kotlin
val client = WebDav.client(context) // 读取 WebDavConfigStore 中的配置

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

默认服务器地址为坚果云 WebDAV：`https://dav.jianguoyun.com/dav/`。

### 7. HTTP 明文

`WebDavConfig.isCleartextHttp` 可判断是否为 `http://`。库本身不强制网络安全策略；若使用 HTTP，宿主 App 需自行配置 `networkSecurityConfig` / `usesCleartextTraffic`（参见本仓库 `app` 模块 Demo）。

### 8. Release 混淆

库已附带 `consumer-rules.pro`，一般无需额外配置。若仍有 R8 问题，可参考 `lib/consumer-rules.pro` 与 `app/proguard-rules.pro`。

## Demo

`:app` 模块提供完整示例：双 Tab（设置 / 文件浏览）、面包屑导航、上传测试文件、MediaStore 下载、删除等。本地运行：

```bash
./gradlew :app:assembleDebug
```

## 模块结构

```
webdav/
├── lib/     # 发布库（JitPack 产物 :lib）
└── app/     # Demo 应用
```

## 发布（维护者）

1. 更新 `gradle.properties` 中 `VERSION_NAME`
2. 提交并推送到 `main`
3. 打 Tag（与版本号一致）：`git tag 1.0.0 && git push origin 1.0.0`
4. 在 [JitPack](https://jitpack.io/#AnkioTomas/webdav) 等待构建变绿

本地验证：

```bash
./gradlew :lib:publishToMavenLocal -x test -x lint
```

| 文件 | 作用 |
|------|------|
| `jitpack.yml` | JitPack 构建（JDK 17、`publishToMavenLocal`） |
| `gradle.properties` | `GROUP`、`VERSION_NAME`、POM 元数据 |

## License

Apache License 2.0
