package de.malteans.sosactivities.di

import de.malteans.datastore.DataStoreConfig
import de.malteans.sosactivities.core.presentation.util.IOSTTS
import de.malteans.sosactivities.core.presentation.util.TextToSpeechService
import io.ktor.client.engine.*
import io.ktor.client.engine.darwin.*
import org.koin.core.module.Module
import org.koin.dsl.module

actual val platformModule: Module
    get() = module {
        single<HttpClientEngine> { Darwin.create() }
        single<DataStoreConfig> { DataStoreConfig(null) }

        single<TextToSpeechService> { IOSTTS() }
    }