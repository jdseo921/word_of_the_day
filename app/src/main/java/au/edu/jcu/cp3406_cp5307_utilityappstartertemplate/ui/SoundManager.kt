package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui

import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.R
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
                toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 40)
            } catch (e: Exception) {
                return null
            }
        }
        return toneGenerator
    }

    @Volatile
    private var mediaPlayer: MediaPlayer? = null
    private var currentThemeIndex: Int = -1
    private var volumeAnimator: ValueAnimator? = null
    private var targetVolume: Float = 0.15f

    private val themes = listOf(
        R.raw.theme1,
        R.raw.theme2,
        R.raw.theme3
    )

    private fun playTone(type: Int, duration: Int) {
        Thread {
            try {
                getToneGenerator()?.startTone(type, duration)
            } catch (e: Exception) {}
        }.start()
    }

    fun playRefreshSound() = playTone(ToneGenerator.TONE_SUP_PIP, 40)
    fun playNavigationSound() = playTone(ToneGenerator.TONE_CDMA_PIP, 30)
    fun playToggleSound() = playTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 25)
    fun playScrollSound() = playTone(ToneGenerator.TONE_SUP_PIP, 20)

    fun playMusic(themeIndex: Int) {
        if (currentThemeIndex == themeIndex && mediaPlayer != null) {
            try {
                if (!mediaPlayer!!.isPlaying) {
                    mainHandler.post { fadeIn() }
                }
            } catch (e: Exception) {
                stopMusic()
                createAndStartPlayer(themeIndex)
            }
            return
        }

        if (mediaPlayer == null) {
            createAndStartPlayer(themeIndex)
        } else {
            mainHandler.post {
                fadeOut {
                    stopMusic()
                    createAndStartPlayer(themeIndex)
                }
            }
        }
    }

    private fun createAndStartPlayer(themeIndex: Int) {
        currentThemeIndex = themeIndex
        Thread {
            try {
                val player = MediaPlayer.create(context, themes[themeIndex])
                player?.let {
                    it.isLooping = true
                    it.setVolume(0f, 0f)
                    it.start()
                    mediaPlayer = it
                    mainHandler.post { fadeIn() }
                }
            } catch (e: Exception) {
                mediaPlayer = null
                currentThemeIndex = -1
            }
        }.start()
    }

    fun pauseMusic() {
        mainHandler.post {
            fadeOut {
                try {
                    mediaPlayer?.pause()
                } catch (e: Exception) {}
            }
        }
    }

    fun stopMusic() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (e: Exception) {
        } finally {
            mediaPlayer = null
            currentThemeIndex = -1
        }
    }

    fun handleMusicState(enabled: Boolean, themeIndex: Int) {
        if (!enabled) {
            pauseMusic()
        } else {
            if (currentThemeIndex != themeIndex || mediaPlayer == null) {
                playMusic(themeIndex)
            } else {
                try {
                    if (!mediaPlayer!!.isPlaying) mainHandler.post { fadeIn() }
                } catch (e: Exception) {
                    playMusic(themeIndex)
                }
            }
        }
    }

    private fun fadeIn() {
        volumeAnimator?.cancel()
        try {
            mediaPlayer?.start()
            volumeAnimator = ValueAnimator.ofFloat(0f, targetVolume).apply {
                duration = 1000
                addUpdateListener { animation ->
                    val volume = animation.animatedValue as Float
                    mediaPlayer?.setVolume(volume, volume)
                }
                start()
            }
        } catch (e: Exception) {}
    }

    private fun fadeOut(onFinished: () -> Unit = {}) {
        if (mediaPlayer == null) {
            onFinished()
            return
        }

        volumeAnimator?.cancel()
        try {
            val currentVol = try { if (mediaPlayer?.isPlaying == true) targetVolume else 0f } catch (e: Exception) { 0f }
            volumeAnimator = ValueAnimator.ofFloat(currentVol, 0f).apply {
                duration = 800
                addUpdateListener { animation ->
                    val volume = animation.animatedValue as Float
                    mediaPlayer?.setVolume(volume, volume)
                }
                addListener(object : android.animation.AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        onFinished()
                    }
                })
                start()
            }
        } catch (e: Exception) {
            onFinished()
        }
    }
}
