package com.hassanaj.mindgrid

import android.media.AudioManager
import android.media.ToneGenerator

object SoundManager {
    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 70)
        } catch (_: Exception) {
            toneGenerator = null
        }
    }

    private val tileTones = intArrayOf(
        ToneGenerator.TONE_DTMF_1,
        ToneGenerator.TONE_DTMF_2,
        ToneGenerator.TONE_DTMF_3,
        ToneGenerator.TONE_DTMF_4,
        ToneGenerator.TONE_DTMF_5,
        ToneGenerator.TONE_DTMF_6,
        ToneGenerator.TONE_DTMF_7,
        ToneGenerator.TONE_DTMF_8,
        ToneGenerator.TONE_DTMF_9,
        ToneGenerator.TONE_DTMF_0,
        ToneGenerator.TONE_DTMF_A,
        ToneGenerator.TONE_DTMF_B,
        ToneGenerator.TONE_DTMF_C,
        ToneGenerator.TONE_DTMF_D,
        ToneGenerator.TONE_PROP_BEEP,
        ToneGenerator.TONE_PROP_ACK
    )

    fun playTileSound(tileIndex: Int) {
        val tone = tileTones.getOrElse(tileIndex % tileTones.size) { ToneGenerator.TONE_PROP_BEEP }
        try {
            toneGenerator?.startTone(tone, 160)
        } catch (_: Exception) {}
    }

    fun playErrorSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 350)
        } catch (_: Exception) {}
    }

    fun playSuccessSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 250)
        } catch (_: Exception) {}
    }
}
