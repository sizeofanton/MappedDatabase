package com.sizeofanton.mappeddatabaseandroid.di

import com.sizeofanton.mappeddatabaseandroid.contract.*
import com.sizeofanton.mappeddatabaseandroid.data.model.*
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import com.sizeofanton.mappeddatabaseandroid.ui.admin_loc.AdminLocViewModel
import com.sizeofanton.mappeddatabaseandroid.ui.admin_usr.AdminUserViewModel
import com.sizeofanton.mappeddatabaseandroid.ui.location.LocationViewModel
import com.sizeofanton.mappeddatabaseandroid.ui.login.LoginViewModel
import com.sizeofanton.mappeddatabaseandroid.ui.map.MapViewModel
import com.sizeofanton.mappeddatabaseandroid.ui.settings.SettingsViewModel
import org.koin.android.ext.koin.androidApplication
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module


val appModule = module {

    single { NetworkService.getInstance() }

    viewModel { LoginViewModel(androidApplication()) }
    factory<LoginContract.Model> { LoginModel() }

    viewModel { MapViewModel(androidApplication())}
    factory<MapContract.Model> { MapModel() }

    viewModel { AdminUserViewModel(androidApplication()) }
    factory<AdminUserContract.Model> { AdminUserModel() }

    viewModel { AdminLocViewModel(androidApplication()) }
    factory<AdminLocContract.Model> { AdminLocModel() }

    viewModel { LocationViewModel(androidApplication()) }
    factory<LocationContract.Model> { LocationModel() }

    viewModel { SettingsViewModel(androidApplication())}
    factory<SettingsContract.Model> { SettingsModel() }
}