package de.malteans.leisureactivities.core.presentation.util

import android.content.Context
import android.speech.tts.TextToSpeech
import java.util.*

class AndroidTTS(private val context: Context) : TextToSpeechService {
    private var tts: TextToSpeech? = null

    init {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                tts?.language = Locale.getDefault()
            }
        }
    }

    override fun speak(text: String) {
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "utteranceId")
    }
}
