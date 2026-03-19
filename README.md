# 贪吃蛇

这是一个原生 Android 贪吃蛇项目，使用 Kotlin + View 系统实现，可以直接导入 Android Studio 运行，也可以按本文档在命令行完成 APK 构建。

## 功能

- 20 x 20 棋盘
- 按钮控制和滑动控制
- 得分统计和本地最高分记录
- 暂停、继续、重新开始
- 随分数增加逐渐加速

## 项目结构

- `app/src/main/java/com/example/snake/MainActivity.kt`：页面入口和按钮交互
- `app/src/main/java/com/example/snake/SnakeGameView.kt`：游戏循环、绘制和碰撞逻辑
- `app/src/main/res/layout/activity_main.xml`：界面布局

## 运行环境

- Android Studio 稳定版，内置 JDK 17 即可
- 或者命令行环境下可用的 JDK 17
- Android SDK 34
- 一台安卓模拟器或真机

## Android Studio 运行

1. 打开 Android Studio。
2. 选择 `Open`，打开当前目录：
   `C:\Users\Lu\OneDrive\Obsidian Vault\游戏\ai贪吃蛇`
3. 等待 Gradle Sync 完成。
4. 选择模拟器或已连接设备。
5. 运行 `app` 配置。

## 命令行构建

这个仓库已经在当前目录下准备好了本地工具链：

- 本地 JDK 17：`.tools\jdk\current`
- 本地 Android SDK：`.tools\android-sdk`

在项目根目录执行下面的 PowerShell 命令即可构建：

```powershell
$env:JAVA_HOME = (Resolve-Path '.\.tools\jdk\current').Path
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:ANDROID_SDK_ROOT = (Resolve-Path '.\.tools\android-sdk').Path
$env:ANDROID_HOME = $env:ANDROID_SDK_ROOT
.\gradlew.bat assembleDebug
```

如果要安装到已连接设备：

```powershell
$env:JAVA_HOME = (Resolve-Path '.\.tools\jdk\current').Path
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"
$env:ANDROID_SDK_ROOT = (Resolve-Path '.\.tools\android-sdk').Path
$env:ANDROID_HOME = $env:ANDROID_SDK_ROOT
.\gradlew.bat installDebug
```

## 构建结果

已成功构建出的调试 APK 位于：

- `app\build\outputs\apk\debug\app-debug.apk`

当前机器上的实际绝对路径：

- `C:\Users\Lu\OneDrive\Obsidian Vault\游戏\ai贪吃蛇\app\build\outputs\apk\debug\app-debug.apk`

## 在电脑上运行

这是一个 Android 应用，不能像普通 Windows 程序一样直接双击运行。

可以使用以下方式在电脑上体验：

1. Android Studio Emulator
2. 第三方安卓模拟器，安装上面生成的 APK

## 游戏操作

- 点击 `上 / 下 / 左 / 右` 按钮控制移动方向
- 也可以在棋盘区域滑动切换方向
- 点击 `暂停` 暂停游戏
- 点击 `重新开始` 开启新一局

## 构建说明

- 当前项目目录包含中文路径，Android Gradle Plugin 在 Windows 下默认会拦截这种路径。
- 项目已经在 `gradle.properties` 中加入 `android.overridePathCheck=true`，否则命令行构建会失败。
- 项目仓库源已配置为镜像优先、官方兜底，用于改善依赖下载速度。
