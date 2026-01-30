package au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.ui

import android.animation.ValueAnimator
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.media.ToneGenerator
import au.edu.jcu.cp3406_cp5307_utilityappstartertemplate.R
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.random.Random

@Singleton
class SoundManager @Inject constructor(@ApplicationContext private val context: Context) {
    private val toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 40) // Reduced volume to 40
    private var mediaPlayer: MediaPlayer? = null
    private var currentThemeIndex: Int = -1
    private var volumeAnimator: ValueAnimator? = null
    private var targetVolume: Float = 0.15f // Reduced to 0.15f

    private val themes = listOf(
        R.raw.theme1,
        R.raw.theme2,
        R.raw.theme3
    )

    fun playRefreshSound() {
        toneGenerator.startTone(ToneGenerator.TONE_SUP_PIP, 40)
    }

    fun playNavigationSound() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_PIP, 30)
    }

    fun playToggleSound() {
        toneGenerator.startTone(ToneGenerator.TONE_CDMA_SOFT_ERROR_LITE, 25)
    }

    fun playScrollSound() {
        toneGenerator.startTone(ToneGenerator.TONE_SUP_PIP, 20)
    }

    fun startRandomMusic() {
        val randomIndex = Random.nextInt(themes.size)
        playMusic(randomIndex)
    }

    fun playMusic(themeIndex: Int) {
        if (currentThemeIndex == themeIndex && mediaPlayer != null) {
            try {
                if (!mediaPlayer!!.isPlaying) {
                    fadeIn()
                }
            } catch (e: Exception) {
                // Handle possible state issues
                stopMusic()
                createAndStartPlayer(themeIndex)
            }
            return
        }

        if (mediaPlayer == null) {
            createAndStartPlayer(themeIndex)
        } else {
            fadeOut {
                stopMusic()
                createAndStartPlayer(themeIndex)
            }
        }
    }

    private fun createAndStartPlayer(themeIndex: Int) {
        currentThemeIndex = themeIndex
        mediaPlayer = MediaPlayer.create(context, themes[themeIndex]).apply {
            isLooping = true
            setVolume(0f, 0f)
            start()
        }
        fadeIn()
    }

    fun pauseMusic() {
        fadeOut {
            try {
                mediaPlayer?.pause()
            } catch (e: Exception) {}
        }
    }

    fun stopMusic() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) it.stop()
                it.release()
            }
        } catch (e: Exception) {
            // Ignore errors during release
        } finally {
            mediaPlayer = null
            currentThemeIndex = -1
        }
    }

    fun handleMusicState(enabled: Boolean, themeIndex: Int) {
        if (!enabled) {
            pauseMusic()
        } else {
            if (currentThemeIndex != themeIndex) {
                playMusic(themeIndex)
            } else {
                if (mediaPlayer == null) {
                    playMusic(themeIndex)
                } else {
                    try {
                        if (!mediaPlayer!!.isPlaying) fadeIn()
                    } catch (e: Exception) {
                        playMusic(themeIndex)
                    }
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
            val currentVol = if (mediaPlayer!!.isPlaying) targetVolume else 0f
            volumeAnimator = ValueAnimator.ofFloat(currentVol, 0f).apply {
                duration = 800 // Slightly faster fade out for responsiveness
                addUpdateListener { animation ->
                    val volume = animation.animatedValue as Float
                    mediaPlayer?.setVolume(volume, volume)
                }
                postOnAnimation {
                    if (!isRunning) onFinished()
                }
                start()
            }
        } catch (e: Exception) {
            onFinished()
        }
    }
    
    private fun ValueAnimator.postOnAnimation(action: () -> Unit) {
        addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: android.animation.Animator) {
                action()
            }
        })
    }
}
