package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore by preferencesDataStore(name = "word_prefs")

@Singleton
class PreferencesManager @Inject constructor(@ApplicationContext context: Context) {
    private val dataStore = context.dataStore

    val currentWord: Flow<String?> = dataStore.data.map { it[KEY_WORD] }
    val lastRefreshDate: Flow<String?> = dataStore.data.map { it[KEY_DATE] }
    val isStrictSelection: Flow<Boolean> = dataStore.data.map { it[KEY_STRICT] ?: false }
    val shownWords: Flow<Set<String>> = dataStore.data.map { it[KEY_SHOWN_WORDS] ?: emptySet() }
    val refreshCount: Flow<Int> = dataStore.data.map { it[KEY_REFRESH_COUNT] ?: 2 }
    val selectedLanguage: Flow<String> = dataStore.data.map { it[KEY_LANGUAGE] ?: "English" }
    val isDarkTheme: Flow<Boolean> = dataStore.data.map { it[KEY_DARK_THEME] ?: false }
    val fontSizeMultiplier: Flow<Float> = dataStore.data.map { it[KEY_FONT_SIZE] ?: 1.0f }
    
    val last30Words: Flow<List<String>> = dataStore.data.map { it[KEY_LAST_30] ?: "" }
        .map { if (it.isEmpty()) emptyList() else it.split(",") }

    val wordData: Flow<Triple<String, String, String>?> = dataStore.data.map { prefs ->
        val word = prefs[KEY_WORD]
        val pos = prefs[KEY_POS]
        val def = prefs[KEY_DEF]
        if (word != null && pos != null && def != null) {
            Triple(word, pos, def)
        } else null
    }

    suspend fun saveWord(word: String, pos: String, def: String, date: String) {
        dataStore.edit { prefs ->
            prefs[KEY_WORD] = word
            prefs[KEY_POS] = pos
            prefs[KEY_DEF] = def
            prefs[KEY_DATE] = date

            val currentShown = prefs[KEY_SHOWN_WORDS] ?: emptySet()
            prefs[KEY_SHOWN_WORDS] = currentShown + word

            val last30List = (prefs[KEY_LAST_30] ?: "").split(",").filter { it.isNotEmpty() }.toMutableList()
            if (!last30List.contains(word)) {
                last30List.add(0, word)
                if (last30List.size > 30) last30List.removeAt(last30List.size - 1)
                prefs[KEY_LAST_30] = last30List.joinToString(",")
            }
        }
    }

    suspend fun decrementRefreshCount() {
        dataStore.edit { prefs ->
            val current = prefs[KEY_REFRESH_COUNT] ?: 2
            if (current > 0) prefs[KEY_REFRESH_COUNT] = current - 1
        }
    }

    suspend fun resetRefreshCount() {
        dataStore.edit { prefs ->
            prefs[KEY_REFRESH_COUNT] = 2
        }
    }

    suspend fun setStrictSelection(enabled: Boolean) {
        dataStore.edit { it[KEY_STRICT] = enabled }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { it[KEY_LANGUAGE] = language }
    }

    suspend fun setDarkTheme(enabled: Boolean) {
        dataStore.edit { it[KEY_DARK_THEME] = enabled }
    }

    suspend fun setFontSizeMultiplier(multiplier: Float) {
        dataStore.edit { it[KEY_FONT_SIZE] = multiplier }
    }

    companion object {
        private val KEY_WORD = stringPreferencesKey("current_word")
        private val KEY_POS = stringPreferencesKey("current_pos")
        private val KEY_DEF = stringPreferencesKey("current_def")
        private val KEY_DATE = stringPreferencesKey("last_refresh_date")
        private val KEY_STRICT = booleanPreferencesKey("strict_selection")
        private val KEY_SHOWN_WORDS = stringSetPreferencesKey("shown_words")
        private val KEY_LAST_30 = stringPreferencesKey("last_30_words")
        private val KEY_REFRESH_COUNT = intPreferencesKey("refresh_count")
        private val KEY_LANGUAGE = stringPreferencesKey("selected_language")
        private val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        private val KEY_FONT_SIZE = floatPreferencesKey("font_size_multiplier")
    }
}
