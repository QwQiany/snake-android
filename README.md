# AI 贪吃蛇

这是一个可以直接导入 Android Studio 运行的原生 Android 贪吃蛇项目，使用 Kotlin + View 系统实现，不依赖游戏引擎。

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

- Android Studio（建议使用近两年的稳定版，内置 JDK 17 即可）
- Android SDK 34
- 一台安卓模拟器或真机

## 如何运行

### 方式一：Android Studio 直接运行

1. 打开 Android Studio。
2. 选择 `Open`，打开当前目录：
   `C:\Users\Lu\OneDrive\Obsidian Vault\游戏\ai贪吃蛇`
3. 等待 Gradle Sync 完成。
4. 选择一个模拟器或已连接的安卓设备。
5. 点击运行按钮，或直接运行 `app` 配置。

### 方式二：命令行构建

如果你的系统已经有 `JDK 17`，可以直接在项目根目录执行：

```powershell
.\gradlew.bat assembleDebug
```

安装到已连接设备：

```powershell
.\gradlew.bat installDebug
```

如果你本机已经全局安装了 `Gradle`，也可以使用：

```powershell
gradle assembleDebug
gradle installDebug
```

## 游戏操作

- 点击 `上 / 下 / 左 / 右` 按钮控制移动方向
- 也可以在棋盘区域滑动切换方向
- 点击 `暂停` 暂停游戏
- 点击 `重新开始` 开启新一局

## 说明

- 这个仓库当前已经包含标准 Android 工程骨架、游戏代码和 `Gradle Wrapper`。
- 我在当前执行环境里没有找到本地 `Java / Android SDK`，因此没有完成实际 APK 构建验证；如果你本机装有 Android Studio，按上面的步骤可以直接导入并运行。
