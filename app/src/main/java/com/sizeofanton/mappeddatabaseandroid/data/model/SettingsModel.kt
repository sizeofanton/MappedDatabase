package com.sizeofanton.mappeddatabaseandroid.data.model

import android.annotation.SuppressLint
import com.sizeofanton.mappeddatabaseandroid.contract.SettingsContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import com.sizeofanton.mappeddatabaseandroid.util.HttpStatusCode
import com.sizeofanton.mappeddatabaseandroid.util.LimitedCompletable
import com.sizeofanton.mappeddatabaseandroid.util.NonCriticalError
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

@SuppressLint("CheckResult")
class SettingsModel: SettingsContract.Model, KoinComponent {

    private val networkService: NetworkService by inject()

    override fun changePassword(oldPassword: String, newPassword: String): LimitedCompletable {
        val completable = LimitedCompletable()

        networkService.getAuthApi()
            .changePassword(CurrentUser.getUser(), oldPassword, newPassword)
            .enqueue(object: Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) completable.complete()
                    else completable.error(NonCriticalError(response.message()))
                }


                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }
            })


        return completable
    }
}