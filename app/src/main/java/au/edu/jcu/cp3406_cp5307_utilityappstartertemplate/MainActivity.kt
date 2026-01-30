package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.Article
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.SoundManager
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.WordContentState
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.WordUiState
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.WordViewModel
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui.theme.CP3406_CP5603UtilityAppStarterTemplateTheme
import coil.compose.AsyncImage
import dagger.hilt.android.AndroidEntryPoint
import java.util.TimeZone
import javax.inject.Inject
import kotlin.math.abs

/**
 * Translations map for dynamic language switching.
 */
object Translations {
    val data = mapOf(
        "English" to mapOf(
            "utility_tab" to "Word",
            "settings_tab" to "Settings",
            "utility_title" to "Word of the Day",
            "settings_title" to "Settings",
            "refreshes_remaining" to "Refreshes remaining",
            "resets_at_midnight" to "resets at midnight",
            "new_word_button" to "NEW WORD",
            "definition_label" to "Definition",
            "example_usage_title" to "Example Usage",
            "no_news_context" to "No news articles found for this word context.",
            "appearance_label" to "Appearance",
            "dark_mode" to "Dark Mode",
            "light_mode" to "Light Mode",
            "selection_strategy" to "Selection Strategy",
            "strict_mode" to "Strict (No repeats)",
            "avoid_30_mode" to "Avoid last 30",
            "font_size_label" to "Font Size",
            "timezone_label" to "Timezone",
            "language_label" to "Language",
            "disclaimer" to "Note: Dictionary definitions and news contexts can sometimes be quirky dance partners. If the usage above feels a bit 'off-script', it's a great chance to learn both senses of the word!",
            "show_interaction" to "Show Interaction",
            "hide_interaction" to "Hide Interaction",
            "context_challenge" to "Context challenge: Try to use '%s' in a sentence about this news topic.",
            "dev_tools" to "Developer Tools",
            "dev_reset_button" to "Reset Daily Refresh Count",
            "noun" to "noun",
            "verb" to "verb",
            "adjective" to "adjective",
            "adverb" to "adverb",
            "pronoun" to "pronoun",
            "preposition" to "preposition",
            "conjunction" to "conjunction",
            "interjection" to "interjection",
            "article" to "article",
            "music_label" to "Background Music",
            "music_theme_label" to "Background Music Selection"
        ),
        "Mandarin" to mapOf(
            "utility_tab" to "单词",
            "settings_tab" to "设置",
            "utility_title" to "Word of the Day",
            "settings_title" to "设置",
            "refreshes_remaining" to "剩余刷新次数",
            "resets_at_midnight" to "午夜重置",
            "new_word_button" to "获取新单词",
            "definition_label" to "定义",
            "example_usage_title" to "用例参考",
            "no_news_context" to "未找到该单词的新闻背景。",
            "appearance_label" to "外观",
            "dark_mode" to "深色模式",
            "light_mode" to "浅色模式",
            "selection_strategy" to "选择策略",
            "strict_mode" to "严格（不重复）",
            "avoid_30_mode" to "避开最后30个",
            "font_size_label" to "字体大小",
            "timezone_label" to "时区",
            "language_label" to "语言",
            "disclaimer" to "注意：词典定义和新闻背景有时会显得不太一致。如果上述用法感觉有点“不按剧本”，这正是学习该单词两种含义的好机会！",
            "show_interaction" to "显示互动",
            "hide_interaction" to "隐藏互动",
            "context_challenge" to "语境挑战：尝试针对此新闻主题使用“%s”造句。",
            "dev_tools" to "开发者工具",
            "dev_reset_button" to "重置每日刷新计数",
            "noun" to "名词",
            "verb" to "动词",
            "adjective" to "形容词",
            "adverb" to "副词",
            "pronoun" to "代词",
            "preposition" to "介词",
            "conjunction" to "连词",
            "interjection" to "感叹词",
            "article" to "冠词",
            "music_label" to "背景音乐",
            "music_theme_label" to "背景音乐选择"
        ),
        "Korean" to mapOf(
            "utility_tab" to "단어",
            "settings_tab" to "설정",
            "utility_title" to "Word of the Day",
            "settings_title" to "설정",
            "refreshes_remaining" to "남은 새로고침 횟수",
            "resets_at_midnight" to "자정에 초기화됨",
            "new_word_button" to "새 단어",
            "definition_label" to "정의",
            "example_usage_title" to "사용 예시",
            "no_news_context" to "이 단어에 대한 뉴스 기사를 찾을 수 없습니다.",
            "appearance_label" to "화면 테마",
            "dark_mode" to "다크 모드",
            "light_mode" to "라이트 모드",
            "selection_strategy" to "선택 전략",
            "strict_mode" to "엄격 (중복 없음)",
            "avoid_30_mode" to "최근 30개 제외",
            "font_size_label" to "글자 크기",
            "timezone_label" to "시간대",
            "language_label" to "언어",
            "disclaimer" to "참고: 사전 정의와 뉴스 맥락은 때때로 다를 수 있습니다. 위의 사용법이 어색하게 느껴진다면, 단어의 두 가지 의미를 모두 배울 수 있는 좋은 기회입니다!",
            "show_interaction" to "상호작용 보기",
            "hide_interaction" to "상호작용 숨기기",
            "context_challenge" to "맥락 챌린지: 이 뉴스 주제에 대해 '%s' 단어를 사용하여 문장을 만들어 보세요.",
            "dev_tools" to "개발자 도구",
            "dev_reset_button" to "새로고침 횟수 초기화",
            "noun" to "명사",
            "verb" to "동사",
            "adjective" to "형용사",
            "adverb" to "부사",
            "pronoun" to "대명사",
            "preposition" to "전치사",
            "conjunction" to "접속사",
            "interjection" to "감탄사",
            "article" to "관사",
            "music_label" to "배경 음악",
            "music_theme_label" to "배경 음악 선택"
        )
    )
}

