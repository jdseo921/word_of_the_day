package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SoundManager @Inject constructor(@ApplicationContext private val context: Context) {
    private var toneGenerator: ToneGenerator? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    @Synchronized
    private fun getToneGenerator(): ToneGenerator? {
        if (toneGenerator == null) {
            try {
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 80) // Increased volume
            } catch (e: Exception) {
                return null
            }
        }
        return toneGenerator
    }

    private fun playTone(type: Int, duration: Int) {
        Thread {
            try {
                getToneGenerator()?.startTone(type, duration)
            } catch (e: Exception) {}
        }.start()
    }

    fun playRefreshSound() = playTone(ToneGenerator.TONE_SUP_PIP, 40)
    fun playNavigationSound() = playTone(ToneGenerator.TONE_CDMA_PIP, 30)
    fun playScrollSound() = playTone(ToneGenerator.TONE_SUP_PIP, 20)
}
