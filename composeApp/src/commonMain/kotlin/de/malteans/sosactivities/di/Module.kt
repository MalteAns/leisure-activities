package de.malteans.sosactivities.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import de.malteans.datastore.createDataStore
import de.malteans.sosactivities.core.data.DefaultDataStoreRepository
import de.malteans.sosactivities.core.data.DefaultMainService
import de.malteans.sosactivities.core.data.DefaultRemoteService
import de.malteans.sosactivities.core.data.network.HttpClientFactory
import de.malteans.sosactivities.core.domain.DataStoreRepository
import de.malteans.sosactivities.core.domain.MainService
import de.malteans.sosactivities.core.domain.RemoteService
import de.malteans.sosactivities.core.presentation.settings.SettingsViewModel
import de.malteans.sosactivities.registration.data.DefaultRegistrationService
import de.malteans.sosactivities.registration.domain.RegistrationService
import de.malteans.sosactivities.registration.presentation.RegistrationViewModel
import de.malteans.sosactivities.signUp.data.DefaultSignUpService
import de.malteans.sosactivities.signUp.domain.SignUpService
import de.malteans.sosactivities.signUp.presentation.SignUpViewModel
import de.malteans.sosactivities.staff.data.DefaultStaffService
import de.malteans.sosactivities.staff.domain.StaffService
import de.malteans.sosactivities.staff.presentation.activityDetails.ActivityDetailsViewModel
import de.malteans.sosactivities.staff.presentation.overview.StaffOverviewViewModel
import io.ktor.client.*
import org.koin.core.module.Module
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module


expect val platformModule: Module

val sharedModule = module {
    single<HttpClient> { HttpClientFactory.create(get()) }

    single<DataStore<Preferences>> { createDataStore(get()) }

    single<DataStoreRepository> { DefaultDataStoreRepository(get()) }

    single<RemoteService> { DefaultRemoteService(get(), get()) }

    single<MainService> { DefaultMainService(get(), get()) }
    single<RegistrationService> { DefaultRegistrationService(get(), get()) }
    single<SignUpService> { DefaultSignUpService(get()) }
    single<StaffService> { DefaultStaffService(get()) }

    viewModel { RegistrationViewModel(
        get()
    ) }
    viewModel { SignUpViewModel(
        get(), get(), get()
    ) }
    viewModel { StaffOverviewViewModel(
        get()
    ) }
    viewModel { ActivityDetailsViewModel(
        get()
    ) }
    viewModel { SettingsViewModel(
        get()
    ) }
}