fun t(key: String, language: String): String = Translations.data[language]?.get(key) ?: Translations.data["English"]!![key]!!

fun translatePartOfSpeech(pos: String, language: String): String {
    val key = pos.lowercase().trim()
    return Translations.data[language]?.get(key) ?: pos
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: WordViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsState()

            CP3406_CP5603UtilityAppStarterTemplateTheme(
                darkTheme = uiState.isDarkTheme
            ) {
                UtilityApp(
                    soundManager = soundManager,
                    viewModel = viewModel
                )
            }
        }
    }
}

@Composable
fun UtilityApp(
    soundManager: SoundManager,
    viewModel: WordViewModel
) {
    var selectedTab by remember { mutableStateOf("Utility") }
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Lifecycle observer to handle pause/resume music
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> soundManager.pauseMusic()
                Lifecycle.Event.ON_RESUME -> {
                    if (uiState.musicEnabled && (uiState.content is WordContentState.Success || uiState.content is WordContentState.Error)) {
                        soundManager.handleMusicState(true, uiState.selectedMusicTheme)
                    }
                }
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // Control music based on state and content status
    LaunchedEffect(uiState.musicEnabled, uiState.selectedMusicTheme, uiState.content) {
        if (uiState.content is WordContentState.Success || uiState.content is WordContentState.Error) {
            soundManager.handleMusicState(uiState.musicEnabled, uiState.selectedMusicTheme)
        }
    }

    Scaffold(
        bottomBar = {
            AppNavigationBar(
                selectedTab = selectedTab,
                language = uiState.selectedLanguage,
                onTabSelected = {
                    if (selectedTab != it) {
                        selectedTab = it
                        soundManager.playNavigationSound()
                    }
                }
            )
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Box(modifier = Modifier.padding(innerPadding)) {
                AnimatedContent(
                    targetState = selectedTab,
                    transitionSpec = {
                        if (targetState == "Settings") {
                            slideInHorizontally { it } + fadeIn() togetherWith
                                    slideOutHorizontally { -it } + fadeOut()
                        } else {
                            slideInHorizontally { -it } + fadeIn() togetherWith
                                    slideOutHorizontally { it } + fadeOut()
                        }.using(SizeTransform { _, _ -> tween<IntSize>(durationMillis = 500) })
                    },
                    label = "tabTransition"
                ) { tab ->
                    when (tab) {
                        "Utility" -> WordScreen(
                            state = uiState.content,
                            refreshCount = uiState.refreshCount,
                            fontSizeMultiplier = uiState.fontSizeMultiplier,
                            language = uiState.selectedLanguage,
                            onRefresh = {
                                viewModel.refreshWord()
                                soundManager.playRefreshSound()
                            },
                            onRefreshNews = {
                                viewModel.refreshNewsOnly()
                                soundManager.playRefreshSound()
                            },
                            soundManager = soundManager
                        )
                        "Settings" -> SettingsScreen(
                            isDarkTheme = uiState.isDarkTheme,
                            onThemeChange = {
                                viewModel.setDarkTheme(it)
                                soundManager.playToggleSound()
                            },
                            fontSizeMultiplier = uiState.fontSizeMultiplier,
                            onFontSizeChange = {
                                viewModel.setFontSizeMultiplier(it)
                                soundManager.playToggleSound()
                            },
                            isStrictSelection = uiState.isStrictSelection,
                            onStrictSelectionChange = {
                                viewModel.toggleStrictSelection(it)
                                soundManager.playToggleSound()
                            },
                            onDevResetRefresh = {
                                viewModel.devResetRefreshCount()
                                soundManager.playRefreshSound()
                            },
                            selectedLanguage = uiState.selectedLanguage,
                            onLanguageChange = {
                                viewModel.setLanguage(it)
                                soundManager.playToggleSound()
                            },
                            musicEnabled = uiState.musicEnabled,
                            onMusicEnabledChange = {
                                viewModel.setMusicEnabled(it)
                                soundManager.playToggleSound()
                            },
                            selectedMusicTheme = uiState.selectedMusicTheme,
                            onMusicThemeChange = {
                                viewModel.setMusicTheme(it)
                                soundManager.playToggleSound()
                            },
                            soundManager = soundManager
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppNavigationBar(selectedTab: String, language: String, onTabSelected: (String) -> Unit) {
    NavigationBar(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, null) },
            label = { Text(t("utility_tab", language)) },
            selected = selectedTab == "Utility",
            onClick = { onTabSelected("Utility") }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Settings, null) },
            label = { Text(t("settings_tab", language)) },
            selected = selectedTab == "Settings",
            onClick = { onTabSelected("Settings") }
        )
    }
}

@Composable
fun WordScreen(
    state: WordContentState,
    refreshCount: Int,
    fontSizeMultiplier: Float,
    language: String,
    onRefresh: () -> Unit,
    onRefreshNews: () -> Unit,
    soundManager: SoundManager
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            t("utility_title", language),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (MaterialTheme.typography.headlineMedium.fontSize.value * fontSizeMultiplier).sp
        )

        Box(modifier = Modifier.weight(1f)) {
            AnimatedContent(targetState = state, label = "contentTransition") { currentState ->
                when (currentState) {
                    is WordContentState.Loading -> LoadingIndicator()
                    is WordContentState.Error -> ErrorMessage(currentState.message)
                    is WordContentState.Success -> WordContent(currentState, fontSizeMultiplier, language, soundManager, onRefreshNews)
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            RefreshButton(onRefresh, fontSizeMultiplier, language, refreshCount > 0 && state !is WordContentState.Loading)
            Text(
                text = "${t("refreshes_remaining", language)}: $refreshCount (${t("resets_at_midnight", language)})",
                style = MaterialTheme.typography.labelMedium,
                color = if (refreshCount > 0) MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Bold,
                fontSize = (13 * fontSizeMultiplier).sp
            )
        }
    }
}

@Composable
fun WordContent(
    state: WordContentState.Success,
    fontSizeMultiplier: Float,
    language: String,
    soundManager: SoundManager,
    onRefreshNews: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(24.dp)
                ),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    state.word,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = (MaterialTheme.typography.displaySmall.fontSize.value * fontSizeMultiplier).sp
                )
                Text(
                    translatePartOfSpeech(state.partOfSpeech, language),
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    color = MaterialTheme.colorScheme.secondary,
                    fontSize = (MaterialTheme.typography.bodyLarge.fontSize.value * fontSizeMultiplier).sp
                )
                Spacer(Modifier.height(16.dp))
                Text(
                    t("definition_label", language),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.tertiary
                )
                Text(
                    state.definition,
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = (MaterialTheme.typography.bodyLarge.fontSize.value * fontSizeMultiplier).sp,
                    lineHeight = (24 * fontSizeMultiplier).sp
                )
            }
        }

        AppSection(
            title = t("example_usage_title", language),
            fontSizeMultiplier = fontSizeMultiplier,
            action = {
                IconButton(onClick = onRefreshNews) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Refresh News",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        ) {
            if (state.newsArticles.isEmpty()) {
                EmptyNewsState(language)
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    DisclaimerText(fontSizeMultiplier, language)
                    NewsContextCard(state.newsArticles.first(), state.word, fontSizeMultiplier, language, soundManager)
                }
            }
        }
    }
}

