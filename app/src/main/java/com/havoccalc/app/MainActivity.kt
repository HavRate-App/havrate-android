package com.havoccalc.app

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.havoccalc.app.ui.theme.哈夫币换算器Theme
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import java.math.BigDecimal
import java.math.RoundingMode
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val context = LocalContext.current
            var themeMode by rememberSaveable {
                mutableStateOf(loadEnumSetting(context, KEY_THEME_MODE, ThemeMode.SYSTEM))
            }
            val isDark = when (themeMode) {
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
            }
            哈夫币换算器Theme(darkTheme = isDark) {
                val windowBgColor = MaterialTheme.colorScheme.background.toArgb()
                SideEffect {
                    val barStyle = if (isDark) {
                        SystemBarStyle.dark(android.graphics.Color.TRANSPARENT)
                    } else {
                        SystemBarStyle.light(
                            android.graphics.Color.TRANSPARENT,
                            android.graphics.Color.TRANSPARENT
                        )
                    }
                    enableEdgeToEdge(
                        statusBarStyle = barStyle,
                        navigationBarStyle = barStyle
                    )
                    window.decorView.setBackgroundColor(windowBgColor)
                }
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HavocCalcApp(
                        themeMode = themeMode,
                        onThemeModeChange = { mode ->
                            themeMode = mode
                            saveStringSetting(context, KEY_THEME_MODE, mode.name)
                        }
                    )
                }
            }
        }
    }
}

private const val PREFS_NAME = "havoc_calc_prefs"
private const val KEY_AUTO_DETECT_UNIT = "auto_detect_coin_unit"
private const val KEY_SHOW_SPECIAL_GOLD_SKIN = "show_special_gold_skin"
private const val KEY_SHOW_GOLD_SKIN = "show_gold_skin"
private const val KEY_HISTORY_JSON = "calc_history_json"
private const val KEY_START_PAGE = "default_start_page"
private const val KEY_DEFAULT_RATE = "default_rate"
private const val KEY_DEFAULT_COIN_UNIT = "default_coin_unit"
private const val KEY_AMOUNT_PRECISION = "amount_precision"
private const val KEY_AMOUNT_ROUNDING = "amount_rounding"
private const val KEY_REMARK_SEPARATOR = "remark_separator"
private const val KEY_THEME_MODE = "theme_mode"
private const val KEY_DEFAULT_SHOW_SKINS_LAYER = "default_show_skins_layer"
private const val KEY_DEFAULT_SHOW_SAFEBOX_LAYER = "default_show_safebox_layer"
private const val KEY_DEFAULT_SHOW_BASE_LAYER = "default_show_base_layer"
private const val KEY_DEFAULT_SHOW_SPECIAL_ITEMS_LAYER = "default_show_special_items_layer"
private const val KEY_DEFAULT_A_BIG_AMMO_PRICE = "default_a_big_ammo_price"
private const val KEY_DEFAULT_TIER6_ARMOR_PRICE = "default_tier6_armor_price"
private const val KEY_DEFAULT_RED_AMMO_PRICE = "default_red_ammo_price"
private const val KEY_INCLUDE_SPECIAL_ITEM_PRICE_IN_REMARK = "include_special_item_price_in_remark"
private const val KEY_AUTO_SAVE_HISTORY = "auto_save_history"
private const val KEY_HISTORY_LIMIT = "history_limit"
private const val DEFAULT_HISTORY_LIMIT = 50
private const val DEFAULT_A_BIG_AMMO_UNIT_PRICE = "0.5"
private const val DEFAULT_TIER6_ARMOR_UNIT_PRICE = "2"
private val HISTORY_LIMIT_OPTIONS = listOf(20, 50, 100)

enum class ThemeMode(val label: String) {
    SYSTEM("跟随系统"),
    LIGHT("浅色"),
    DARK("深色")
}

private enum class StartPageOption(val label: String) {
    PURE("纯币折算"),
    ADVANCED("进阶折算");

    fun toAppPage(): AppPage = when (this) {
        PURE -> AppPage.PURE_CONVERT
        ADVANCED -> AppPage.ADVANCED_CONVERT
    }
}

private enum class AmountPrecision(val label: String, val scale: Int) {
    TWO_DECIMALS("2位小数", 2),
    ONE_DECIMAL("1位小数", 1),
    INTEGER("取整", 0)
}

private enum class AmountRounding(val label: String, val mode: RoundingMode) {
    HALF_UP("四舍五入", RoundingMode.HALF_UP),
    FLOOR("向下取整", RoundingMode.FLOOR),
    CEILING("向上取整", RoundingMode.CEILING)
}

private enum class RemarkSeparator(val label: String, val separator: String) {
    PIPE("竖线 |", " | "),
    SLASH("斜杠 /", " / "),
    SPACE("空格", " "),
    NEWLINE("换行", "\n")
}

private enum class AppPage(val depth: Int) {
    PURE_CONVERT(0),
    ADVANCED_CONVERT(0),
    HISTORY(0),
    LAB(0),
    SETTINGS(0),
    RED_SKIN_SELECT(1),
    SPECIAL_GOLD_SKIN_SELECT(1),
    GOLD_SKIN_SELECT(1),
    KNIFE_SKIN_SELECT(1),
    ABOUT(1),
    CHANGELOG(2)
}

private enum class CoinUnit(
    val buttonLabel: String,
    val suffix: String,
    val placeholder: String,
    val toWanMultiplier: BigDecimal
) {
    NONE(
        buttonLabel = "无",
        suffix = "",
        placeholder = "例如 530000",
        toWanMultiplier = BigDecimal("0.0001")
    ),
    K(
        buttonLabel = "K(千)",
        suffix = "K",
        placeholder = "例如 530",
        toWanMultiplier = BigDecimal("0.1")
    ),
    M(
        buttonLabel = "M(百万)",
        suffix = "M",
        placeholder = "例如 53",
        toWanMultiplier = BigDecimal("100")
    ),
    B(
        buttonLabel = "B(十亿)",
        suffix = "B",
        placeholder = "例如 1.2",
        toWanMultiplier = BigDecimal("100000")
    )
}

private data class SkinCatalogItem(
    val name: String,
    val subtitle: String,
    val group: String? = null
)

private val RED_SKIN_OPTIONS = listOf(
    SkinCatalogItem("蚀金玫瑰", "红狼"),
    SkinCatalogItem("水墨云图", "骇爪"),
    SkinCatalogItem("维什戴尔", "骇爪 · 明日方舟联动"),
    SkinCatalogItem("凌霄戍卫", "威龙"),
    SkinCatalogItem("黑·天际线", "露娜 · 明日方舟联动"),
    SkinCatalogItem("午夜邮差", "蛊 · 明日方舟联动"),
    SkinCatalogItem("安魂", "牧羊人"),
    SkinCatalogItem("狂怒", "乌鲁鲁"),
    SkinCatalogItem("雷霆之息", "银翼")
)

private val SPECIAL_GOLD_SKIN_OPTIONS = listOf(
    SkinCatalogItem("飞虎", "威龙"),
    SkinCatalogItem("铁面判官", "威龙"),
    SkinCatalogItem("蛟龙突击队", "威龙"),
    SkinCatalogItem("电锯惊魂", "红狼 · 联动"),
    SkinCatalogItem("送葬人·无题密令", "蜂医 · S5通行证/明日方舟联动")
)

private val GOLD_SKIN_OPTIONS = listOf(
    SkinCatalogItem("壮志凌云", "威龙 · S3通行证"),
    SkinCatalogItem("街头之王", "牧羊人 · S2通行证"),
    SkinCatalogItem("守夜人", "无名 · 彩六联动"),
    SkinCatalogItem("裸蛇", "无名 · 潜龙谍影联动"),
    SkinCatalogItem("医生", "蜂医 · 彩六联动"),
    SkinCatalogItem("不破誓约", "深蓝 · S6通行证"),
    SkinCatalogItem("盾峰", "深蓝 · 彩六联动"),
    SkinCatalogItem("金牌射手", "露娜 · S7通行证"),
    SkinCatalogItem("荒原猎手", "乌鲁鲁 · S8通行证"),
    SkinCatalogItem("未结卷宗", "银翼 · S9通行证"),
    SkinCatalogItem("西部往事", "疾风 · S10通行证"),
    SkinCatalogItem("骇客少女", "骇爪 · S11通行证"),
    SkinCatalogItem("沙漠孤星", "比特 · S12通行证"),
    SkinCatalogItem("不羁人生", "蛊")
)

private val KNIFE_SKIN_OPTIONS = listOf(
    SkinCatalogItem("赤枭", "S1"),
    SkinCatalogItem("怜悯", "S2"),
    SkinCatalogItem("影锋", "S3"),
    SkinCatalogItem("黑海", "S4"),
    SkinCatalogItem("北极星", "S5"),
    SkinCatalogItem("电锯", "S6 · 电锯惊魂联动"),
    SkinCatalogItem("信条", "S7"),
    SkinCatalogItem("暗星", "S8"),
    SkinCatalogItem("坠星者", "S9"),
    SkinCatalogItem("挽歌", "S10"),
    SkinCatalogItem("重塑", "S11"),
    SkinCatalogItem("黑伤", "S12 · 影之刃零联动")
)

private fun parseSavedSkinNames(raw: String): List<String> =
    if (raw.isBlank()) emptyList() else raw.split("\n").filter { it.isNotBlank() }

private fun toggleSkinSelection(
    currentRaw: String,
    skinName: String,
    catalog: List<SkinCatalogItem>
): String {
    val selected = parseSavedSkinNames(currentRaw).toMutableSet()
    if (!selected.add(skinName)) {
        selected.remove(skinName)
    }
    return catalog.map { it.name }.filter { it in selected }.joinToString("\n")
}

private data class HistoryEntry(
    val id: Long,
    val timestampText: String,
    val rmbFormatted: String,
    val remark: String
)

private sealed interface CalcState {
    data object Incomplete : CalcState
    data class Error(val message: String) : CalcState
    data class Valid(
        val rmbFormatted: String,
        val remark: String,
        val breakdownText: String? = null
    ) : CalcState
}

private fun formatTimestamp(millis: Long): String {
    val formatter = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return formatter.format(Date(millis))
}

private inline fun <reified T : Enum<T>> loadEnumSetting(
    context: Context,
    key: String,
    defaultValue: T
): T {
    val saved = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(key, null) ?: return defaultValue
    return enumValues<T>().firstOrNull { it.name == saved } ?: defaultValue
}

private fun loadStringSetting(context: Context, key: String, defaultValue: String = ""): String {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(key, defaultValue) ?: defaultValue
}

private fun saveStringSetting(context: Context, key: String, value: String) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(key, value)
        .apply()
}

private fun loadBooleanSetting(context: Context, key: String, defaultValue: Boolean): Boolean {
    return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getBoolean(key, defaultValue)
}

private fun saveBooleanSetting(context: Context, key: String, value: Boolean) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putBoolean(key, value)
        .apply()
}

private fun loadHistoryLimitSetting(context: Context): Int {
    val saved = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getInt(KEY_HISTORY_LIMIT, DEFAULT_HISTORY_LIMIT)
    return if (saved in HISTORY_LIMIT_OPTIONS) saved else DEFAULT_HISTORY_LIMIT
}

private fun saveHistoryLimitSetting(context: Context, limit: Int) {
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putInt(KEY_HISTORY_LIMIT, limit)
        .apply()
}

@Suppress("DEPRECATION")
private fun getAppVersionName(context: Context): String {
    return try {
        val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(
                context.packageName,
                PackageManager.PackageInfoFlags.of(0L)
            )
        } else {
            context.packageManager.getPackageInfo(context.packageName, 0)
        }
        info.versionName ?: "1.0"
    } catch (_: Exception) {
        "1.0"
    }
}

private fun loadAutoDetectSetting(context: Context): Boolean =
    loadBooleanSetting(context, KEY_AUTO_DETECT_UNIT, false)

private fun saveAutoDetectSetting(context: Context, enabled: Boolean) =
    saveBooleanSetting(context, KEY_AUTO_DETECT_UNIT, enabled)

private fun loadShowSpecialGoldSkinSetting(context: Context): Boolean =
    loadBooleanSetting(context, KEY_SHOW_SPECIAL_GOLD_SKIN, false)

private fun saveShowSpecialGoldSkinSetting(context: Context, enabled: Boolean) =
    saveBooleanSetting(context, KEY_SHOW_SPECIAL_GOLD_SKIN, enabled)

private fun loadShowGoldSkinSetting(context: Context): Boolean =
    loadBooleanSetting(context, KEY_SHOW_GOLD_SKIN, false)

private fun saveShowGoldSkinSetting(context: Context, enabled: Boolean) =
    saveBooleanSetting(context, KEY_SHOW_GOLD_SKIN, enabled)

private fun loadHistory(context: Context): List<HistoryEntry> {
    val raw = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .getString(KEY_HISTORY_JSON, null)
        ?: return emptyList()
    return try {
        val array = JSONArray(raw)
        buildList {
            for (i in 0 until array.length()) {
                val obj = array.optJSONObject(i) ?: continue
                val id = obj.optLong("id", 0L)
                val timestampText = obj.optString("timestampText", "")
                val rmbFormatted = obj.optString("rmbFormatted", "")
                val remark = obj.optString("remark", "")
                if (remark.isNotBlank()) {
                    add(
                        HistoryEntry(
                            id = if (id > 0L) id else i.toLong() + 1L,
                            timestampText = timestampText,
                            rmbFormatted = rmbFormatted,
                            remark = remark
                        )
                    )
                }
            }
        }
    } catch (_: Exception) {
        emptyList()
    }
}

private fun saveHistory(
    context: Context,
    entries: List<HistoryEntry>,
    maxCount: Int = DEFAULT_HISTORY_LIMIT
) {
    val array = JSONArray()
    entries.take(maxCount).forEach { entry ->
        val obj = JSONObject()
        obj.put("id", entry.id)
        obj.put("timestampText", entry.timestampText)
        obj.put("rmbFormatted", entry.rmbFormatted)
        obj.put("remark", entry.remark)
        array.put(obj)
    }
    context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        .edit()
        .putString(KEY_HISTORY_JSON, array.toString())
        .apply()
}

private fun parseCoinInput(rawInput: String): Pair<String, CoinUnit?> {
    val numberPart = StringBuilder()
    var hasDot = false
    var detectedUnit: CoinUnit? = null

    for (ch in rawInput) {
        when {
            ch.isDigit() -> numberPart.append(ch)
            ch == '.' && !hasDot -> {
                hasDot = true
                numberPart.append(ch)
            }
            ch.equals('K', ignoreCase = true) -> detectedUnit = CoinUnit.K
            ch.equals('M', ignoreCase = true) -> detectedUnit = CoinUnit.M
            ch.equals('B', ignoreCase = true) -> detectedUnit = CoinUnit.B
        }
    }

    return numberPart.toString() to detectedUnit
}

private fun sanitizeDecimalInput(input: String): String {
    val result = StringBuilder()
    var hasDot = false
    for (ch in input) {
        when {
            ch.isDigit() -> result.append(ch)
            ch == '.' && !hasDot -> {
                hasDot = true
                result.append(ch)
            }
        }
    }
    return result.toString()
}

