package de.malteans.leisureactivities.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import de.malteans.datastore.createDataStore
import de.malteans.leisureactivities.core.data.DefaultDataStoreRepository
import de.malteans.leisureactivities.core.data.DefaultMainService
import de.malteans.leisureactivities.core.data.DefaultRemoteService
import de.malteans.leisureactivities.core.data.network.HttpClientFactory
import de.malteans.leisureactivities.core.domain.DataStoreRepository
import de.malteans.leisureactivities.core.domain.MainService
import de.malteans.leisureactivities.core.domain.RemoteService
import de.malteans.leisureactivities.core.presentation.settings.SettingsViewModel
import de.malteans.leisureactivities.registration.data.DefaultRegistrationService
import de.malteans.leisureactivities.registration.domain.RegistrationService
import de.malteans.leisureactivities.registration.presentation.RegistrationViewModel
import de.malteans.leisureactivities.signUp.data.DefaultSignUpService
import de.malteans.leisureactivities.signUp.domain.SignUpService
import de.malteans.leisureactivities.signUp.presentation.SignUpViewModel
import de.malteans.leisureactivities.staff.data.DefaultStaffService
import de.malteans.leisureactivities.staff.domain.StaffService
import de.malteans.leisureactivities.staff.presentation.activityDetails.ActivityDetailsViewModel
import de.malteans.leisureactivities.staff.presentation.modifyActivity.ModifyActivityViewModel
import de.malteans.leisureactivities.staff.presentation.overview.StaffOverviewViewModel
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
    viewModel { ModifyActivityViewModel(
        get()
    ) }
    viewModel { SettingsViewModel(
        get()
    ) }
}