@Composable
fun DisclaimerText(fontSizeMultiplier: Float, language: String) {
    Text(
        text = t("disclaimer", language),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontStyle = FontStyle.Italic,
        fontSize = (14 * fontSizeMultiplier).sp,
        modifier = Modifier.padding(horizontal = 8.dp),
        textAlign = TextAlign.Center,
        lineHeight = (18 * fontSizeMultiplier).sp
    )
}

@Composable
fun NewsContextCard(article: Article, word: String, fontSizeMultiplier: Float, language: String, soundManager: SoundManager) {
    val context = LocalContext.current
    var showRetrieval by remember { mutableStateOf(false) }

    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                soundManager.playNavigationSound()
                context.startActivity(Intent(Intent.ACTION_VIEW, article.url.toUri()))
            },
        shape = RoundedCornerShape(20.dp)
    ) {
        Column {
            article.urlToImage?.let { url ->
                AsyncImage(
                    model = url,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        article.source.name,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.secondary,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }

                Text(
                    article.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = (17 * fontSizeMultiplier).sp,
                    lineHeight = (22 * fontSizeMultiplier).sp
                )

                val snippet = article.description ?: article.content ?: ""
                Text(
                    text = buildAnnotatedString {
                        val lowerSnippet = snippet.lowercase()
                        val lowerWord = word.lowercase()
                        var startIndex = 0
                        while (startIndex < snippet.length) {
                            val index = lowerSnippet.indexOf(lowerWord, startIndex)
                            if (index == -1) {
                                append(snippet.substring(startIndex))
                                break
                            } else {
                                append(snippet.substring(startIndex, index))
                                withStyle(style = SpanStyle(background = Color.Yellow.copy(alpha = 0.5f), fontWeight = FontWeight.Bold)) {
                                    append(snippet.substring(index, index + lowerWord.length))
                                }
                                startIndex = index + lowerWord.length
                            }
                        }
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    fontSize = (15 * fontSizeMultiplier).sp,
                    maxLines = 4
                )

                Button(
                    onClick = {
                        showRetrieval = !showRetrieval
                        soundManager.playToggleSound()
                    },
                    modifier = Modifier.align(Alignment.End),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                ) {
                    Text(if (showRetrieval) t("hide_interaction", language) else t("show_interaction", language), fontSize = (12 * fontSizeMultiplier).sp)
                }

                AnimatedVisibility(visible = showRetrieval) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            t("context_challenge", language).format(word),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = (13 * fontSizeMultiplier).sp,
                            fontStyle = FontStyle.Italic
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AppSection(
    title: String,
    fontSizeMultiplier: Float,
    action: (@Composable () -> Unit)? = null,
    content: @Composable () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                fontSize = (MaterialTheme.typography.titleLarge.fontSize.value * fontSizeMultiplier).sp
            )
            action?.invoke()
        }
        content()
    }
}

@Composable
fun RefreshButton(onClick: () -> Unit, fontSizeMultiplier: Float, language: String, enabled: Boolean) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val scale by animateFloatAsState(targetValue = if (isPressed && enabled) 0.96f else 1f, label = "scale")

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth().height(56.dp).scale(scale),
        shape = RoundedCornerShape(16.dp),
        interactionSource = interactionSource,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                t("new_word_button", language),
                fontSize = (16 * fontSizeMultiplier).sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.2.sp
            )
        }
    }
}