private fun BigDecimal.toCleanString(): String {
    val stripped = stripTrailingZeros()
    return if (stripped.compareTo(BigDecimal.ZERO) == 0) {
        "0"
    } else {
        stripped.toPlainString()
    }
}

private fun sanitizeIntegerInput(input: String, maxDigits: Int = 4): String {
    return input.filter { it.isDigit() }.take(maxDigits)
}

private fun calcSpecialItemSubtotal(
    enabled: Boolean,
    countText: String,
    unitPriceText: String
): BigDecimal {
    if (!enabled) return BigDecimal.ZERO
    val count = countText.toIntOrNull() ?: return BigDecimal.ZERO
    if (count <= 0) return BigDecimal.ZERO
    val unitPrice = unitPriceText.toBigDecimalOrNull() ?: return BigDecimal.ZERO
    if (unitPrice.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO
    return unitPrice.multiply(BigDecimal.valueOf(count.toLong()))
}

private fun formatQuantityAttribute(
    label: String,
    enabled: Boolean,
    countText: String,
    unitSuffix: String,
    unitPriceText: String = "",
    includeUnitPrice: Boolean = true
): String? {
    if (!enabled) return null
    val count = countText.toIntOrNull()?.takeIf { it > 0 }
    val unitPrice = if (includeUnitPrice) {
        unitPriceText.toBigDecimalOrNull()?.takeIf { it.compareTo(BigDecimal.ZERO) > 0 }
    } else {
        null
    }
    val quantityPart = if (count != null) "${count}$unitSuffix" else "有"
    val pricePart = if (unitPrice != null) "(${unitPrice.toCleanString()}元/$unitSuffix)" else ""
    return "$label:$quantityPart$pricePart"
}

private fun formatAttributesSummary(
    hasRedSkin: Boolean,
    selectedRedSkins: List<String>,
    showSpecialGoldSkin: Boolean,
    hasSpecialGoldSkin: Boolean,
    selectedSpecialGoldSkins: List<String>,
    showGoldSkin: Boolean,
    hasGoldSkin: Boolean,
    selectedGoldSkins: List<String>,
    hasKnifeSkin: Boolean,
    selectedKnifeSkins: List<String>,
    safeSlots: Int,
    hasNineSlotTrialCard: Boolean,
    nineSlotTrialCardCountText: String,
    staminaLevel: Int,
    weightLevel: Int,
    divingCenterLevel: Int?,
    decontaminationLevel: Int?,
    hasABigAmmo: Boolean,
    aBigAmmoCountText: String,
    aBigAmmoUnitPriceText: String,
    hasTier6Armor: Boolean,
    tier6ArmorCountText: String,
    tier6ArmorUnitPriceText: String,
    hasRedAmmo: Boolean,
    redAmmoCountText: String,
    redAmmoUnitPriceText: String,
    includeSpecialItemPriceInRemark: Boolean = true
): String {
    val parts = buildList {
        val redText = when {
            !hasRedSkin -> "无"
            selectedRedSkins.isNotEmpty() -> selectedRedSkins.joinToString("/")
            else -> "有"
        }
        add("红皮:$redText")
        if (showSpecialGoldSkin && hasSpecialGoldSkin) {
            val specialGoldText = if (selectedSpecialGoldSkins.isNotEmpty()) {
                selectedSpecialGoldSkins.joinToString("/")
            } else {
                "有"
            }
            add("特殊金皮:$specialGoldText")
        }
        if (showGoldSkin && hasGoldSkin) {
            val goldText = if (selectedGoldSkins.isNotEmpty()) {
                selectedGoldSkins.joinToString("/")
            } else {
                "有"
            }
            add("金皮:$goldText")
        }
        val knifeText = when {
            !hasKnifeSkin -> "无"
            selectedKnifeSkins.isNotEmpty() -> selectedKnifeSkins.joinToString("/")
            else -> "有"
        }
        add("刀皮:$knifeText")
        add("保险:${safeSlots}格")
        formatQuantityAttribute(
            label = "9格体验卡",
            enabled = hasNineSlotTrialCard,
            countText = nineSlotTrialCardCountText,
            unitSuffix = "张"
        )?.let { add(it) }
        add("体力:${staminaLevel}级")
        add("负重:${weightLevel}级")
        if (divingCenterLevel != null) {
            add("潜水中心:${divingCenterLevel}级")
        }
        if (decontaminationLevel != null) {
            add("洗消舱:${decontaminationLevel}级")
        }
        formatQuantityAttribute(
            label = "A大子弹",
            enabled = hasABigAmmo,
            countText = aBigAmmoCountText,
            unitSuffix = "发",
            unitPriceText = aBigAmmoUnitPriceText,
            includeUnitPrice = includeSpecialItemPriceInRemark
        )?.let { add(it) }
        formatQuantityAttribute(
            label = "六套",
            enabled = hasTier6Armor,
            countText = tier6ArmorCountText,
            unitSuffix = "套",
            unitPriceText = tier6ArmorUnitPriceText,
            includeUnitPrice = includeSpecialItemPriceInRemark
        )?.let { add(it) }
        formatQuantityAttribute(
            label = "红弹",
            enabled = hasRedAmmo,
            countText = redAmmoCountText,
            unitSuffix = "组",
            unitPriceText = redAmmoUnitPriceText,
            includeUnitPrice = includeSpecialItemPriceInRemark
        )?.let { add(it) }
    }
    return parts.joinToString(" ")
}

private fun calculateResult(
    coinText: String,
    coinUnit: CoinUnit,
    rateText: String,
    attributesSummary: String? = null,
    extraRmb: BigDecimal = BigDecimal.ZERO,
    precision: AmountPrecision = AmountPrecision.TWO_DECIMALS,
    rounding: AmountRounding = AmountRounding.HALF_UP,
    remarkSeparator: RemarkSeparator = RemarkSeparator.PIPE
): CalcState {
    if (coinText.isBlank() || rateText.isBlank()) {
        return CalcState.Incomplete
    }

    val coinsInput = coinText.toBigDecimalOrNull()
        ?: return CalcState.Error("请输入有效的哈夫币数量")
    val rate = rateText.toBigDecimalOrNull()
        ?: return CalcState.Error("请输入有效的比例")
    if (rate.compareTo(BigDecimal.ZERO) <= 0) {
        return CalcState.Error("比例必须大于 0")
    }

    val coinsInWan = coinsInput.multiply(coinUnit.toWanMultiplier)
    val hasExtra = extraRmb.compareTo(BigDecimal.ZERO) > 0
    val totalNumerator = if (hasExtra) {
        coinsInWan.add(rate.multiply(extraRmb))
    } else {
        coinsInWan
    }
    val totalRmb = totalNumerator.divide(rate, precision.scale, rounding.mode)
    val rmbFormatted = totalRmb.toPlainString()

    val breakdownText = if (hasExtra) {
        val coinOnlyFormatted = coinsInWan.divide(rate, precision.scale, rounding.mode)
            .toPlainString()
        "纯币 ¥$coinOnlyFormatted + 特殊物品 ¥${extraRmb.toCleanString()}"
    } else {
        null
    }

    val remarkParts = buildList {
        add("${coinsInput.toCleanString()}${coinUnit.suffix}")
        add("1:${rate.toCleanString()}")
        add("${rmbFormatted}元")
        if (!attributesSummary.isNullOrBlank()) {
            add(attributesSummary)
        }
    }

    return CalcState.Valid(
        rmbFormatted = rmbFormatted,
        remark = remarkParts.joinToString(remarkSeparator.separator),
        breakdownText = breakdownText
    )
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("哈夫币折算备注", text))
}

