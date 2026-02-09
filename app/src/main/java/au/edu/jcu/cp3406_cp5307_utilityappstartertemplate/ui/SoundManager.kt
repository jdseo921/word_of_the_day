package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.concurrent.thread

@Singleton
class SoundManager @Inject constructor(@ApplicationContext private val context: Context) {
    private var toneGenerator: ToneGenerator? = null

    @Synchronized
    private fun getToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            try {
                // Initialize ToneGenerator lazily on first use to speed up app launch
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80)
            } catch (e: Exception) {
                return null
            }
        }
        return toneGenerator
    }

    private fun playTone(type: Int, duration: Int) {
        thread(start = true) {
            try {
                getToneGenerator()?.startTone(type, duration)
            } catch (e: Exception) {
                // Ignore tone playback errors
            }
        }
    }

    fun playRefreshSound() = playTone(ToneGenerator.TONE_SUP_PIP, 40)
    fun playNavigationSound() = playTone(ToneGenerator.TONE_CDMA_PIP, 30)
    fun playScrollSound() = playTone(ToneGenerator.TONE_SUP_PIP, 20)
}