@Composable
fun LoadingIndicator() = Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
    CircularProgressIndicator(strokeWidth = 4.dp, modifier = Modifier.scale(1.5f))
}

@Composable
fun ErrorMessage(error: String) = Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
    Text(
        text = error,
        color = MaterialTheme.colorScheme.error,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyLarge,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun EmptyNewsState(language: String) = Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
    Text(t("no_news_context", language), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    fontSizeMultiplier: Float,
    onFontSizeChange: (Float) -> Unit,
    isStrictSelection: Boolean,
    onStrictSelectionChange: (Boolean) -> Unit,
    onDevResetRefresh: () -> Unit,
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit,
    musicEnabled: Boolean,
    onMusicEnabledChange: (Boolean) -> Unit,
    selectedMusicTheme: Int,
    onMusicThemeChange: (Int) -> Unit,
    soundManager: SoundManager
) {
    val timezones = remember {
        val now = System.currentTimeMillis()
        TimeZone.getAvailableIDs()
            .map { TimeZone.getTimeZone(it) }
            .distinctBy { it.getOffset(now) }
            .sortedBy { it.getOffset(now) }
            .map { tz ->
                val offset = tz.getOffset(now)
                val hours = abs(offset / 3600000)
                val minutes = abs((offset / 60000) % 60)
                val sign = if (offset >= 0) "+" else "-"
                val gmt = "GMT $sign%02d:%02d".format(hours, minutes)
                val name = tz.id.substringAfter("/")
                if (name.isNotEmpty() && name != tz.id) "$gmt ($name)" else gmt
            }
    }

    var selectedTz by remember { mutableStateOf(timezones.find { it.contains("London") } ?: timezones[0]) }
    var expandedTz by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(t("settings_title", selectedLanguage), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.ExtraBold)

        ElevatedCard(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(24.dp)) {
            Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(24.dp)) {
                SettingsRow(title = t("appearance_label", selectedLanguage), subtitle = if (isDarkTheme) t("dark_mode", selectedLanguage) else t("light_mode", selectedLanguage)) {
                    Switch(checked = isDarkTheme, onCheckedChange = onThemeChange)
                }

                SettingsRow(title = t("selection_strategy", selectedLanguage), subtitle = if (isStrictSelection) t("strict_mode", selectedLanguage) else t("avoid_30_mode", selectedLanguage)) {
                    Switch(checked = isStrictSelection, onCheckedChange = onStrictSelectionChange)
                }

                SettingsRow(title = t("music_label", selectedLanguage), subtitle = if (musicEnabled) "On" else "Off") {
                    Switch(checked = musicEnabled, onCheckedChange = onMusicEnabledChange)
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(t("music_theme_label", selectedLanguage), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        listOf(0 to "1", 1 to "2", 2 to "3").forEach { (idx, label) ->
                            Button(
                                onClick = { onMusicThemeChange(idx) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedMusicTheme == idx) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedMusicTheme == idx) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) { Text(label, fontSize = 11.sp) }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(t("language_label", selectedLanguage), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        listOf("English" to "English", "Mandarin" to "中文", "Korean" to "한국어").forEach { (id, label) ->
                            Button(
                                onClick = { onLanguageChange(id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (selectedLanguage == id) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (selectedLanguage == id) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) { Text(label, fontSize = 12.sp) }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(t("font_size_label", selectedLanguage), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(8.dp)) {
                        listOf(0.8f to "Small", 1f to "Med", 1.2f to "Large").forEach { (scale, label) ->
                            Button(
                                onClick = { onFontSizeChange(scale) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (fontSizeMultiplier == scale) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                                    contentColor = if (fontSizeMultiplier == scale) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            ) { Text(label, fontSize = 12.sp) }
                        }
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(t("timezone_label", selectedLanguage), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    ExposedDropdownMenuBox(expanded = expandedTz, onExpandedChange = { expandedTz = !expandedTz }) {
                        TextField(
                            value = selectedTz,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedTz) },
                            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth(),
                            colors = ExposedDropdownMenuDefaults.textFieldColors(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = expandedTz, onDismissRequest = { expandedTz = false }) {
                            timezones.forEach { tz ->
                                DropdownMenuItem(
                                    text = { Text(tz) },
                                    onClick = {
                                        selectedTz = tz
                                        expandedTz = false
                                        soundManager.playScrollSound()
                                    }
                                )
                            }
                        }
                    }
                }

                // Dev Feature: Reset Refresh Count
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(t("dev_tools", selectedLanguage), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.error)
                    Button(
                        onClick = onDevResetRefresh,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.errorContainer, contentColor = MaterialTheme.colorScheme.onErrorContainer)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Refresh, null)
                            Text(t("dev_reset_button", selectedLanguage))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SettingsRow(title: String, subtitle: String, control: @Composable () -> Unit) {
    Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
            Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.secondary)
        }
        control()
    }
}
