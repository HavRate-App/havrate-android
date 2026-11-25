# 哈夫币折算器 (HavRate Android)

以下内容由Gemini撰写😼

一款基于 **Kotlin + Jetpack Compose + Material 3** 开发的 Android 离线工具应用，用于快速完成哈夫币与人民币金额折算、账号资产配置记录以及一键生成结构化交易备注。

> 所有折算均在本机离线完成，**不申请网络权限、不联网、不收集任何个人数据**；历史记录与偏好设置仅保存在本机。

---

## 功能特性

### 1. 纯币折算
- 极简输入布局，快速按自定义比例（如 `1:53`）将哈夫币折算为人民币金额。
- 支持 **无 / `K`（千）/ `M`（百万）/ `B`（十亿）** 单位切换与实时高精度（`BigDecimal`）计算。
- 一键复制折算结果与备注。

### 2. 进阶折算与结构化备注
按层级整理账号信息，每一层均支持独立展开或隐藏，并自动拼接为整齐的备注文本：
- **红皮 & 刀皮**：内置红皮与近战刀皮列表多选。
- **保险**：支持记录保险格数与 9 格体验卡数量。
- **特勤处**：支持体力、负重等级，以及可选的潜水中心、洗消舱等级。
- **特殊物品计价**：支持 **A大子弹（发）**、**六套（套）**、**红弹（组）** 数量与单价录入，自动累加到总金额并展示「纯币 + 特殊物品」分项明细。

### 3. 折算历史
- 复制结果时可自动写入本地历史记录（可在设置中关闭）。
- 支持自定义历史保留上限（`20` / `50` / `100` 条），随时回看与再次复制。

### 4. 个性化设置与 Material 3 设计
- **启动与默认值**：自定义默认启动页（纯币折算 / 进阶折算）、默认折算比例、默认哈夫币单位、特殊物品默认单价。
- **折算与备注格式**：自定义金额小数位数（2 位 / 1 位 / 取整）、取整方式（四舍五入 / 向下取整 / 向上取整）、备注分隔符（`|`、`/`、空格、换行）及是否在备注中附带特殊物品单价。
- **外观体验**：遵循 Material 3 规范，支持 **浅色 / 深色 / 跟随系统** 主题、Android 12+ 动态取色（Dynamic Color）与 Edge-to-Edge 沉浸式界面。

---

## 环境要求与技术栈

| 项目 | 版本 / 说明 |
| --- | --- |
| 语言 | Kotlin `2.2.10` |
| UI 框架 | Jetpack Compose + Material 3（Compose BOM `2026.02.01`） |
| 最低支持系统 | Android 7.0（`minSdk = 24`） |
| 编译 / 目标 SDK | `compileSdk = 37` / `targetSdk = 37` |
| 构建工具 | Gradle `9.6.0` / Android Gradle Plugin `9.5.2` |
| 本地持久化 | `SharedPreferences` + `BigDecimal` 高精度计算 |

---

## 快速开始

### 1. 克隆项目
```bash
git clone https://github.com/HavRate-App/havrate-android.git
cd havrate-android
```

### 2. 使用 Android Studio 运行
直接用新版 Android Studio 打开项目目录，等待 Gradle 同步完成后选择模拟器或真机运行 `app` 即可。

### 3. 命令行构建 Debug APK
```bash
./gradlew assembleDebug
```
产物位于 `app/build/outputs/apk/debug/`。

---

## Release 签名打包

`app/build.gradle.kts` 已支持通过根目录的 `keystore.properties` 自动加载正式版签名配置（该文件与 `*.jks` / `*.keystore` 均已加入 `.gitignore`，**切勿提交到仓库**）。

1. 在项目根目录创建 `keystore.properties`：
   ```properties
   storeFile=your-release-key.jks
   storePassword=your_store_password
   keyAlias=your_key_alias
   keyPassword=your_key_password
   ```
2. 执行打包命令：
   ```bash
   ./gradlew assembleRelease
   ```
   产物位于 `app/build/outputs/apk/release/`。

---

## 项目结构

```text
.
├── app/
│   ├── build.gradle.kts                 # 应用模块配置与 Release 签名加载
│   └── src/main/
│       ├── AndroidManifest.xml          # 清单文件（无网络及敏感权限）
│       ├── java/com/havoccalc/app/
│       │   ├── MainActivity.kt          # 核心计算逻辑、页面与组件实现
│       │   └── ui/theme/                # Material 3 主题与配色定义
│       └── res/                         # 应用图标、字符串与基础资源
├── gradle/
│   └── libs.versions.toml               # Gradle Version Catalog 依赖管理
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 隐私与声明

- 本应用为纯本地离线工具，不收集、不上传任何用户数据。
- 内置皮肤列表与计价选项仅作辅助记录参考，可根据实际行情自定义单价与比例。