@Composable
fun HavocCalcApp(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    onThemeModeChange: (ThemeMode) -> Unit = {}
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    var startPage by rememberSaveable {
        mutableStateOf(loadEnumSetting(context, KEY_START_PAGE, StartPageOption.PURE))
    }
    var defaultRateText by rememberSaveable {
        mutableStateOf(loadStringSetting(context, KEY_DEFAULT_RATE, ""))
    }
    var defaultCoinUnit by rememberSaveable {
        mutableStateOf(loadEnumSetting(context, KEY_DEFAULT_COIN_UNIT, CoinUnit.M))
    }
    var amountPrecision by rememberSaveable {
        mutableStateOf(loadEnumSetting(context, KEY_AMOUNT_PRECISION, AmountPrecision.TWO_DECIMALS))
    }
    var amountRounding by rememberSaveable {
        mutableStateOf(loadEnumSetting(context, KEY_AMOUNT_ROUNDING, AmountRounding.HALF_UP))
    }
    var remarkSeparator by rememberSaveable {
        mutableStateOf(loadEnumSetting(context, KEY_REMARK_SEPARATOR, RemarkSeparator.PIPE))
    }
    var defaultShowSkinsLayer by rememberSaveable {
        mutableStateOf(loadBooleanSetting(context, KEY_DEFAULT_SHOW_SKINS_LAYER, true))
    }
    var defaultShowSafeBoxLayer by rememberSaveable {
        mutableStateOf(loadBooleanSetting(context, KEY_DEFAULT_SHOW_SAFEBOX_LAYER, true))
    }
    var defaultShowBaseLayer by rememberSaveable {
        mutableStateOf(loadBooleanSetting(context, KEY_DEFAULT_SHOW_BASE_LAYER, true))
    }
    var defaultShowSpecialItemsLayer by rememberSaveable {
        mutableStateOf(loadBooleanSetting(context, KEY_DEFAULT_SHOW_SPECIAL_ITEMS_LAYER, true))
    }
    var defaultABigAmmoPriceText by rememberSaveable {
        mutableStateOf(
            loadStringSetting(context, KEY_DEFAULT_A_BIG_AMMO_PRICE, DEFAULT_A_BIG_AMMO_UNIT_PRICE)
        )
    }
    var defaultTier6ArmorPriceText by rememberSaveable {
        mutableStateOf(
            loadStringSetting(context, KEY_DEFAULT_TIER6_ARMOR_PRICE, DEFAULT_TIER6_ARMOR_UNIT_PRICE)
        )
    }
    var defaultRedAmmoPriceText by rememberSaveable {
        mutableStateOf(loadStringSetting(context, KEY_DEFAULT_RED_AMMO_PRICE, ""))
    }
    var includeSpecialItemPriceInRemark by rememberSaveable {
        mutableStateOf(
            loadBooleanSetting(context, KEY_INCLUDE_SPECIAL_ITEM_PRICE_IN_REMARK, true)
        )
    }
    var autoSaveHistory by rememberSaveable {
        mutableStateOf(loadBooleanSetting(context, KEY_AUTO_SAVE_HISTORY, true))
    }
    var historyLimit by rememberSaveable {
        mutableIntStateOf(loadHistoryLimitSetting(context))
    }

    var pureCoinText by rememberSaveable { mutableStateOf("") }
    var pureCoinUnit by rememberSaveable { mutableStateOf(defaultCoinUnit) }
    var isPureUnitManuallySelected by rememberSaveable { mutableStateOf(true) }
    var pureUnitFlashTrigger by remember { mutableIntStateOf(0) }
    var pureRateText by rememberSaveable { mutableStateOf(defaultRateText) }

    var coinText by rememberSaveable { mutableStateOf("") }
    var coinUnit by rememberSaveable { mutableStateOf(defaultCoinUnit) }
    var isUnitManuallySelected by rememberSaveable { mutableStateOf(true) }
    var unitFlashTrigger by remember { mutableIntStateOf(0) }
    var rateText by rememberSaveable { mutableStateOf(defaultRateText) }

    var hasRedSkin by rememberSaveable { mutableStateOf(false) }
    var selectedRedSkinsRaw by rememberSaveable { mutableStateOf("") }
    var hasSpecialGoldSkin by rememberSaveable { mutableStateOf(false) }
    var selectedSpecialGoldSkinsRaw by rememberSaveable { mutableStateOf("") }
    var hasGoldSkin by rememberSaveable { mutableStateOf(false) }
    var selectedGoldSkinsRaw by rememberSaveable { mutableStateOf("") }
    var hasKnifeSkin by rememberSaveable { mutableStateOf(false) }
    var selectedKnifeSkinsRaw by rememberSaveable { mutableStateOf("") }
    var safeSlots by rememberSaveable { mutableIntStateOf(6) }
    var showSmallSafeSlots by rememberSaveable { mutableStateOf(false) }
    var hasNineSlotTrialCard by rememberSaveable { mutableStateOf(false) }
    var nineSlotTrialCardCountText by rememberSaveable { mutableStateOf("") }
    var staminaLevel by rememberSaveable { mutableIntStateOf(5) }
    var showLowStaminaLevels by rememberSaveable { mutableStateOf(false) }
    var weightLevel by rememberSaveable { mutableIntStateOf(5) }
    var showLowWeightLevels by rememberSaveable { mutableStateOf(false) }
    var divingCenterLevel by rememberSaveable { mutableStateOf<Int?>(null) }
    var decontaminationLevel by rememberSaveable { mutableStateOf<Int?>(null) }
    var showOptionalFacilities by rememberSaveable { mutableStateOf(false) }
    var hasABigAmmo by rememberSaveable { mutableStateOf(false) }
    var aBigAmmoCountText by rememberSaveable { mutableStateOf("") }
    var aBigAmmoUnitPriceText by rememberSaveable { mutableStateOf(defaultABigAmmoPriceText) }
    var hasTier6Armor by rememberSaveable { mutableStateOf(false) }
    var tier6ArmorCountText by rememberSaveable { mutableStateOf("") }
    var tier6ArmorUnitPriceText by rememberSaveable { mutableStateOf(defaultTier6ArmorPriceText) }
    var hasRedAmmo by rememberSaveable { mutableStateOf(false) }
    var redAmmoCountText by rememberSaveable { mutableStateOf("") }
    var redAmmoUnitPriceText by rememberSaveable { mutableStateOf(defaultRedAmmoPriceText) }
    var showSkinsLayer by rememberSaveable { mutableStateOf(defaultShowSkinsLayer) }
    var showSafeBoxLayer by rememberSaveable { mutableStateOf(defaultShowSafeBoxLayer) }
    var showBaseLayer by rememberSaveable { mutableStateOf(defaultShowBaseLayer) }
    var showSpecialItemsLayer by rememberSaveable { mutableStateOf(defaultShowSpecialItemsLayer) }

    var autoDetectCoinUnit by rememberSaveable {
        mutableStateOf(loadAutoDetectSetting(context))
    }
    var showSpecialGoldSkin by rememberSaveable {
        mutableStateOf(loadShowSpecialGoldSkinSetting(context))
    }
    var showGoldSkin by rememberSaveable {
        mutableStateOf(loadShowGoldSkinSetting(context))
    }
    var historyEntries by remember {
        mutableStateOf(loadHistory(context).take(historyLimit))
    }
    var currentPage by rememberSaveable { mutableStateOf(startPage.toAppPage()) }

    val selectedRedSkins = remember(selectedRedSkinsRaw) {
        parseSavedSkinNames(selectedRedSkinsRaw)
    }
    val selectedSpecialGoldSkins = remember(selectedSpecialGoldSkinsRaw) {
        parseSavedSkinNames(selectedSpecialGoldSkinsRaw)
    }
    val selectedGoldSkins = remember(selectedGoldSkinsRaw) {
        parseSavedSkinNames(selectedGoldSkinsRaw)
    }
    val selectedKnifeSkins = remember(selectedKnifeSkinsRaw) {
        parseSavedSkinNames(selectedKnifeSkinsRaw)
    }

    val attributesSummary = remember(
        hasRedSkin,
        selectedRedSkins,
        showSpecialGoldSkin,
        hasSpecialGoldSkin,
        selectedSpecialGoldSkins,
        showGoldSkin,
        hasGoldSkin,
        selectedGoldSkins,
        hasKnifeSkin,
        selectedKnifeSkins,
        safeSlots,
        hasNineSlotTrialCard,
        nineSlotTrialCardCountText,
        staminaLevel,
        weightLevel,
        divingCenterLevel,
        decontaminationLevel,
        hasABigAmmo,
        aBigAmmoCountText,
        aBigAmmoUnitPriceText,
        hasTier6Armor,
        tier6ArmorCountText,
        tier6ArmorUnitPriceText,
        hasRedAmmo,
        redAmmoCountText,
        redAmmoUnitPriceText,
        includeSpecialItemPriceInRemark
    ) {
        formatAttributesSummary(
            hasRedSkin = hasRedSkin,
            selectedRedSkins = selectedRedSkins,
            showSpecialGoldSkin = showSpecialGoldSkin,
            hasSpecialGoldSkin = hasSpecialGoldSkin,
            selectedSpecialGoldSkins = selectedSpecialGoldSkins,
            showGoldSkin = showGoldSkin,
            hasGoldSkin = hasGoldSkin,
            selectedGoldSkins = selectedGoldSkins,
            hasKnifeSkin = hasKnifeSkin,
            selectedKnifeSkins = selectedKnifeSkins,
            safeSlots = safeSlots,
            hasNineSlotTrialCard = hasNineSlotTrialCard,
            nineSlotTrialCardCountText = nineSlotTrialCardCountText,
            staminaLevel = staminaLevel,
            weightLevel = weightLevel,
            divingCenterLevel = divingCenterLevel,
            decontaminationLevel = decontaminationLevel,
            hasABigAmmo = hasABigAmmo,
            aBigAmmoCountText = aBigAmmoCountText,
            aBigAmmoUnitPriceText = aBigAmmoUnitPriceText,
            hasTier6Armor = hasTier6Armor,
            tier6ArmorCountText = tier6ArmorCountText,
            tier6ArmorUnitPriceText = tier6ArmorUnitPriceText,
            hasRedAmmo = hasRedAmmo,
            redAmmoCountText = redAmmoCountText,
            redAmmoUnitPriceText = redAmmoUnitPriceText,
            includeSpecialItemPriceInRemark = includeSpecialItemPriceInRemark
        )
    }

    val specialItemsRmb = remember(
        hasABigAmmo,
        aBigAmmoCountText,
        aBigAmmoUnitPriceText,
        hasTier6Armor,
        tier6ArmorCountText,
        tier6ArmorUnitPriceText,
        hasRedAmmo,
        redAmmoCountText,
        redAmmoUnitPriceText
    ) {
        calcSpecialItemSubtotal(hasABigAmmo, aBigAmmoCountText, aBigAmmoUnitPriceText)
            .add(calcSpecialItemSubtotal(hasTier6Armor, tier6ArmorCountText, tier6ArmorUnitPriceText))
            .add(calcSpecialItemSubtotal(hasRedAmmo, redAmmoCountText, redAmmoUnitPriceText))
    }

    val pureCalcState = remember(
        pureCoinText,
        pureCoinUnit,
        pureRateText,
        amountPrecision,
        amountRounding,
        remarkSeparator
    ) {
        calculateResult(
            coinText = pureCoinText,
            coinUnit = pureCoinUnit,
            rateText = pureRateText,
            precision = amountPrecision,
            rounding = amountRounding,
            remarkSeparator = remarkSeparator
        )
    }

    val calcState = remember(
        coinText,
        coinUnit,
        rateText,
        attributesSummary,
        specialItemsRmb,
        amountPrecision,
        amountRounding,
        remarkSeparator
    ) {
        calculateResult(
            coinText = coinText,
            coinUnit = coinUnit,
            rateText = rateText,
            attributesSummary = attributesSummary,
            extraRmb = specialItemsRmb,
            precision = amountPrecision,
            rounding = amountRounding,
            remarkSeparator = remarkSeparator
        )
    }

    val recordHistory: (String, String) -> Unit = { rmbFormatted, remark ->
        if (autoSaveHistory) {
            val now = System.currentTimeMillis()
            val newEntry = HistoryEntry(
                id = now,
                timestampText = formatTimestamp(now),
                rmbFormatted = rmbFormatted,
                remark = remark
            )
            val updated = (listOf(newEntry) + historyEntries.filterNot { it.remark == remark })
                .take(historyLimit)
            historyEntries = updated
            saveHistory(context, updated, historyLimit)
        }
    }

    val openDrawer: () -> Unit = {
        focusManager.clearFocus()
        coroutineScope.launch {
            drawerState.open()
        }
    }

    val selectDrawerPage: (AppPage) -> Unit = { target ->
        currentPage = target
        coroutineScope.launch {
            drawerState.close()
        }
    }

    val homePage = startPage.toAppPage()

    BackHandler(enabled = drawerState.isOpen || currentPage != homePage) {
        when {
            drawerState.isOpen -> {
                coroutineScope.launch { drawerState.close() }
            }
            currentPage == AppPage.CHANGELOG -> {
                currentPage = AppPage.ABOUT
            }
            currentPage == AppPage.ABOUT -> {
                currentPage = AppPage.SETTINGS
            }
            currentPage == AppPage.RED_SKIN_SELECT ||
                currentPage == AppPage.SPECIAL_GOLD_SKIN_SELECT ||
                currentPage == AppPage.GOLD_SKIN_SELECT ||
                currentPage == AppPage.KNIFE_SKIN_SELECT -> {
                currentPage = AppPage.ADVANCED_CONVERT
            }
            currentPage != homePage -> {
                currentPage = homePage
            }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = currentPage.depth == 0 || drawerState.isOpen,
        drawerContent = {
            AppDrawerSheet(
                currentPage = currentPage,
                historyCount = historyEntries.size,
                onSelectPage = selectDrawerPage
            )
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = currentPage,
                transitionSpec = {
                    when {
                        targetState.depth > initialState.depth -> {
                            (slideInHorizontally(tween(260, easing = FastOutSlowInEasing)) { it / 4 } +
                                fadeIn(tween(220, easing = FastOutSlowInEasing))) togetherWith
                                (slideOutHorizontally(tween(240, easing = FastOutSlowInEasing)) { -it / 5 } +
                                    fadeOut(tween(180, easing = FastOutSlowInEasing)))
                        }
                        targetState.depth < initialState.depth -> {
                            (slideInHorizontally(tween(260, easing = FastOutSlowInEasing)) { -it / 4 } +
                                fadeIn(tween(220, easing = FastOutSlowInEasing))) togetherWith
                                (slideOutHorizontally(tween(240, easing = FastOutSlowInEasing)) { it / 5 } +
                                    fadeOut(tween(180, easing = FastOutSlowInEasing)))
                        }
                        else -> {
                            fadeIn(tween(220, easing = FastOutSlowInEasing)) togetherWith
                                fadeOut(tween(220, easing = FastOutSlowInEasing))
                        }
                    }
                },
                label = "PageTransition"
            ) { page ->
            when (page) {
                AppPage.PURE_CONVERT -> PureConvertPage(
                    coinText = pureCoinText,
                    onCoinChange = { rawInput ->
                        if (autoDetectCoinUnit) {
                            val (numericOnly, detectedUnit) = parseCoinInput(rawInput)
                            pureCoinText = numericOnly
                            when {
                                detectedUnit != null -> {
                                    pureCoinUnit = detectedUnit
                                    isPureUnitManuallySelected = false
                                    pureUnitFlashTrigger++
                                }
                                numericOnly.isEmpty() && !isPureUnitManuallySelected -> {
                                    pureCoinUnit = CoinUnit.NONE
                                }
                            }
                        } else {
                            pureCoinText = sanitizeDecimalInput(rawInput)
                        }
                    },
                    coinUnit = pureCoinUnit,
                    unitFlashTrigger = pureUnitFlashTrigger,
                    autoDetectCoinUnit = autoDetectCoinUnit,
                    onCoinUnitChange = { newUnit ->
                        pureCoinUnit = newUnit
                        isPureUnitManuallySelected = true
                        if (newUnit != CoinUnit.NONE) {
                            pureUnitFlashTrigger++
                        }
                    },
                    rateText = pureRateText,
                    onRateChange = { pureRateText = sanitizeDecimalInput(it) },
                    calcState = pureCalcState,
                    autoSaveHistory = autoSaveHistory,
                    onOpenDrawer = openDrawer,
                    onRecordHistory = recordHistory,
                    onReset = {
                        pureCoinText = ""
                        pureCoinUnit = if (autoDetectCoinUnit) CoinUnit.NONE else defaultCoinUnit
                        isPureUnitManuallySelected = !autoDetectCoinUnit
                        pureRateText = defaultRateText
                    }
                )

                AppPage.ADVANCED_CONVERT -> AdvancedConvertPage(
                    coinText = coinText,
                    onCoinChange = { rawInput ->
                        if (autoDetectCoinUnit) {
                            val (numericOnly, detectedUnit) = parseCoinInput(rawInput)
                            coinText = numericOnly
                            when {
                                detectedUnit != null -> {
                                    coinUnit = detectedUnit
                                    isUnitManuallySelected = false
                                    unitFlashTrigger++
                                }
                                numericOnly.isEmpty() && !isUnitManuallySelected -> {
                                    coinUnit = CoinUnit.NONE
                                }
                            }
                        } else {
                            coinText = sanitizeDecimalInput(rawInput)
                        }
                    },
                    coinUnit = coinUnit,
                    unitFlashTrigger = unitFlashTrigger,
                    autoDetectCoinUnit = autoDetectCoinUnit,
                    onCoinUnitChange = { newUnit ->
                        coinUnit = newUnit
                        isUnitManuallySelected = true
                        if (newUnit != CoinUnit.NONE) {
                            unitFlashTrigger++
                        }
                    },
                    rateText = rateText,
                    onRateChange = { rateText = sanitizeDecimalInput(it) },
                    hasRedSkin = hasRedSkin,
                    selectedRedSkins = selectedRedSkins,
                    onRedSkinChange = { hasRedSkin = it },
                    showSpecialGoldSkin = showSpecialGoldSkin,
                    hasSpecialGoldSkin = hasSpecialGoldSkin,
                    selectedSpecialGoldSkins = selectedSpecialGoldSkins,
                    onSpecialGoldSkinChange = { hasSpecialGoldSkin = it },
                    showGoldSkin = showGoldSkin,
                    hasGoldSkin = hasGoldSkin,
                    selectedGoldSkins = selectedGoldSkins,
                    onGoldSkinChange = { hasGoldSkin = it },
                    hasKnifeSkin = hasKnifeSkin,
                    selectedKnifeSkins = selectedKnifeSkins,
                    onKnifeSkinChange = { hasKnifeSkin = it },
                    safeSlots = safeSlots,
                    onSafeSlotsChange = { safeSlots = it },
                    showSmallSafeSlots = showSmallSafeSlots,
                    onToggleSmallSafeSlots = { showSmallSafeSlots = !showSmallSafeSlots },
                    hasNineSlotTrialCard = hasNineSlotTrialCard,
                    onNineSlotTrialCardChange = { hasNineSlotTrialCard = it },
                    nineSlotTrialCardCountText = nineSlotTrialCardCountText,
                    onNineSlotTrialCardCountChange = {
                        nineSlotTrialCardCountText = sanitizeIntegerInput(it)
                    },
                    staminaLevel = staminaLevel,
                    onStaminaLevelChange = { staminaLevel = it },
                    showLowStaminaLevels = showLowStaminaLevels,
                    onToggleLowStaminaLevels = { showLowStaminaLevels = !showLowStaminaLevels },
                    weightLevel = weightLevel,
                    onWeightLevelChange = { weightLevel = it },
                    showLowWeightLevels = showLowWeightLevels,
                    onToggleLowWeightLevels = { showLowWeightLevels = !showLowWeightLevels },
                    divingCenterLevel = divingCenterLevel,
                    onDivingCenterLevelChange = { divingCenterLevel = it },
                    decontaminationLevel = decontaminationLevel,
                    onDecontaminationLevelChange = { decontaminationLevel = it },
                    showOptionalFacilities = showOptionalFacilities,
                    onToggleOptionalFacilities = {
                        showOptionalFacilities = !showOptionalFacilities
                    },
                    hasABigAmmo = hasABigAmmo,
                    onABigAmmoChange = { hasABigAmmo = it },
                    aBigAmmoCountText = aBigAmmoCountText,
                    onABigAmmoCountChange = {
                        aBigAmmoCountText = sanitizeIntegerInput(it, maxDigits = 6)
                    },
                    aBigAmmoUnitPriceText = aBigAmmoUnitPriceText,
                    onABigAmmoUnitPriceChange = {
                        aBigAmmoUnitPriceText = sanitizeDecimalInput(it)
                    },
                    hasTier6Armor = hasTier6Armor,
                    onTier6ArmorChange = { hasTier6Armor = it },
                    tier6ArmorCountText = tier6ArmorCountText,
                    onTier6ArmorCountChange = {
                        tier6ArmorCountText = sanitizeIntegerInput(it, maxDigits = 6)
                    },
                    tier6ArmorUnitPriceText = tier6ArmorUnitPriceText,
                    onTier6ArmorUnitPriceChange = {
                        tier6ArmorUnitPriceText = sanitizeDecimalInput(it)
                    },
                    hasRedAmmo = hasRedAmmo,
                    onRedAmmoChange = { hasRedAmmo = it },
                    redAmmoCountText = redAmmoCountText,
                    onRedAmmoCountChange = {
                        redAmmoCountText = sanitizeIntegerInput(it, maxDigits = 6)
                    },
                    redAmmoUnitPriceText = redAmmoUnitPriceText,
                    onRedAmmoUnitPriceChange = {
                        redAmmoUnitPriceText = sanitizeDecimalInput(it)
                    },
                    showSkinsLayer = showSkinsLayer,
                    onToggleSkinsLayer = { showSkinsLayer = !showSkinsLayer },
                    showSafeBoxLayer = showSafeBoxLayer,
                    onToggleSafeBoxLayer = { showSafeBoxLayer = !showSafeBoxLayer },
                    showBaseLayer = showBaseLayer,
                    onToggleBaseLayer = { showBaseLayer = !showBaseLayer },
                    showSpecialItemsLayer = showSpecialItemsLayer,
                    onToggleSpecialItemsLayer = { showSpecialItemsLayer = !showSpecialItemsLayer },
                    calcState = calcState,
                    autoSaveHistory = autoSaveHistory,
                    onOpenDrawer = openDrawer,
                    onOpenRedSkinSelect = { currentPage = AppPage.RED_SKIN_SELECT },
                    onOpenSpecialGoldSkinSelect = {
                        currentPage = AppPage.SPECIAL_GOLD_SKIN_SELECT
                    },
                    onOpenGoldSkinSelect = { currentPage = AppPage.GOLD_SKIN_SELECT },
                    onOpenKnifeSkinSelect = { currentPage = AppPage.KNIFE_SKIN_SELECT },
                    onRecordHistory = recordHistory,
                    onResetAll = {
                        coinText = ""
                        coinUnit = if (autoDetectCoinUnit) CoinUnit.NONE else defaultCoinUnit
                        isUnitManuallySelected = !autoDetectCoinUnit
                        rateText = defaultRateText
                        hasRedSkin = false
                        selectedRedSkinsRaw = ""
                        hasSpecialGoldSkin = false
                        selectedSpecialGoldSkinsRaw = ""
                        hasGoldSkin = false
                        selectedGoldSkinsRaw = ""
                        hasKnifeSkin = false
                        selectedKnifeSkinsRaw = ""
                        safeSlots = 6
                        showSmallSafeSlots = false
                        hasNineSlotTrialCard = false
                        nineSlotTrialCardCountText = ""
                        staminaLevel = 5
                        showLowStaminaLevels = false
                        weightLevel = 5
                        showLowWeightLevels = false
                        divingCenterLevel = null
                        decontaminationLevel = null
                        showOptionalFacilities = false
                        hasABigAmmo = false
                        aBigAmmoCountText = ""
                        aBigAmmoUnitPriceText = defaultABigAmmoPriceText
                        hasTier6Armor = false
                        tier6ArmorCountText = ""
                        tier6ArmorUnitPriceText = defaultTier6ArmorPriceText
                        hasRedAmmo = false
                        redAmmoCountText = ""
                        redAmmoUnitPriceText = defaultRedAmmoPriceText
                        showSkinsLayer = defaultShowSkinsLayer
                        showSafeBoxLayer = defaultShowSafeBoxLayer
                        showBaseLayer = defaultShowBaseLayer
                        showSpecialItemsLayer = defaultShowSpecialItemsLayer
                    }
                )

                AppPage.HISTORY -> HistoryPage(
                    entries = historyEntries,
                    autoSaveHistory = autoSaveHistory,
                    onOpenDrawer = openDrawer,
                    onDeleteEntry = { entryId ->
                        val updated = historyEntries.filterNot { it.id == entryId }
                        historyEntries = updated
                        saveHistory(context, updated, historyLimit)
                    },
                    onClearAll = {
                        historyEntries = emptyList()
                        saveHistory(context, emptyList(), historyLimit)
                    }
                )

                AppPage.LAB -> LabPage(
                    autoDetectCoinUnit = autoDetectCoinUnit,
                    onAutoDetectCoinUnitChange = { enabled ->
                        autoDetectCoinUnit = enabled
                        saveAutoDetectSetting(context, enabled)
                        if (!enabled) {
                            isUnitManuallySelected = true
                            isPureUnitManuallySelected = true
                        }
                    },
                    showSpecialGoldSkin = showSpecialGoldSkin,
                    onShowSpecialGoldSkinChange = { enabled ->
                        showSpecialGoldSkin = enabled
                        saveShowSpecialGoldSkinSetting(context, enabled)
                    },
                    showGoldSkin = showGoldSkin,
                    onShowGoldSkinChange = { enabled ->
                        showGoldSkin = enabled
                        saveShowGoldSkinSetting(context, enabled)
                    },
                    onOpenDrawer = openDrawer
                )

                AppPage.SETTINGS -> SettingsPage(
                    startPage = startPage,
                    onStartPageChange = { option ->
                        startPage = option
                        saveStringSetting(context, KEY_START_PAGE, option.name)
                    },
                    defaultRateText = defaultRateText,
                    onDefaultRateChange = { raw ->
                        val cleaned = sanitizeDecimalInput(raw)
                        val oldDefault = defaultRateText
                        defaultRateText = cleaned
                        saveStringSetting(context, KEY_DEFAULT_RATE, cleaned)
                        if (pureRateText.isEmpty() || pureRateText == oldDefault) {
                            pureRateText = cleaned
                        }
                        if (rateText.isEmpty() || rateText == oldDefault) {
                            rateText = cleaned
                        }
                    },
                    defaultCoinUnit = defaultCoinUnit,
                    onDefaultCoinUnitChange = { unit ->
                        defaultCoinUnit = unit
                        saveStringSetting(context, KEY_DEFAULT_COIN_UNIT, unit.name)
                        if (pureCoinText.isEmpty()) {
                            pureCoinUnit = unit
                        }
                        if (coinText.isEmpty()) {
                            coinUnit = unit
                        }
                    },
                    defaultABigAmmoPriceText = defaultABigAmmoPriceText,
                    onDefaultABigAmmoPriceChange = { raw ->
                        val cleaned = sanitizeDecimalInput(raw)
                        val oldDefault = defaultABigAmmoPriceText
                        defaultABigAmmoPriceText = cleaned
                        saveStringSetting(context, KEY_DEFAULT_A_BIG_AMMO_PRICE, cleaned)
                        if (aBigAmmoUnitPriceText.isEmpty() || aBigAmmoUnitPriceText == oldDefault) {
                            aBigAmmoUnitPriceText = cleaned
                        }
                    },
                    defaultTier6ArmorPriceText = defaultTier6ArmorPriceText,
                    onDefaultTier6ArmorPriceChange = { raw ->
                        val cleaned = sanitizeDecimalInput(raw)
                        val oldDefault = defaultTier6ArmorPriceText
                        defaultTier6ArmorPriceText = cleaned
                        saveStringSetting(context, KEY_DEFAULT_TIER6_ARMOR_PRICE, cleaned)
                        if (tier6ArmorUnitPriceText.isEmpty() || tier6ArmorUnitPriceText == oldDefault) {
                            tier6ArmorUnitPriceText = cleaned
                        }
                    },
                    defaultRedAmmoPriceText = defaultRedAmmoPriceText,
                    onDefaultRedAmmoPriceChange = { raw ->
                        val cleaned = sanitizeDecimalInput(raw)
                        val oldDefault = defaultRedAmmoPriceText
                        defaultRedAmmoPriceText = cleaned
                        saveStringSetting(context, KEY_DEFAULT_RED_AMMO_PRICE, cleaned)
                        if (redAmmoUnitPriceText.isEmpty() || redAmmoUnitPriceText == oldDefault) {
                            redAmmoUnitPriceText = cleaned
                        }
                    },
                    amountPrecision = amountPrecision,
                    onAmountPrecisionChange = { precision ->
                        amountPrecision = precision
                        saveStringSetting(context, KEY_AMOUNT_PRECISION, precision.name)
                    },
                    amountRounding = amountRounding,
                    onAmountRoundingChange = { rounding ->
                        amountRounding = rounding
                        saveStringSetting(context, KEY_AMOUNT_ROUNDING, rounding.name)
                    },
                    remarkSeparator = remarkSeparator,
                    onRemarkSeparatorChange = { separator ->
                        remarkSeparator = separator
                        saveStringSetting(context, KEY_REMARK_SEPARATOR, separator.name)
                    },
                    includeSpecialItemPriceInRemark = includeSpecialItemPriceInRemark,
                    onIncludeSpecialItemPriceInRemarkChange = { enabled ->
                        includeSpecialItemPriceInRemark = enabled
                        saveBooleanSetting(
                            context,
                            KEY_INCLUDE_SPECIAL_ITEM_PRICE_IN_REMARK,
                            enabled
                        )
                    },
                    themeMode = themeMode,
                    onThemeModeChange = onThemeModeChange,
                    defaultShowSkinsLayer = defaultShowSkinsLayer,
                    onDefaultShowSkinsLayerChange = { visible ->
                        defaultShowSkinsLayer = visible
                        showSkinsLayer = visible
                        saveBooleanSetting(context, KEY_DEFAULT_SHOW_SKINS_LAYER, visible)
                    },
                    defaultShowSafeBoxLayer = defaultShowSafeBoxLayer,
                    onDefaultShowSafeBoxLayerChange = { visible ->
                        defaultShowSafeBoxLayer = visible
                        showSafeBoxLayer = visible
                        saveBooleanSetting(context, KEY_DEFAULT_SHOW_SAFEBOX_LAYER, visible)
                    },
                    defaultShowBaseLayer = defaultShowBaseLayer,
                    onDefaultShowBaseLayerChange = { visible ->
                        defaultShowBaseLayer = visible
                        showBaseLayer = visible
                        saveBooleanSetting(context, KEY_DEFAULT_SHOW_BASE_LAYER, visible)
                    },
                    defaultShowSpecialItemsLayer = defaultShowSpecialItemsLayer,
                    onDefaultShowSpecialItemsLayerChange = { visible ->
                        defaultShowSpecialItemsLayer = visible
                        showSpecialItemsLayer = visible
                        saveBooleanSetting(context, KEY_DEFAULT_SHOW_SPECIAL_ITEMS_LAYER, visible)
                    },
                    autoSaveHistory = autoSaveHistory,
                    onAutoSaveHistoryChange = { enabled ->
                        autoSaveHistory = enabled
                        saveBooleanSetting(context, KEY_AUTO_SAVE_HISTORY, enabled)
                    },
                    historyLimit = historyLimit,
                    onHistoryLimitChange = { limit ->
                        historyLimit = limit
                        saveHistoryLimitSetting(context, limit)
                        if (historyEntries.size > limit) {
                            val trimmed = historyEntries.take(limit)
                            historyEntries = trimmed
                            saveHistory(context, trimmed, limit)
                        }
                    },
                    historyCount = historyEntries.size,
                    onClearHistory = {
                        historyEntries = emptyList()
                        saveHistory(context, emptyList(), historyLimit)
                    },
                    onResetDefaults = {
                        val oldDefaultRate = defaultRateText
                        val oldDefaultABigAmmoPrice = defaultABigAmmoPriceText
                        val oldDefaultTier6ArmorPrice = defaultTier6ArmorPriceText
                        val oldDefaultRedAmmoPrice = defaultRedAmmoPriceText
                        startPage = StartPageOption.PURE
                        defaultRateText = ""
                        if (pureRateText == oldDefaultRate) pureRateText = ""
                        if (rateText == oldDefaultRate) rateText = ""
                        defaultCoinUnit = CoinUnit.M
                        if (pureCoinText.isEmpty()) pureCoinUnit = CoinUnit.M
                        if (coinText.isEmpty()) coinUnit = CoinUnit.M
                        defaultABigAmmoPriceText = DEFAULT_A_BIG_AMMO_UNIT_PRICE
                        if (aBigAmmoUnitPriceText == oldDefaultABigAmmoPrice) {
                            aBigAmmoUnitPriceText = DEFAULT_A_BIG_AMMO_UNIT_PRICE
                        }
                        defaultTier6ArmorPriceText = DEFAULT_TIER6_ARMOR_UNIT_PRICE
                        if (tier6ArmorUnitPriceText == oldDefaultTier6ArmorPrice) {
                            tier6ArmorUnitPriceText = DEFAULT_TIER6_ARMOR_UNIT_PRICE
                        }
                        defaultRedAmmoPriceText = ""
                        if (redAmmoUnitPriceText == oldDefaultRedAmmoPrice) {
                            redAmmoUnitPriceText = ""
                        }
                        amountPrecision = AmountPrecision.TWO_DECIMALS
                        amountRounding = AmountRounding.HALF_UP
                        remarkSeparator = RemarkSeparator.PIPE
                        includeSpecialItemPriceInRemark = true
                        onThemeModeChange(ThemeMode.SYSTEM)
                        defaultShowSkinsLayer = true
                        showSkinsLayer = true
                        defaultShowSafeBoxLayer = true
                        showSafeBoxLayer = true
                        defaultShowBaseLayer = true
                        showBaseLayer = true
                        defaultShowSpecialItemsLayer = true
                        showSpecialItemsLayer = true
                        autoSaveHistory = true
                        historyLimit = DEFAULT_HISTORY_LIMIT
                        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                            .edit()
                            .putString(KEY_START_PAGE, StartPageOption.PURE.name)
                            .putString(KEY_DEFAULT_RATE, "")
                            .putString(KEY_DEFAULT_COIN_UNIT, CoinUnit.M.name)
                            .putString(KEY_DEFAULT_A_BIG_AMMO_PRICE, DEFAULT_A_BIG_AMMO_UNIT_PRICE)
                            .putString(KEY_DEFAULT_TIER6_ARMOR_PRICE, DEFAULT_TIER6_ARMOR_UNIT_PRICE)
                            .putString(KEY_DEFAULT_RED_AMMO_PRICE, "")
                            .putString(KEY_AMOUNT_PRECISION, AmountPrecision.TWO_DECIMALS.name)
                            .putString(KEY_AMOUNT_ROUNDING, AmountRounding.HALF_UP.name)
                            .putString(KEY_REMARK_SEPARATOR, RemarkSeparator.PIPE.name)
                            .putBoolean(KEY_INCLUDE_SPECIAL_ITEM_PRICE_IN_REMARK, true)
                            .putString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
                            .putBoolean(KEY_DEFAULT_SHOW_SKINS_LAYER, true)
                            .putBoolean(KEY_DEFAULT_SHOW_SAFEBOX_LAYER, true)
                            .putBoolean(KEY_DEFAULT_SHOW_BASE_LAYER, true)
                            .putBoolean(KEY_DEFAULT_SHOW_SPECIAL_ITEMS_LAYER, true)
                            .putBoolean(KEY_AUTO_SAVE_HISTORY, true)
                            .putInt(KEY_HISTORY_LIMIT, DEFAULT_HISTORY_LIMIT)
                            .apply()
                    },
                    onOpenAbout = { currentPage = AppPage.ABOUT },
                    onOpenDrawer = openDrawer
                )

                AppPage.ABOUT -> AboutPage(
                    onOpenChangelog = { currentPage = AppPage.CHANGELOG },
                    onBack = { currentPage = AppPage.SETTINGS }
                )

                AppPage.CHANGELOG -> ChangelogPage(
                    onBack = { currentPage = AppPage.ABOUT }
                )

                AppPage.RED_SKIN_SELECT -> SkinSelectPage(
                    title = "选择红皮",
                    options = RED_SKIN_OPTIONS,
                    selectedNames = selectedRedSkins,
                    onToggleSkin = { name ->
                        selectedRedSkinsRaw = toggleSkinSelection(
                            currentRaw = selectedRedSkinsRaw,
                            skinName = name,
                            catalog = RED_SKIN_OPTIONS
                        )
                    },
                    onClearSelection = { selectedRedSkinsRaw = "" },
                    onBack = { currentPage = AppPage.ADVANCED_CONVERT }
                )

                AppPage.SPECIAL_GOLD_SKIN_SELECT -> SkinSelectPage(
                    title = "选择特殊金皮",
                    options = SPECIAL_GOLD_SKIN_OPTIONS,
                    selectedNames = selectedSpecialGoldSkins,
                    onToggleSkin = { name ->
                        selectedSpecialGoldSkinsRaw = toggleSkinSelection(
                            currentRaw = selectedSpecialGoldSkinsRaw,
                            skinName = name,
                            catalog = SPECIAL_GOLD_SKIN_OPTIONS
                        )
                    },
                    onClearSelection = { selectedSpecialGoldSkinsRaw = "" },
                    onBack = { currentPage = AppPage.ADVANCED_CONVERT }
                )

                AppPage.GOLD_SKIN_SELECT -> SkinSelectPage(
                    title = "选择金皮",
                    options = GOLD_SKIN_OPTIONS,
                    selectedNames = selectedGoldSkins,
                    onToggleSkin = { name ->
                        selectedGoldSkinsRaw = toggleSkinSelection(
                            currentRaw = selectedGoldSkinsRaw,
                            skinName = name,
                            catalog = GOLD_SKIN_OPTIONS
                        )
                    },
                    onClearSelection = { selectedGoldSkinsRaw = "" },
                    onBack = { currentPage = AppPage.ADVANCED_CONVERT }
                )

                AppPage.KNIFE_SKIN_SELECT -> SkinSelectPage(
                    title = "选择刀皮",
                    options = KNIFE_SKIN_OPTIONS,
                    selectedNames = selectedKnifeSkins,
                    onToggleSkin = { name ->
                        selectedKnifeSkinsRaw = toggleSkinSelection(
                            currentRaw = selectedKnifeSkinsRaw,
                            skinName = name,
                            catalog = KNIFE_SKIN_OPTIONS
                        )
                    },
                    onClearSelection = { selectedKnifeSkinsRaw = "" },
                    onBack = { currentPage = AppPage.ADVANCED_CONVERT }
                )
            }
        }
        }
    }
}

