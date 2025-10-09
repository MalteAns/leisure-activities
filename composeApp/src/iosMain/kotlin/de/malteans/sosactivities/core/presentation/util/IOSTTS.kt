package de.malteans.sosactivities.core.presentation.util

import platform.AVFAudio.AVSpeechSynthesisVoice
import platform.AVFAudio.AVSpeechSynthesizer
import platform.AVFAudio.AVSpeechUtterance

class IOSTTS : TextToSpeechService {
    private val synthesizer = AVSpeechSynthesizer()

    override fun speak(text: String) {
        val utterance = AVSpeechUtterance(string = text)
        utterance.voice = AVSpeechSynthesisVoice.voiceWithLanguage("de-DE")
        synthesizer.speakUtterance(utterance)
    }
}
