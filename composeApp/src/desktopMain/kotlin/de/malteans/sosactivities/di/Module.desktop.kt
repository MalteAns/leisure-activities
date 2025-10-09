package de.malteans.sosactivities.di

import de.malteans.datastore.DataStoreConfig
import de.malteans.sosactivities.core.presentation.util.DesktopTTS
import de.malteans.sosactivities.core.presentation.util.TextToSpeechService
import io.ktor.client.engine.*
import io.ktor.client.engine.okhttp.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {

        single<HttpClientEngine> { OkHttp.create() }
        single<DataStoreConfig> { DataStoreConfig(null) }

        single<TextToSpeechService> { DesktopTTS() }
    }