package de.malteans.leisureactivities.di

import de.malteans.datastore.DataStoreConfig
import de.malteans.leisureactivities.core.presentation.util.AndroidTTS
import de.malteans.leisureactivities.core.presentation.util.TextToSpeechService
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { OkHttp.create() }
        single<DataStoreConfig> { DataStoreConfig(androidApplication()) }

        single<TextToSpeechService> { AndroidTTS(androidApplication()) }
    }