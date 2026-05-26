# Ankio WebDAV

Android WebDAV 客户端库：轻量 API 封装（Sardine）、Compose 配置页、连接测试（ThemeToast）。

[![JitPack](https://jitpack.io/v/AnkioTomas/webdav.svg)](https://jitpack.io/#AnkioTomas/webdav)

## 环境要求

| 项 | 值 |
|---|---|
| minSdk | 30 |
| Java | 17 |
| Compose | BOM 2026.05+ |
| 依赖 | [Ankio Theme](https://github.com/AnkioTomas/theme) 1.0.4+ |

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

`1.0.0` 替换为 [JitPack](https://jitpack.io/#AnkioTomas/webdav) 上对应 **Git Tag** 或 **commit hash**。

### 3. 初始化

```kotlin
class App : Application() {
    override fun onCreate() {
        super.onCreate()
        WebDav.init(this)
    }
}
```

### 4. 使用示例

```kotlin
// 配置页
WebDavSettingsScreen(
    state = WebDavSettingsState(...),
    onServerChange = { ... },
    onUsernameChange = { ... },
    onPasswordChange = { ... },
    onTestingChange = { ... },
)

// 客户端
val client = WebDav.client(context)
client.writeText("demo/hello.txt", "hello")
val items = client.list("demo")
```

## 发布到 JitPack

1. 将代码推送到 GitHub 仓库 `AnkioTomas/webdav`
2. 打 Tag（版本号与 `gradle.properties` 中 `VERSION_NAME` 一致，例如 `1.0.0`）
3. 打开 [JitPack](https://jitpack.io/#AnkioTomas/webdav)，等待构建成功

本地验证发布产物：

```bash
./gradlew :lib:publishToMavenLocal -x test
```

配置文件说明：

| 文件 | 作用 |
|------|------|
| `jitpack.yml` | JitPack 构建命令（JDK 17、`publishToMavenLocal`） |
| `gradle.properties` | `GROUP`、`VERSION_NAME`、POM 元数据 |
| `lib/build.gradle.kts` | `maven-publish` 与 `release` 出版物 |

## License

Apache License 2.0
