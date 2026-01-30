package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.R
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.PreferencesManager
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.api.Article
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed interface WordContentState {
    data object Loading : WordContentState
    data class Success(
        val word: String,
        val partOfSpeech: String,
        val definition: String,
        val newsArticles: List<Article>
    ) : WordContentState
    data class Error(val message: String) : WordContentState
}

data class WordUiState(
    val content: WordContentState = WordContentState.Loading,
    val refreshCount: Int = 2,
    val isStrictSelection: Boolean = false,
    val selectedLanguage: String = "English",
    val isDarkTheme: Boolean = false,
    val fontSizeMultiplier: Float = 1.0f,
    val musicEnabled: Boolean = true,
    val selectedMusicTheme: Int = 0
)

@HiltViewModel
class WordViewModel @Inject constructor(
    private val repository: WordRepository,
    private val preferencesManager: PreferencesManager,
    application: Application
) : AndroidViewModel(application) {

    private val _contentState = MutableStateFlow<WordContentState>(WordContentState.Loading)
    
    // Optimized combine chain to ensure faster emission and cleaner code
    val uiState: StateFlow<WordUiState> = combine(
        _contentState,
        preferencesManager.refreshCount,
        preferencesManager.isStrictSelection,
        preferencesManager.selectedLanguage,
        preferencesManager.isDarkTheme,
        preferencesManager.musicEnabled,
        preferencesManager.selectedMusicTheme,
        preferencesManager.fontSizeMultiplier
    ) { args ->
        WordUiState(
            content = args[0] as WordContentState,
            refreshCount = args[1] as Int,
            isStrictSelection = args[2] as Boolean,
            selectedLanguage = args[3] as String,
            isDarkTheme = args[4] as Boolean,
            musicEnabled = args[5] as Boolean,
            selectedMusicTheme = args[6] as Int,
            fontSizeMultiplier = args[7] as Float
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = WordUiState()
    )

    private val wordQueue = mutableListOf<String>()
    
    private val mwApiKey: String by lazy {
        getApplication<Application>().getString(R.string.merriam_webster_api_key)
    }
    
    private val newsApiKey: String by lazy {
        getApplication<Application>().getString(R.string.news_api_key)
    }

    init {
        loadOrRefreshWord()
    }

    private fun loadOrRefreshWord() {
        viewModelScope.launch {
            val today = getCurrentDate()
            val lastDate = preferencesManager.lastRefreshDate.first()
            val savedData = preferencesManager.wordData.first()

            if (lastDate == today && savedData != null) {
                val (word, pos, def) = savedData
                fetchNewsUsage(word, emptyList(), word, pos, def)
            } else {
                preferencesManager.resetRefreshCount()
                refreshWord(isManual = false)
            }
        }
    }

    // region Settings Actions
    fun toggleStrictSelection(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setStrictSelection(enabled) }
    }

    fun setLanguage(language: String) {
        viewModelScope.launch { preferencesManager.setLanguage(language) }
    }

    fun setDarkTheme(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setDarkTheme(enabled) }
    }

    fun setFontSizeMultiplier(multiplier: Float) {
        viewModelScope.launch { preferencesManager.setFontSizeMultiplier(multiplier) }
    }

    fun setMusicEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setMusicEnabled(enabled) }
    }

    fun setMusicTheme(themeIndex: Int) {
        viewModelScope.launch { preferencesManager.setSelectedMusicTheme(themeIndex) }
    }

    fun devResetRefreshCount() {
        viewModelScope.launch { preferencesManager.resetRefreshCount() }
    }
    // endregion

    fun refreshNewsOnly() {
        val state = _contentState.value
        if (state is WordContentState.Success) {
            viewModelScope.launch {
                if (state.newsArticles.size > 1) {
                    val rotated = state.newsArticles.drop(1) + state.newsArticles.take(1)
                    _contentState.value = state.copy(newsArticles = rotated)
                } else {
                    fetchNewsUsage(state.word, emptyList(), state.word, state.partOfSpeech, state.definition)
                }
            }
        }
    }

    fun refreshWord(isManual: Boolean = true) {
        viewModelScope.launch {
            if (isManual) {
                val currentCount = preferencesManager.refreshCount.first()
                if (currentCount <= 0) {
                    _contentState.value = WordContentState.Error("No refreshes remaining today. Resets at midnight.")
                    return@launch
                }
                preferencesManager.decrementRefreshCount()
            }

            _contentState.value = WordContentState.Loading
            
            val strict = preferencesManager.isStrictSelection.first()
            val shown = preferencesManager.shownWords.first()
            val last30 = preferencesManager.last30Words.first()

            if (wordQueue.isEmpty()) {
                repository.getRandomWords(30).onSuccess { words ->
                    val filteredWords = words.filter { word ->
                        word.length > 6 && if (strict) {
                            word !in shown
                        } else {
                            word !in last30
                        }
                    }
                    wordQueue.addAll(filteredWords)
                    if (wordQueue.isEmpty() && words.isNotEmpty()) {
                        wordQueue.addAll(words)
                    }
                }.onFailure { _ ->
                    _contentState.value = WordContentState.Error("Failed to fetch new words.")
                    return@launch
                }
            }

            if (wordQueue.isNotEmpty()) {
                val nextWord = wordQueue.removeAt(0)
                fetchWordData(nextWord)
            } else {
                repository.getRandomWords(10).onSuccess { words ->
                    if (words.isNotEmpty()) fetchWordData(words.first())
                    else _contentState.value = WordContentState.Error("No suitable words found. Try again.")
                }.onFailure { _ ->
                    _contentState.value = WordContentState.Error("No suitable words found. Try again.")
                }
            }
        }
    }

    private suspend fun fetchWordData(word: String) {
        if (mwApiKey == "YOUR_KEY_HERE" || mwApiKey.isBlank()) {
            fetchFallbackWordData(word)
            return
        }

        repository.getMerriamWebsterDefinition(word, mwApiKey).onSuccess { response ->
            if (response.isNotEmpty()) {
                val entry = response.first()
                val actualWord = entry.meta.id.split(":").first()
                val pos = entry.functionalLabel
                val def = entry.shortDefinitions.firstOrNull() ?: "No definition found"
                fetchNewsUsage(word, entry.meta.stems, actualWord, pos, def)
            } else {
                fetchFallbackWordData(word)
            }
        }.onFailure {
            fetchFallbackWordData(word)
        }
    }

    private suspend fun fetchFallbackWordData(word: String) {
        repository.getWordDefinition(word).onSuccess { response ->
            val meaning = response.meanings.firstOrNull()
            val pos = meaning?.partOfSpeech ?: "N/A"
            val def = meaning?.definitions?.firstOrNull()?.definition ?: "No definition found"
            fetchNewsUsage(word, emptyList(), response.word, pos, def)
        }.onFailure {
            // Avoid infinite loops by providing a default error if retry fails too many times
            _contentState.value = WordContentState.Error("Word data currently unavailable.")
        }
    }

    private suspend fun fetchNewsUsage(word: String, stems: List<String>, actualWord: String, pos: String, def: String) {
        if (newsApiKey == "YOUR_NEWS_KEY_HERE" || newsApiKey.isBlank()) {
            saveAndDisplayWord(actualWord, pos, def, emptyList())
            return
        }

        repository.getNewsUsage(word, stems, newsApiKey).onSuccess { articles ->
            saveAndDisplayWord(actualWord, pos, def, articles)
        }.onFailure {
            saveAndDisplayWord(actualWord, pos, def, emptyList())
        }
    }

    private suspend fun saveAndDisplayWord(word: String, pos: String, def: String, articles: List<Article>) {
        preferencesManager.saveWord(word, pos, def, getCurrentDate())
        _contentState.value = WordContentState.Success(word, pos, def, articles)
    }

    private fun getCurrentDate(): String {
        return SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    }
}