@Composable
private fun AppDrawerSheet(
    currentPage: AppPage,
    historyCount: Int,
    onSelectPage: (AppPage) -> Unit
) {
    ModalDrawerSheet(
        modifier = Modifier.widthIn(max = 300.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            HavocBrandLogo(modifier = Modifier.size(width = 34.dp, height = 38.dp))
            Column(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "哈夫币折算器",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "天空属于哈夫克！",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        NavigationDrawerItem(
            icon = { PureCoinIcon() },
            label = { Text("纯币折算") },
            selected = currentPage == AppPage.PURE_CONVERT,
            onClick = { onSelectPage(AppPage.PURE_CONVERT) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { ConvertIcon() },
            label = { Text("进阶折算") },
            selected = currentPage == AppPage.ADVANCED_CONVERT ||
                currentPage == AppPage.RED_SKIN_SELECT ||
                currentPage == AppPage.SPECIAL_GOLD_SKIN_SELECT ||
                currentPage == AppPage.GOLD_SKIN_SELECT ||
                currentPage == AppPage.KNIFE_SKIN_SELECT,
            onClick = { onSelectPage(AppPage.ADVANCED_CONVERT) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { HistoryIcon() },
            label = { Text("折算历史") },
            badge = if (historyCount > 0) {
                {
                    Text(
                        text = historyCount.toString(),
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            } else {
                null
            },
            selected = currentPage == AppPage.HISTORY,
            onClick = { onSelectPage(AppPage.HISTORY) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        NavigationDrawerItem(
            icon = { LabIcon() },
            label = { Text("实验室") },
            selected = currentPage == AppPage.LAB,
            onClick = { onSelectPage(AppPage.LAB) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        NavigationDrawerItem(
            icon = { SettingsIcon() },
            label = { Text("设置") },
            selected = currentPage == AppPage.SETTINGS ||
                currentPage == AppPage.ABOUT ||
                currentPage == AppPage.CHANGELOG,
            onClick = { onSelectPage(AppPage.SETTINGS) },
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PureConvertPage(
    coinText: String,
    onCoinChange: (String) -> Unit,
    coinUnit: CoinUnit,
    unitFlashTrigger: Int,
    autoDetectCoinUnit: Boolean,
    onCoinUnitChange: (CoinUnit) -> Unit,
    rateText: String,
    onRateChange: (String) -> Unit,
    calcState: CalcState,
    autoSaveHistory: Boolean,
    onOpenDrawer: () -> Unit,
    onRecordHistory: (rmbFormatted: String, remark: String) -> Unit,
    onReset: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val isRateZeroOrInvalid = rateText.isNotBlank() &&
        (rateText.toBigDecimalOrNull()?.let { it.compareTo(BigDecimal.ZERO) <= 0 } ?: true)

    val pillShape = RoundedCornerShape(28.dp)
    val pillColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainerLow,
        unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
        focusedBorderColor = MaterialTheme.colorScheme.primary
    )
    val inputTextStyle = MaterialTheme.typography.titleMedium
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        MenuIcon()
                    }
                },
                title = {}
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(72.dp))

            HavocBrandLogo(modifier = Modifier.size(width = 56.dp, height = 62.dp))

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "欢迎",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "现金不够使？在这算算吧！",
                style = MaterialTheme.typography.bodyMedium,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = coinText,
                onValueChange = onCoinChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
                textStyle = inputTextStyle,
                shape = pillShape,
                colors = pillColors,
                placeholder = {
                    Text(
                        text = if (autoDetectCoinUnit && coinUnit == CoinUnit.NONE) {
                            "输入哈夫币数量（如 53M）"
                        } else {
                            "输入哈夫币数量（${coinUnit.placeholder}）"
                        },
                        style = inputTextStyle
                    )
                },
                suffix = if (coinUnit.suffix.isNotEmpty()) {
                    {
                        FlashingUnitSuffix(
                            text = coinUnit.suffix,
                            flashTrigger = unitFlashTrigger
                        )
                    }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (autoDetectCoinUnit) {
                        KeyboardType.Ascii
                    } else {
                        KeyboardType.Decimal
                    },
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                )
            )

            Spacer(modifier = Modifier.height(14.dp))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                val units = CoinUnit.entries
                units.forEachIndexed { index, unit ->
                    SegmentedButton(
                        selected = coinUnit == unit,
                        onClick = { onCoinUnitChange(unit) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = units.size
                        ),
                        icon = {},
                        label = {
                            Text(
                                text = unit.buttonLabel,
                                style = MaterialTheme.typography.labelLarge,
                                maxLines = 1
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            OutlinedTextField(
                value = rateText,
                onValueChange = onRateChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 64.dp),
                textStyle = inputTextStyle,
                shape = pillShape,
                colors = pillColors,
                placeholder = {
                    Text(
                        text = "比例（如 53）",
                        style = inputTextStyle
                    )
                },
                prefix = {
                    Text(
                        text = "1 : ",
                        style = inputTextStyle
                    )
                },
                suffix = {
                    Text(
                        text = "万",
                        style = inputTextStyle
                    )
                },
                isError = isRateZeroOrInvalid,
                supportingText = if (isRateZeroOrInvalid) {
                    { Text("比例必须大于 0") }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = { focusManager.clearFocus() }
                )
            )

            Spacer(modifier = Modifier.height(32.dp))

            val amountText = when (calcState) {
                is CalcState.Valid -> "¥${calcState.rmbFormatted}"
                is CalcState.Incomplete -> "¥--"
                is CalcState.Error -> "¥--"
            }

            Text(
                text = amountText,
                style = MaterialTheme.typography.displayMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = if (calcState is CalcState.Valid) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                }
            )

            if (calcState is CalcState.Error) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = calcState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        if (calcState is CalcState.Valid) {
                            focusManager.clearFocus()
                            copyToClipboard(context, calcState.rmbFormatted)
                            onRecordHistory(calcState.rmbFormatted, calcState.remark)
                            coroutineScope.launch {
                                snackbarHostState.currentSnackbarData?.dismiss()
                                snackbarHostState.showSnackbar(
                                    message = if (autoSaveHistory) {
                                        "已复制金额并保存到折算历史"
                                    } else {
                                        "已复制金额到剪贴板"
                                    },
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    },
                    enabled = calcState is CalcState.Valid,
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "复制金额",
                        style = MaterialTheme.typography.titleSmall
                    )
                }

                FilledTonalButton(
                    onClick = {
                        focusManager.clearFocus()
                        onReset()
                    },
                    contentPadding = PaddingValues(horizontal = 28.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "重置",
                        style = MaterialTheme.typography.titleSmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AdvancedConvertPage(
    coinText: String,
    onCoinChange: (String) -> Unit,
    coinUnit: CoinUnit,
    unitFlashTrigger: Int,
    autoDetectCoinUnit: Boolean,
    onCoinUnitChange: (CoinUnit) -> Unit,
    rateText: String,
    onRateChange: (String) -> Unit,
    hasRedSkin: Boolean,
    selectedRedSkins: List<String>,
    onRedSkinChange: (Boolean) -> Unit,
    showSpecialGoldSkin: Boolean,
    hasSpecialGoldSkin: Boolean,
    selectedSpecialGoldSkins: List<String>,
    onSpecialGoldSkinChange: (Boolean) -> Unit,
    showGoldSkin: Boolean,
    hasGoldSkin: Boolean,
    selectedGoldSkins: List<String>,
    onGoldSkinChange: (Boolean) -> Unit,
    hasKnifeSkin: Boolean,
    selectedKnifeSkins: List<String>,
    onKnifeSkinChange: (Boolean) -> Unit,
    safeSlots: Int,
    onSafeSlotsChange: (Int) -> Unit,
    showSmallSafeSlots: Boolean,
    onToggleSmallSafeSlots: () -> Unit,
    hasNineSlotTrialCard: Boolean,
    onNineSlotTrialCardChange: (Boolean) -> Unit,
    nineSlotTrialCardCountText: String,
    onNineSlotTrialCardCountChange: (String) -> Unit,
    staminaLevel: Int,
    onStaminaLevelChange: (Int) -> Unit,
    showLowStaminaLevels: Boolean,
    onToggleLowStaminaLevels: () -> Unit,
    weightLevel: Int,
    onWeightLevelChange: (Int) -> Unit,
    showLowWeightLevels: Boolean,
    onToggleLowWeightLevels: () -> Unit,
    divingCenterLevel: Int?,
    onDivingCenterLevelChange: (Int?) -> Unit,
    decontaminationLevel: Int?,
    onDecontaminationLevelChange: (Int?) -> Unit,
    showOptionalFacilities: Boolean,
    onToggleOptionalFacilities: () -> Unit,
    hasABigAmmo: Boolean,
    onABigAmmoChange: (Boolean) -> Unit,
    aBigAmmoCountText: String,
    onABigAmmoCountChange: (String) -> Unit,
    aBigAmmoUnitPriceText: String,
    onABigAmmoUnitPriceChange: (String) -> Unit,
    hasTier6Armor: Boolean,
    onTier6ArmorChange: (Boolean) -> Unit,
    tier6ArmorCountText: String,
    onTier6ArmorCountChange: (String) -> Unit,
    tier6ArmorUnitPriceText: String,
    onTier6ArmorUnitPriceChange: (String) -> Unit,
    hasRedAmmo: Boolean,
    onRedAmmoChange: (Boolean) -> Unit,
    redAmmoCountText: String,
    onRedAmmoCountChange: (String) -> Unit,
    redAmmoUnitPriceText: String,
    onRedAmmoUnitPriceChange: (String) -> Unit,
    showSkinsLayer: Boolean,
    onToggleSkinsLayer: () -> Unit,
    showSafeBoxLayer: Boolean,
    onToggleSafeBoxLayer: () -> Unit,
    showBaseLayer: Boolean,
    onToggleBaseLayer: () -> Unit,
    showSpecialItemsLayer: Boolean,
    onToggleSpecialItemsLayer: () -> Unit,
    calcState: CalcState,
    autoSaveHistory: Boolean,
    onOpenDrawer: () -> Unit,
    onOpenRedSkinSelect: () -> Unit,
    onOpenSpecialGoldSkinSelect: () -> Unit,
    onOpenGoldSkinSelect: () -> Unit,
    onOpenKnifeSkinSelect: () -> Unit,
    onRecordHistory: (rmbFormatted: String, remark: String) -> Unit,
    onResetAll: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    val isRateZeroOrInvalid = rateText.isNotBlank() &&
        (rateText.toBigDecimalOrNull()?.let { it.compareTo(BigDecimal.ZERO) <= 0 } ?: true)
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        MenuIcon()
                    }
                },
                title = {
                    Text(
                        text = "进阶折算",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            onResetAll()
                        }
                    ) {
                        Text("重置")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            InputSectionCard(
                coinText = coinText,
                onCoinChange = onCoinChange,
                coinUnit = coinUnit,
                unitFlashTrigger = unitFlashTrigger,
                autoDetectCoinUnit = autoDetectCoinUnit,
                onCoinUnitChange = onCoinUnitChange,
                rateText = rateText,
                onRateChange = onRateChange,
                isRateError = isRateZeroOrInvalid,
                onNext = { focusManager.moveFocus(FocusDirection.Down) },
                onDone = { focusManager.clearFocus() }
            )

            SectionGroupHeader(
                title = "红皮&刀皮",
                expanded = showSkinsLayer,
                onToggle = onToggleSkinsLayer
            )

            AnimatedVisibility(visible = showSkinsLayer) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SkinOptionCard(
                        label = "红皮",
                        emptyEntryTitle = "选择拥有的红皮",
                        selectedSkins = selectedRedSkins,
                        checked = hasRedSkin,
                        onCheckedChange = onRedSkinChange,
                        onOpenSelect = {
                            focusManager.clearFocus()
                            onOpenRedSkinSelect()
                        }
                    )

                    if (showSpecialGoldSkin) {
                        SkinOptionCard(
                            label = "特殊金皮",
                            emptyEntryTitle = "选择拥有的特殊金皮",
                            selectedSkins = selectedSpecialGoldSkins,
                            checked = hasSpecialGoldSkin,
                            onCheckedChange = onSpecialGoldSkinChange,
                            onOpenSelect = {
                                focusManager.clearFocus()
                                onOpenSpecialGoldSkinSelect()
                            }
                        )
                    }

                    if (showGoldSkin) {
                        SkinOptionCard(
                            label = "金皮",
                            emptyEntryTitle = "选择拥有的金皮",
                            selectedSkins = selectedGoldSkins,
                            checked = hasGoldSkin,
                            onCheckedChange = onGoldSkinChange,
                            onOpenSelect = {
                                focusManager.clearFocus()
                                onOpenGoldSkinSelect()
                            }
                        )
                    }

                    SkinOptionCard(
                        label = "刀皮",
                        emptyEntryTitle = "选择拥有的刀皮",
                        selectedSkins = selectedKnifeSkins,
                        checked = hasKnifeSkin,
                        onCheckedChange = onKnifeSkinChange,
                        onOpenSelect = {
                            focusManager.clearFocus()
                            onOpenKnifeSkinSelect()
                        }
                    )
                }
            }

            SectionGroupHeader(
                title = "保险",
                expanded = showSafeBoxLayer,
                onToggle = onToggleSafeBoxLayer
            )

            AnimatedVisibility(visible = showSafeBoxLayer) {
                SafeBoxCard(
                    safeSlots = safeSlots,
                    onSafeSlotsChange = onSafeSlotsChange,
                    showSmallSafeSlots = showSmallSafeSlots,
                    onToggleSmallSafeSlots = onToggleSmallSafeSlots,
                    hasNineSlotTrialCard = hasNineSlotTrialCard,
                    onNineSlotTrialCardChange = onNineSlotTrialCardChange,
                    nineSlotTrialCardCountText = nineSlotTrialCardCountText,
                    onNineSlotTrialCardCountChange = onNineSlotTrialCardCountChange,
                    onDone = { focusManager.clearFocus() }
                )
            }

            SectionGroupHeader(
                title = "特勤处",
                expanded = showBaseLayer,
                onToggle = onToggleBaseLayer
            )

            AnimatedVisibility(visible = showBaseLayer) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StandaloneLevelCard(
                        label = "体力（训练中心等级）",
                        primaryOptions = listOf(5, 6, 7),
                        secondaryOptions = listOf(1, 2, 3, 4),
                        secondaryLabel = "1-4级",
                        unitSuffix = "级",
                        selected = staminaLevel,
                        expanded = showLowStaminaLevels,
                        onToggleExpanded = onToggleLowStaminaLevels,
                        onSelect = onStaminaLevelChange
                    )

                    StandaloneLevelCard(
                        label = "负重（靶场等级）",
                        primaryOptions = listOf(5, 6, 7),
                        secondaryOptions = listOf(1, 2, 3, 4),
                        secondaryLabel = "1-4级",
                        unitSuffix = "级",
                        selected = weightLevel,
                        expanded = showLowWeightLevels,
                        onToggleExpanded = onToggleLowWeightLevels,
                        onSelect = onWeightLevelChange
                    )

                    OptionalFacilitiesCard(
                        divingCenterLevel = divingCenterLevel,
                        onDivingCenterLevelChange = onDivingCenterLevelChange,
                        decontaminationLevel = decontaminationLevel,
                        onDecontaminationLevelChange = onDecontaminationLevelChange,
                        showOptionalFacilities = showOptionalFacilities,
                        onToggleOptionalFacilities = onToggleOptionalFacilities
                    )
                }
            }

            SectionGroupHeader(
                title = "特殊物品",
                expanded = showSpecialItemsLayer,
                onToggle = onToggleSpecialItemsLayer
            )

            AnimatedVisibility(visible = showSpecialItemsLayer) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpecialItemOptionCard(
                        label = "A大子弹",
                        unitSuffix = "发",
                        enabled = hasABigAmmo,
                        onEnabledChange = onABigAmmoChange,
                        countText = aBigAmmoCountText,
                        onCountChange = onABigAmmoCountChange,
                        unitPriceText = aBigAmmoUnitPriceText,
                        onUnitPriceChange = onABigAmmoUnitPriceChange,
                        onDone = { focusManager.clearFocus() }
                    )

                    SpecialItemOptionCard(
                        label = "六套",
                        unitSuffix = "套",
                        enabled = hasTier6Armor,
                        onEnabledChange = onTier6ArmorChange,
                        countText = tier6ArmorCountText,
                        onCountChange = onTier6ArmorCountChange,
                        unitPriceText = tier6ArmorUnitPriceText,
                        onUnitPriceChange = onTier6ArmorUnitPriceChange,
                        onDone = { focusManager.clearFocus() }
                    )

                    SpecialItemOptionCard(
                        label = "红弹",
                        unitSuffix = "组",
                        enabled = hasRedAmmo,
                        onEnabledChange = onRedAmmoChange,
                        countText = redAmmoCountText,
                        onCountChange = onRedAmmoCountChange,
                        unitPriceText = redAmmoUnitPriceText,
                        onUnitPriceChange = onRedAmmoUnitPriceChange,
                        onDone = { focusManager.clearFocus() }
                    )
                }
            }

            ResultSectionCard(
                calcState = calcState,
                showRemark = true,
                copyButtonText = "复制备注",
                onCopyClick = { rmbFormatted, textToCopy ->
                    focusManager.clearFocus()
                    copyToClipboard(context, textToCopy)
                    onRecordHistory(rmbFormatted, textToCopy)
                    coroutineScope.launch {
                        snackbarHostState.currentSnackbarData?.dismiss()
                        snackbarHostState.showSnackbar(
                            message = if (autoSaveHistory) {
                                "已复制备注并保存到折算历史"
                            } else {
                                "已复制备注到剪贴板"
                            },
                            duration = SnackbarDuration.Short
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HistoryPage(
    entries: List<HistoryEntry>,
    autoSaveHistory: Boolean,
    onOpenDrawer: () -> Unit,
    onDeleteEntry: (Long) -> Unit,
    onClearAll: () -> Unit
) {
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        MenuIcon()
                    }
                },
                title = {
                    Text(
                        text = "折算历史",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    if (entries.isNotEmpty()) {
                        TextButton(
                            onClick = {
                                onClearAll()
                                coroutineScope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar(
                                        message = "已清空历史记录",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            }
                        ) {
                            Text("清空")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        if (entries.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "暂无折算历史",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = if (autoSaveHistory) {
                            "在「纯币折算」或「进阶折算」点击复制后会自动保存"
                        } else {
                            "当前已关闭自动保存历史，可在「设置」中重新开启"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(entries, key = { it.id }) { entry ->
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "¥${entry.rmbFormatted}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontFamily = FontFamily.Monospace,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = entry.timestampText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }

                            Surface(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceContainerHigh
                            ) {
                                Text(
                                    text = entry.remark,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                TextButton(onClick = { onDeleteEntry(entry.id) }) {
                                    Text("删除")
                                }
                                FilledTonalButton(
                                    onClick = {
                                        copyToClipboard(context, entry.remark)
                                        coroutineScope.launch {
                                            snackbarHostState.currentSnackbarData?.dismiss()
                                            snackbarHostState.showSnackbar(
                                                message = "已复制备注到剪贴板",
                                                duration = SnackbarDuration.Short
                                            )
                                        }
                                    }
                                ) {
                                    Text("复制")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LabPage(
    autoDetectCoinUnit: Boolean,
    onAutoDetectCoinUnitChange: (Boolean) -> Unit,
    showSpecialGoldSkin: Boolean,
    onShowSpecialGoldSkinChange: (Boolean) -> Unit,
    showGoldSkin: Boolean,
    onShowGoldSkinChange: (Boolean) -> Unit,
    onOpenDrawer: () -> Unit
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        MenuIcon()
                    }
                },
                title = {
                    Text(
                        text = "实验室",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .toggleable(
                            value = autoDetectCoinUnit,
                            role = Role.Switch,
                            onValueChange = onAutoDetectCoinUnitChange
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "自动检测哈夫币单位",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "开启后允许输入 K / M / B 自动切换单位并隐藏字母；关闭时仅允许输入数字，手动选择单位。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = autoDetectCoinUnit,
                        onCheckedChange = null
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .toggleable(
                            value = showSpecialGoldSkin,
                            role = Role.Switch,
                            onValueChange = onShowSpecialGoldSkinChange
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "显示特殊金皮",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "开启后在「进阶折算」中显示独立的特殊金皮选项，内置列表仅供参考，不一定完整。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = showSpecialGoldSkin,
                        onCheckedChange = null
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .toggleable(
                            value = showGoldSkin,
                            role = Role.Switch,
                            onValueChange = onShowGoldSkinChange
                        )
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = "显示普通金皮",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "开启后在「进阶折算」中显示独立的金皮选项，内置列表仅供参考，不一定完整。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = showGoldSkin,
                        onCheckedChange = null
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp, start = 4.dp, end = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsPage(
    startPage: StartPageOption,
    onStartPageChange: (StartPageOption) -> Unit,
    defaultRateText: String,
    onDefaultRateChange: (String) -> Unit,
    defaultCoinUnit: CoinUnit,
    onDefaultCoinUnitChange: (CoinUnit) -> Unit,
    defaultABigAmmoPriceText: String,
    onDefaultABigAmmoPriceChange: (String) -> Unit,
    defaultTier6ArmorPriceText: String,
    onDefaultTier6ArmorPriceChange: (String) -> Unit,
    defaultRedAmmoPriceText: String,
    onDefaultRedAmmoPriceChange: (String) -> Unit,
    amountPrecision: AmountPrecision,
    onAmountPrecisionChange: (AmountPrecision) -> Unit,
    amountRounding: AmountRounding,
    onAmountRoundingChange: (AmountRounding) -> Unit,
    remarkSeparator: RemarkSeparator,
    onRemarkSeparatorChange: (RemarkSeparator) -> Unit,
    includeSpecialItemPriceInRemark: Boolean,
    onIncludeSpecialItemPriceInRemarkChange: (Boolean) -> Unit,
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    defaultShowSkinsLayer: Boolean,
    onDefaultShowSkinsLayerChange: (Boolean) -> Unit,
    defaultShowSafeBoxLayer: Boolean,
    onDefaultShowSafeBoxLayerChange: (Boolean) -> Unit,
    defaultShowBaseLayer: Boolean,
    onDefaultShowBaseLayerChange: (Boolean) -> Unit,
    defaultShowSpecialItemsLayer: Boolean,
    onDefaultShowSpecialItemsLayerChange: (Boolean) -> Unit,
    autoSaveHistory: Boolean,
    onAutoSaveHistoryChange: (Boolean) -> Unit,
    historyLimit: Int,
    onHistoryLimitChange: (Int) -> Unit,
    historyCount: Int,
    onClearHistory: () -> Unit,
    onResetDefaults: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenDrawer: () -> Unit
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val versionName = remember(context) { getAppVersionName(context) }

    var showClearHistoryDialog by remember { mutableStateOf(false) }
    var showResetDefaultsDialog by remember { mutableStateOf(false) }

    val isDefaultRateInvalid = defaultRateText.isNotBlank() &&
        (defaultRateText.toBigDecimalOrNull()?.let { it.compareTo(BigDecimal.ZERO) <= 0 } ?: true)

    val sampleCalc = remember(amountPrecision, amountRounding, remarkSeparator) {
        calculateResult(
            coinText = "53.05",
            coinUnit = CoinUnit.M,
            rateText = "53",
            precision = amountPrecision,
            rounding = amountRounding,
            remarkSeparator = remarkSeparator
        )
    }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    if (showClearHistoryDialog) {
        AlertDialog(
            onDismissRequest = { showClearHistoryDialog = false },
            title = { Text("清空折算历史") },
            text = {
                Text("确定要清空全部 $historyCount 条折算历史吗？此操作不可撤销。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearHistoryDialog = false
                        onClearHistory()
                        coroutineScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(
                                message = "已清空折算历史",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                ) {
                    Text(
                        text = "清空",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearHistoryDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    if (showResetDefaultsDialog) {
        AlertDialog(
            onDismissRequest = { showResetDefaultsDialog = false },
            title = { Text("恢复默认设置") },
            text = {
                Text("将「设置」中的所有偏好恢复为默认值，不影响已保存的折算历史和实验室开关。")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showResetDefaultsDialog = false
                        focusManager.clearFocus()
                        onResetDefaults()
                        coroutineScope.launch {
                            snackbarHostState.currentSnackbarData?.dismiss()
                            snackbarHostState.showSnackbar(
                                message = "已恢复默认设置",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                ) {
                    Text("确定")
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDefaultsDialog = false }) {
                    Text("取消")
                }
            }
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onOpenDrawer) {
                        MenuIcon()
                    }
                },
                title = {
                    Text(
                        text = "设置",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SettingsSectionHeader(title = "启动与默认")

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "默认启动页",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "选择打开应用时默认进入的折算页面。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val options = StartPageOption.entries
                        options.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = startPage == option,
                                onClick = { onStartPageChange(option) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                icon = {},
                                label = {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "默认折算比例",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        if (defaultRateText.isNotEmpty()) {
                            TextButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    onDefaultRateChange("")
                                },
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text("清除")
                            }
                        }
                    }
                    Text(
                        text = "启动应用或点击「重置」时自动填入，留空则不预设。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = defaultRateText,
                        onValueChange = onDefaultRateChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("例如 53") },
                        prefix = { Text("1 : ") },
                        suffix = { Text("万") },
                        isError = isDefaultRateInvalid,
                        supportingText = if (isDefaultRateInvalid) {
                            { Text("比例必须大于 0") }
                        } else {
                            null
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "默认哈夫币单位",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "启动应用或点击「重置」时默认选中的单位。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val units = CoinUnit.entries
                        units.forEachIndexed { index, unit ->
                            SegmentedButton(
                                selected = defaultCoinUnit == unit,
                                onClick = { onDefaultCoinUnitChange(unit) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = units.size
                                ),
                                icon = {},
                                label = {
                                    Text(
                                        text = unit.buttonLabel,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "特殊物品默认单价",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "开启「进阶折算」中的特殊物品或点击「重置」时自动填入，留空则不预设。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    OutlinedTextField(
                        value = defaultABigAmmoPriceText,
                        onValueChange = onDefaultABigAmmoPriceChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("A大子弹") },
                        placeholder = { Text("例如 0.5") },
                        suffix = { Text("元/发") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    OutlinedTextField(
                        value = defaultTier6ArmorPriceText,
                        onValueChange = onDefaultTier6ArmorPriceChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("六套") },
                        placeholder = { Text("例如 2") },
                        suffix = { Text("元/套") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        keyboardActions = KeyboardActions(
                            onNext = { focusManager.moveFocus(FocusDirection.Down) }
                        )
                    )
                    OutlinedTextField(
                        value = defaultRedAmmoPriceText,
                        onValueChange = onDefaultRedAmmoPriceChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        label = { Text("红弹") },
                        placeholder = { Text("留空不预设") },
                        suffix = { Text("元/组") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(
                            onDone = { focusManager.clearFocus() }
                        )
                    )
                }
            }

            SettingsSectionHeader(title = "折算与备注")

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "金额精度与取整",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "小数位数",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val options = AmountPrecision.entries
                        options.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = amountPrecision == option,
                                onClick = { onAmountPrecisionChange(option) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                icon = {},
                                label = {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }
                    Text(
                        text = "取整方式",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val options = AmountRounding.entries
                        options.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = amountRounding == option,
                                onClick = { onAmountRoundingChange(option) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                icon = {},
                                label = {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }
                    if (sampleCalc is CalcState.Valid) {
                        Text(
                            text = "示例（53.05M 按 1:53）：¥${sampleCalc.rmbFormatted}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "备注分隔符",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "调整复制备注时各段信息之间的连接方式。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val options = RemarkSeparator.entries
                        options.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = remarkSeparator == option,
                                onClick = { onRemarkSeparatorChange(option) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                icon = {},
                                label = {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }
                    if (sampleCalc is CalcState.Valid) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surfaceContainerHigh
                        ) {
                            Text(
                                text = sampleCalc.remark,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .toggleable(
                                value = includeSpecialItemPriceInRemark,
                                role = Role.Switch,
                                onValueChange = onIncludeSpecialItemPriceInRemarkChange
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "备注附带特殊物品单价",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "开启后备注显示单价（如 A大子弹:10发(0.5元/发)）；关闭后仅显示数量。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = includeSpecialItemPriceInRemark,
                            onCheckedChange = null
                        )
                    }
                }
            }

            SettingsSectionHeader(title = "外观与显示")

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "主题模式",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val options = ThemeMode.entries
                        options.forEachIndexed { index, option ->
                            SegmentedButton(
                                selected = themeMode == option,
                                onClick = { onThemeModeChange(option) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = options.size
                                ),
                                icon = {},
                                label = {
                                    Text(
                                        text = option.label,
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }
                }
            }

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "进阶折算默认展开层",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "控制启动或重置「进阶折算」时各层级的默认显示状态。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        FilterChip(
                            selected = defaultShowSkinsLayer,
                            onClick = { onDefaultShowSkinsLayerChange(!defaultShowSkinsLayer) },
                            label = {
                                Text(
                                    text = "红皮&刀皮",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                        FilterChip(
                            selected = defaultShowSafeBoxLayer,
                            onClick = { onDefaultShowSafeBoxLayerChange(!defaultShowSafeBoxLayer) },
                            label = {
                                Text(
                                    text = "保险",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                        FilterChip(
                            selected = defaultShowBaseLayer,
                            onClick = { onDefaultShowBaseLayerChange(!defaultShowBaseLayer) },
                            label = {
                                Text(
                                    text = "特勤处",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                        FilterChip(
                            selected = defaultShowSpecialItemsLayer,
                            onClick = {
                                onDefaultShowSpecialItemsLayerChange(!defaultShowSpecialItemsLayer)
                            },
                            label = {
                                Text(
                                    text = "特殊物品",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            }
                        )
                    }
                }
            }

            SettingsSectionHeader(title = "数据")

            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(min = 48.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .toggleable(
                                value = autoSaveHistory,
                                role = Role.Switch,
                                onValueChange = onAutoSaveHistoryChange
                            ),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .padding(end = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = "复制时自动保存历史",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "关闭后点击复制仅写入剪贴板，不会记录到「折算历史」。",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Switch(
                            checked = autoSaveHistory,
                            onCheckedChange = null
                        )
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )

                    Text(
                        text = "历史保留上限",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                    SingleChoiceSegmentedButtonRow(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        HISTORY_LIMIT_OPTIONS.forEachIndexed { index, limit ->
                            SegmentedButton(
                                selected = historyLimit == limit,
                                onClick = { onHistoryLimitChange(limit) },
                                shape = SegmentedButtonDefaults.itemShape(
                                    index = index,
                                    count = HISTORY_LIMIT_OPTIONS.size
                                ),
                                icon = {},
                                label = {
                                    Text(
                                        text = "${limit}条",
                                        style = MaterialTheme.typography.labelLarge,
                                        maxLines = 1,
                                        softWrap = false
                                    )
                                }
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showClearHistoryDialog = true },
                            enabled = historyCount > 0,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = if (historyCount > 0) "清空历史 ($historyCount)" else "清空历史",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                        FilledTonalButton(
                            onClick = { showResetDefaultsDialog = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                text = "恢复默认设置",
                                maxLines = 1,
                                softWrap = false
                            )
                        }
                    }
                }
            }

            SettingsSectionHeader(title = "关于")

            OutlinedCard(
                onClick = onOpenAbout,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            HavocBrandLogo(modifier = Modifier.size(width = 30.dp, height = 34.dp))
                            Column(
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = "哈夫币折算器",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = "版本 V$versionName · 本地版",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        ChevronRightIcon(
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "点击查看更新说明与检查更新",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AboutPage(
    onOpenChangelog: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val versionName = remember(context) { getAppVersionName(context) }
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackArrowIcon()
                    }
                },
                title = {
                    Text(
                        text = "关于哈夫币折算器",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(40.dp))

                Surface(
                    modifier = Modifier.size(80.dp),
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest,
                    border = BorderStroke(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        HavocBrandLogo(modifier = Modifier.size(width = 44.dp, height = 49.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "当前版本 V$versionName",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(32.dp))

                OutlinedCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        ListItem(
                            modifier = Modifier.clickable(onClick = onOpenChangelog),
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = {
                                Text(
                                    text = "更新说明",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            trailingContent = {
                                ChevronRightIcon(
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        )

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                        )

                        ListItem(
                            modifier = Modifier.clickable {
                                coroutineScope.launch {
                                    snackbarHostState.currentSnackbarData?.dismiss()
                                    snackbarHostState.showSnackbar(
                                        message = "检查更新功能待实装，当前为本地版",
                                        duration = SnackbarDuration.Short
                                    )
                                }
                            },
                            colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                            headlineContent = {
                                Text(
                                    text = "检查更新",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            },
                            trailingContent = {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.secondaryContainer
                                    ) {
                                        Text(
                                            text = "待实装",
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSecondaryContainer
                                        )
                                    }
                                    Text(
                                        text = "V$versionName",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        )
                    }
                }

                Text(
                    text = "所有折算均在本机离线完成，不联网、不收集任何个人数据。\n历史记录与偏好设置仅保存在本机。",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 28.dp, vertical = 20.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }

            Text(
                text = "天空属于哈夫克！",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 20.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ChangelogPage(
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val versionName = remember(context) { getAppVersionName(context) }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val highlights = remember {
        listOf(
            "纯币折算" to "极简居中布局，支持 无 / K / M / B 单位切换、动态单位提示与一键复制金额。",
            "进阶折算" to "分层展示红皮&刀皮、保险、特勤处与特殊物品，支持每层独立隐藏/显示与一键生成备注。",
            "特殊物品计价" to "支持 A大子弹、六套、红弹 数量与单价录入，自动累加至总价并附带单价明细。",
            "符合 Material 3 规范" to "遵循 Material 3 设计语言与动态取色，支持浅色/深色/跟随系统主题、顶栏滚动联动、统一圆角与触控热区，并优化页面转场与无障碍交互体验。"
        )
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackArrowIcon()
                    }
                },
                title = {
                    Text(
                        text = "更新说明",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedCard(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "版本 V$versionName",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.primaryContainer
                        ) {
                            Text(
                                text = "当前版本",
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )

                    highlights.forEach { (title, desc) ->
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "• $title",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = desc,
                                modifier = Modifier.padding(start = 12.dp),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun FlashingUnitSuffix(
    text: String,
    flashTrigger: Int
) {
    val highlight = remember { Animatable(0f) }
    val alphaAnim = remember { Animatable(1f) }

    LaunchedEffect(flashTrigger) {
        if (flashTrigger > 0) {
            highlight.snapTo(1f)
            alphaAnim.snapTo(0.2f)
            alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 140))
            alphaAnim.animateTo(0.35f, animationSpec = tween(durationMillis = 90))
            alphaAnim.animateTo(1f, animationSpec = tween(durationMillis = 120))
            highlight.animateTo(0f, animationSpec = tween(durationMillis = 220))
        }
    }

    val progress = highlight.value
    val color = lerp(
        start = MaterialTheme.colorScheme.onSurfaceVariant,
        stop = MaterialTheme.colorScheme.primary,
        fraction = progress
    )
    val scale = 1f + 0.22f * progress

    Text(
        text = text,
        color = color,
        fontWeight = if (progress > 0.1f) FontWeight.Bold else FontWeight.Normal,
        modifier = Modifier.graphicsLayer {
            alpha = alphaAnim.value
            scaleX = scale
            scaleY = scale
        }
    )
}

@Composable
private fun InputSectionCard(
    coinText: String,
    onCoinChange: (String) -> Unit,
    coinUnit: CoinUnit,
    unitFlashTrigger: Int,
    autoDetectCoinUnit: Boolean,
    onCoinUnitChange: (CoinUnit) -> Unit,
    rateText: String,
    onRateChange: (String) -> Unit,
    isRateError: Boolean,
    onNext: () -> Unit,
    onDone: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "折算参数",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            OutlinedTextField(
                value = coinText,
                onValueChange = onCoinChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                label = { Text("哈夫币数量") },
                placeholder = {
                    Text(
                        if (autoDetectCoinUnit && coinUnit == CoinUnit.NONE) {
                            "例如 530000 或 53M"
                        } else {
                            coinUnit.placeholder
                        }
                    )
                },
                suffix = if (coinUnit.suffix.isNotEmpty()) {
                    {
                        FlashingUnitSuffix(
                            text = coinUnit.suffix,
                            flashTrigger = unitFlashTrigger
                        )
                    }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = if (autoDetectCoinUnit) {
                        KeyboardType.Ascii
                    } else {
                        KeyboardType.Decimal
                    },
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(onNext = { onNext() })
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "哈夫币单位",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                SingleChoiceSegmentedButtonRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val units = CoinUnit.entries
                    units.forEachIndexed { index, unit ->
                        SegmentedButton(
                            selected = coinUnit == unit,
                            onClick = { onCoinUnitChange(unit) },
                            shape = SegmentedButtonDefaults.itemShape(
                                index = index,
                                count = units.size
                            ),
                            icon = {},
                            label = {
                                Text(
                                    text = unit.buttonLabel,
                                    style = MaterialTheme.typography.labelMedium,
                                    maxLines = 1
                                )
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = rateText,
                onValueChange = onRateChange,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                label = { Text("比例（1元 = ? 万）") },
                placeholder = { Text("例如 53") },
                prefix = { Text("1 : ") },
                suffix = { Text("万") },
                isError = isRateError,
                supportingText = if (isRateError) {
                    { Text("比例必须大于 0") }
                } else {
                    null
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { onDone() })
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SkinSelectPage(
    title: String,
    options: List<SkinCatalogItem>,
    selectedNames: List<String>,
    onToggleSkin: (String) -> Unit,
    onClearSelection: () -> Unit,
    onBack: () -> Unit
) {
    val selectedSet = remember(selectedNames) { selectedNames.toSet() }
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        BackArrowIcon()
                    }
                },
                title = {
                    Text(
                        text = if (selectedNames.isEmpty()) {
                            title
                        } else {
                            "$title（已选 ${selectedNames.size}）"
                        },
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    if (selectedNames.isNotEmpty()) {
                        TextButton(onClick = onClearSelection) {
                            Text("清空")
                        }
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = if (selectedNames.isEmpty()) {
                            "未勾选具体皮肤（备注将显示「有」）"
                        } else {
                            "已选：${selectedNames.joinToString("、")}"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Button(
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("确认返回")
                    }
                }
            }
        }
    ) { innerPadding ->
        val groupedOptions = remember(options) {
            val map = linkedMapOf<String?, MutableList<SkinCatalogItem>>()
            options.forEach { item ->
                map.getOrPut(item.group) { mutableListOf() }.add(item)
            }
            map.toList()
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            groupedOptions.forEach { (groupTitle, groupItems) ->
                if (groupTitle != null) {
                    item(key = "header_$groupTitle") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp, bottom = 2.dp, start = 4.dp, end = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = groupTitle,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            HorizontalDivider(
                                modifier = Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                            )
                        }
                    }
                }
                items(groupItems, key = { it.name }) { item ->
                    val isSelected = item.name in selectedSet
                    OutlinedCard(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.outlinedCardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.secondaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = if (isSelected) {
                            BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                        } else {
                            CardDefaults.outlinedCardBorder()
                        }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 56.dp)
                                .toggleable(
                                    value = isSelected,
                                    role = Role.Checkbox,
                                    onValueChange = { onToggleSkin(item.name) }
                                )
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Checkbox(
                                checked = isSelected,
                                onCheckedChange = null
                            )
                            Column(
                                modifier = Modifier.weight(1f),
                                verticalArrangement = Arrangement.spacedBy(2.dp)
                            ) {
                                Text(
                                    text = item.name,
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onSecondaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                                Text(
                                    text = item.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                                    } else {
                                        MaterialTheme.colorScheme.onSurfaceVariant
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionGroupHeader(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onToggle)
            .padding(horizontal = 4.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary
        )
        HorizontalDivider(
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
        )
        Text(
            text = if (expanded) "隐藏 ▴" else "显示 ▾",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
private fun SkinOptionCard(
    label: String,
    emptyEntryTitle: String,
    selectedSkins: List<String>,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onOpenSelect: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .toggleable(
                        value = checked,
                        role = Role.Switch,
                        onValueChange = onCheckedChange
                    )
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Switch(
                    checked = checked,
                    onCheckedChange = null
                )
            }

            AnimatedVisibility(visible = checked) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 4.dp),
                        color = MaterialTheme.colorScheme.outlineVariant
                    )
                    SkinSelectionEntryRow(
                        title = if (selectedSkins.isEmpty()) {
                            emptyEntryTitle
                        } else {
                            "已选 ${selectedSkins.size} 款：${selectedSkins.joinToString("、")}"
                        },
                        actionText = if (selectedSkins.isEmpty()) "选择 ›" else "修改 ›",
                        onClick = onOpenSelect
                    )
                }
            }
        }
    }
}

@Composable
private fun SkinSelectionEntryRow(
    title: String,
    actionText: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            modifier = Modifier
                .weight(1f)
                .padding(end = 12.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = actionText,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SafeBoxCard(
    safeSlots: Int,
    onSafeSlotsChange: (Int) -> Unit,
    showSmallSafeSlots: Boolean,
    onToggleSmallSafeSlots: () -> Unit,
    hasNineSlotTrialCard: Boolean,
    onNineSlotTrialCardChange: (Boolean) -> Unit,
    nineSlotTrialCardCountText: String,
    onNineSlotTrialCardCountChange: (String) -> Unit,
    onDone: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            ExpandableLevelSelector(
                label = "保险格数",
                primaryOptions = listOf(6, 9),
                secondaryOptions = listOf(2, 4),
                secondaryLabel = "2/4格",
                unitSuffix = "格",
                selected = safeSlots,
                expanded = showSmallSafeSlots,
                onToggleExpanded = onToggleSmallSafeSlots,
                onSelect = onSafeSlotsChange
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .toggleable(
                        value = hasNineSlotTrialCard,
                        role = Role.Checkbox,
                        onValueChange = onNineSlotTrialCardChange
                    )
                    .padding(horizontal = 4.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Checkbox(
                    checked = hasNineSlotTrialCard,
                    onCheckedChange = null
                )
                Text(
                    text = "9格体验卡",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            AnimatedVisibility(visible = hasNineSlotTrialCard) {
                OutlinedTextField(
                    value = nineSlotTrialCardCountText,
                    onValueChange = onNineSlotTrialCardCountChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 2.dp),
                    shape = RoundedCornerShape(12.dp),
                    label = { Text("9格体验卡数量") },
                    placeholder = { Text("请输入拥有数量") },
                    suffix = { Text("张") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                        imeAction = ImeAction.Done
                    ),
                    keyboardActions = KeyboardActions(onDone = { onDone() })
                )
            }
        }
    }
}

@Composable
private fun StandaloneLevelCard(
    label: String,
    primaryOptions: List<Int>,
    secondaryOptions: List<Int>,
    secondaryLabel: String,
    unitSuffix: String,
    selected: Int,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSelect: (Int) -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            ExpandableLevelSelector(
                label = label,
                primaryOptions = primaryOptions,
                secondaryOptions = secondaryOptions,
                secondaryLabel = secondaryLabel,
                unitSuffix = unitSuffix,
                selected = selected,
                expanded = expanded,
                onToggleExpanded = onToggleExpanded,
                onSelect = onSelect
            )
        }
    }
}

@Composable
private fun OptionalFacilitiesCard(
    divingCenterLevel: Int?,
    onDivingCenterLevelChange: (Int?) -> Unit,
    decontaminationLevel: Int?,
    onDecontaminationLevelChange: (Int?) -> Unit,
    showOptionalFacilities: Boolean,
    onToggleOptionalFacilities: () -> Unit
) {
    val optionalSelectedCount = listOfNotNull(divingCenterLevel, decontaminationLevel).size
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onToggleOptionalFacilities)
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "其他",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                val optionalToggleText = when {
                    showOptionalFacilities -> "收起 ▴"
                    optionalSelectedCount > 0 -> "已选${optionalSelectedCount}项 · 显示 ▾"
                    else -> "显示 ▾"
                }
                Text(
                    text = optionalToggleText,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            AnimatedVisibility(visible = showOptionalFacilities) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OptionalLevelSelector(
                        label = "潜水中心等级",
                        options = listOf(1, 2, 3),
                        selected = divingCenterLevel,
                        onSelect = onDivingCenterLevelChange
                    )

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)
                    )

                    OptionalLevelSelector(
                        label = "洗消舱等级",
                        options = listOf(1, 2, 3),
                        selected = decontaminationLevel,
                        onSelect = onDecontaminationLevelChange
                    )
                }
            }
        }
    }
}

@Composable
private fun SpecialItemOptionCard(
    label: String,
    unitSuffix: String,
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    countText: String,
    onCountChange: (String) -> Unit,
    unitPriceText: String,
    onUnitPriceChange: (String) -> Unit,
    onDone: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val subtotal = remember(enabled, countText, unitPriceText) {
        calcSpecialItemSubtotal(enabled, countText, unitPriceText)
    }

    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 48.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .toggleable(
                        value = enabled,
                        role = Role.Switch,
                        onValueChange = onEnabledChange
                    )
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = enabled,
                    onCheckedChange = null
                )
            }

            AnimatedVisibility(visible = enabled) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = countText,
                            onValueChange = onCountChange,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            label = {
                                Text(
                                    text = "数量",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
                            placeholder = { Text("可选") },
                            suffix = { Text(unitSuffix) },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Number,
                                imeAction = ImeAction.Next
                            ),
                            keyboardActions = KeyboardActions(
                                onNext = { focusManager.moveFocus(FocusDirection.Right) }
                            )
                        )

                        OutlinedTextField(
                            value = unitPriceText,
                            onValueChange = onUnitPriceChange,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            label = {
                                Text(
                                    text = "每${unitSuffix}价钱",
                                    maxLines = 1,
                                    softWrap = false
                                )
                            },
                            placeholder = { Text("可选") },
                            suffix = { Text("元") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal,
                                imeAction = ImeAction.Done
                            ),
                            keyboardActions = KeyboardActions(onDone = { onDone() })
                        )
                    }

                    if (subtotal.compareTo(BigDecimal.ZERO) > 0) {
                        Text(
                            text = "小计：¥${subtotal.toCleanString()}",
                            style = MaterialTheme.typography.bodySmall,
                            fontFamily = FontFamily.Monospace,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpandableLevelSelector(
    label: String,
    primaryOptions: List<Int>,
    secondaryOptions: List<Int>,
    secondaryLabel: String,
    unitSuffix: String,
    selected: Int,
    expanded: Boolean,
    onToggleExpanded: () -> Unit,
    onSelect: (Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            val toggleText = when {
                expanded -> "收起 $secondaryLabel ▴"
                selected in secondaryOptions -> "已选${selected}${unitSuffix} · 显示 ▾"
                else -> "显示 $secondaryLabel ▾"
            }
            TextButton(
                onClick = onToggleExpanded,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Text(
                    text = toggleText,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            primaryOptions.forEachIndexed { index, value ->
                SegmentedButton(
                    selected = selected == value,
                    onClick = { onSelect(value) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = primaryOptions.size
                    ),
                    icon = {},
                    label = {
                        Text(
                            text = "${value}${unitSuffix}",
                            maxLines = 1
                        )
                    }
                )
            }
        }

        AnimatedVisibility(visible = expanded) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                secondaryOptions.forEachIndexed { index, value ->
                    SegmentedButton(
                        selected = selected == value,
                        onClick = { onSelect(value) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = secondaryOptions.size
                        ),
                        icon = {},
                        label = {
                            Text(
                                text = "${value}${unitSuffix}",
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionalLevelSelector(
    label: String,
    options: List<Int>,
    selected: Int?,
    onSelect: (Int?) -> Unit
) {
    val enabled = selected != null
    val defaultLevel = options.firstOrNull() ?: 1
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .toggleable(
                    value = enabled,
                    role = Role.Switch,
                    onValueChange = { isChecked ->
                        onSelect(if (isChecked) defaultLevel else null)
                    }
                )
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Switch(
                checked = enabled,
                onCheckedChange = null
            )
        }

        AnimatedVisibility(visible = enabled) {
            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth()
            ) {
                options.forEachIndexed { index, value ->
                    SegmentedButton(
                        selected = selected == value,
                        onClick = { onSelect(value) },
                        shape = SegmentedButtonDefaults.itemShape(
                            index = index,
                            count = options.size
                        ),
                        icon = {},
                        label = {
                            Text(
                                text = "${value}级",
                                maxLines = 1
                            )
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ResultSectionCard(
    calcState: CalcState,
    showRemark: Boolean = true,
    copyButtonText: String = "复制备注",
    onCopyClick: (rmbFormatted: String, remark: String) -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "人民币",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
            )

            val amountText = when (calcState) {
                is CalcState.Valid -> "¥${calcState.rmbFormatted}"
                is CalcState.Incomplete -> "¥--"
                is CalcState.Error -> "¥--"
            }

            Text(
                text = amountText,
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (calcState is CalcState.Valid && !calcState.breakdownText.isNullOrBlank()) {
                Text(
                    text = calcState.breakdownText,
                    style = MaterialTheme.typography.bodySmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }

            if (showRemark) {
                val remarkDisplay = when (calcState) {
                    is CalcState.Valid -> calcState.remark
                    is CalcState.Incomplete -> "请输入哈夫币数量与比例后生成备注"
                    is CalcState.Error -> calcState.message
                }

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerLowest
                ) {
                    val isMultiLine = remarkDisplay.contains('\n')
                    Text(
                        text = remarkDisplay,
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (isMultiLine) {
                                    Modifier
                                } else {
                                    Modifier.horizontalScroll(rememberScrollState())
                                }
                            )
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = if (calcState is CalcState.Error) {
                            MaterialTheme.colorScheme.error
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = if (isMultiLine) Int.MAX_VALUE else 1,
                        softWrap = isMultiLine
                    )
                }
            } else if (calcState is CalcState.Error) {
                Text(
                    text = calcState.message,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Button(
                onClick = {
                    if (calcState is CalcState.Valid) {
                        onCopyClick(calcState.rmbFormatted, calcState.remark)
                    }
                },
                enabled = calcState is CalcState.Valid,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(copyButtonText)
            }
        }
    }
}

@Composable
private fun HavocBrandLogo(modifier: Modifier = Modifier) {
    val brandBlue = Color(0xFF1E5899)
    Canvas(modifier = modifier) {
        val sx = size.width / 243f
        val sy = size.height / 268f
        val scale = minOf(sx, sy)
        val ox = (size.width - 243f * scale) / 2f
        val oy = (size.height - 268f * scale) / 2f

        fun px(x: Float) = ox + x * scale
        fun py(y: Float) = oy + y * scale

        val upperLeftPath = Path().apply {
            moveTo(px(0f), py(145f))
            lineTo(px(76f), py(69f))
            lineTo(px(76f), py(207f))
            lineTo(px(99f), py(184f))
            lineTo(px(99f), py(0f))
            lineTo(px(145f), py(46f))
            lineTo(px(145f), py(151f))
            lineTo(px(76f), py(220f))
            close()
        }

        val lowerRightPath = Path().apply {
            moveTo(px(99f), py(214f))
            lineTo(px(167.5f), py(145.5f))
            lineTo(px(167.5f), py(69.5f))
            lineTo(px(243f), py(145f))
            lineTo(px(167.5f), py(220.5f))
            lineTo(px(167.5f), py(159f))
            lineTo(px(145f), py(181.5f))
            lineTo(px(145f), py(244f))
            lineTo(px(121.5f), py(267.5f))
            lineTo(px(99f), py(245f))
            close()
        }

        drawPath(path = upperLeftPath, color = brandBlue)
        drawPath(path = lowerRightPath, color = brandBlue)
    }
}

@Composable
private fun MenuIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(
        modifier = modifier
            .size(24.dp)
            .semantics { contentDescription = "打开导航菜单" }
    ) {
        val u = size.width / 24f
        val strokeWidth = 2f * u
        listOf(6.5f, 12f, 17.5f).forEach { y ->
            drawLine(
                color = color,
                start = Offset(3.5f * u, y * u),
                end = Offset(20.5f * u, y * u),
                strokeWidth = strokeWidth,
                cap = StrokeCap.Round
            )
        }
    }
}

@Composable
private fun ChevronRightIcon(
    modifier: Modifier = Modifier,
    tint: Color = LocalContentColor.current
) {
    Canvas(modifier = modifier.size(18.dp)) {
        val u = size.width / 24f
        val path = Path().apply {
            moveTo(9f * u, 6f * u)
            lineTo(15.5f * u, 12f * u)
            lineTo(9f * u, 18f * u)
        }
        drawPath(
            path = path,
            color = tint,
            style = Stroke(
                width = 2.1f * u,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
private fun BackArrowIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(
        modifier = modifier
            .size(24.dp)
            .semantics { contentDescription = "返回" }
    ) {
        val u = size.width / 24f
        val strokeWidth = 2f * u
        drawLine(
            color = color,
            start = Offset(19.5f * u, 12f * u),
            end = Offset(5f * u, 12f * u),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        val head = Path().apply {
            moveTo(11f * u, 6f * u)
            lineTo(5f * u, 12f * u)
            lineTo(11f * u, 18f * u)
        }
        drawPath(
            path = head,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
    }
}

@Composable
private fun PureCoinIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier.size(24.dp)) {
        val u = size.width / 24f
        val strokeWidth = 1.9f * u
        drawCircle(
            color = color,
            radius = 8.5f * u,
            center = Offset(12f * u, 12f * u),
            style = Stroke(width = strokeWidth)
        )
        val vPath = Path().apply {
            moveTo(8.8f * u, 7.8f * u)
            lineTo(12f * u, 11.4f * u)
            lineTo(15.2f * u, 7.8f * u)
        }
        drawPath(
            path = vPath,
            color = color,
            style = Stroke(
                width = strokeWidth,
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )
        drawLine(
            color = color,
            start = Offset(12f * u, 11.4f * u),
            end = Offset(12f * u, 16.8f * u),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(9.2f * u, 12.6f * u),
            end = Offset(14.8f * u, 12.6f * u),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
        drawLine(
            color = color,
            start = Offset(9.2f * u, 14.8f * u),
            end = Offset(14.8f * u, 14.8f * u),
            strokeWidth = strokeWidth,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun ConvertIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier.size(24.dp)) {
        val u = size.width / 24f
        val stroke = Stroke(
            width = 1.9f * u,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        drawLine(
            color = color,
            start = Offset(4f * u, 8.5f * u),
            end = Offset(20f * u, 8.5f * u),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        val topHead = Path().apply {
            moveTo(16f * u, 4.5f * u)
            lineTo(20f * u, 8.5f * u)
            lineTo(16f * u, 12.5f * u)
        }
        drawPath(path = topHead, color = color, style = stroke)

        drawLine(
            color = color,
            start = Offset(20f * u, 15.5f * u),
            end = Offset(4f * u, 15.5f * u),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        val bottomHead = Path().apply {
            moveTo(8f * u, 11.5f * u)
            lineTo(4f * u, 15.5f * u)
            lineTo(8f * u, 19.5f * u)
        }
        drawPath(path = bottomHead, color = color, style = stroke)
    }
}

@Composable
private fun HistoryIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier.size(24.dp)) {
        val u = size.width / 24f
        val stroke = Stroke(
            width = 1.9f * u,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        val radius = 7.8f * u
        drawArc(
            color = color,
            startAngle = -145f,
            sweepAngle = 310f,
            useCenter = false,
            topLeft = Offset(12f * u - radius, 12f * u - radius),
            size = Size(radius * 2f, radius * 2f),
            style = stroke
        )
        val arrow = Path().apply {
            moveTo(4.2f * u, 5.2f * u)
            lineTo(4.2f * u, 9.2f * u)
            lineTo(8.2f * u, 9.2f * u)
        }
        drawPath(path = arrow, color = color, style = stroke)

        val hands = Path().apply {
            moveTo(12f * u, 7.8f * u)
            lineTo(12f * u, 12.2f * u)
            lineTo(15.4f * u, 14f * u)
        }
        drawPath(path = hands, color = color, style = stroke)
    }
}

@Composable
private fun LabIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier.size(24.dp)) {
        val u = size.width / 24f
        val stroke = Stroke(
            width = 1.9f * u,
            cap = StrokeCap.Round,
            join = StrokeJoin.Round
        )
        drawLine(
            color = color,
            start = Offset(8.5f * u, 3.8f * u),
            end = Offset(15.5f * u, 3.8f * u),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
        val flaskPath = Path().apply {
            moveTo(10f * u, 3.8f * u)
            lineTo(10f * u, 9.2f * u)
            lineTo(5.3f * u, 18.1f * u)
            quadraticTo(4.5f * u, 19.7f * u, 6.2f * u, 20f * u)
            lineTo(17.8f * u, 20f * u)
            quadraticTo(19.5f * u, 19.7f * u, 18.7f * u, 18.1f * u)
            lineTo(14f * u, 9.2f * u)
            lineTo(14f * u, 3.8f * u)
        }
        drawPath(path = flaskPath, color = color, style = stroke)
        drawLine(
            color = color,
            start = Offset(7.3f * u, 15.2f * u),
            end = Offset(16.7f * u, 15.2f * u),
            strokeWidth = stroke.width,
            cap = StrokeCap.Round
        )
    }
}

@Composable
private fun SettingsIcon(modifier: Modifier = Modifier) {
    val color = LocalContentColor.current
    Canvas(modifier = modifier.size(24.dp)) {
        val u = size.width / 24f
        val center = Offset(12f * u, 12f * u)
        val strokeWidth = 1.9f * u
        drawCircle(
            color = color,
            radius = 3f * u,
            center = center,
            style = Stroke(width = strokeWidth)
        )
        drawCircle(
            color = color,
            radius = 6.3f * u,
            center = center,
            style = Stroke(width = strokeWidth)
        )
        for (i in 0 until 8) {
            val rad = Math.toRadians(i * 45.0)
            val cosA = cos(rad).toFloat()
            val sinA = sin(rad).toFloat()
            drawLine(
                color = color,
                start = Offset(center.x + 6.4f * u * cosA, center.y + 6.4f * u * sinA),
                end = Offset(center.x + 9.1f * u * cosA, center.y + 9.1f * u * sinA),
                strokeWidth = 2.2f * u,
                cap = StrokeCap.Round
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun HavocCalcConvertPreview() {
    哈夫币换算器Theme {
        HavocCalcApp()
    }
}

@Preview(showBackground = true, widthDp = 300, heightDp = 600)
@Composable
private fun HavocCalcDrawerPreview() {
    哈夫币换算器Theme {
        AppDrawerSheet(
            currentPage = AppPage.PURE_CONVERT,
            historyCount = 3,
            onSelectPage = {}
        )
    }
}
