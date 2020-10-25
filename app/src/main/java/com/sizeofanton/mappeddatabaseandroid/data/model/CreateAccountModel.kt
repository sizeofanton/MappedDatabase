package com.sizeofanton.mappeddatabaseandroid.data.model

import com.sizeofanton.mappeddatabaseandroid.contract.CreateAccountContract
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import io.reactivex.Completable
import io.reactivex.CompletableObserver
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class CreateAccountModel: CreateAccountContract.Model, KoinComponent {

    private val networkService: NetworkService by inject()

    override fun createAccount(username: String, password: String): Completable {

        val completable = object: Completable() {

            private val subsList = mutableListOf<CompletableObserver>()

            fun complete() {
                subsList.forEach { it.onComplete() }
            }

            fun error(msg: String) {
                subsList.forEach { it.onError(Throwable(msg)) }
            }

            override fun subscribeActual(observer: CompletableObserver?) {
                if (observer != null) subsList.add(observer)
            }
        }

        networkService
            .getAuthApi()
            .register(username, password)
            .enqueue(object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    Timber.d("Error creating new user - ${t.message}")
                    completable.error(t.message!!)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == 200) {
                        completable.complete()
                    }
                }
            })

        return completable.observeOn(Schedulers.io()).subscribeOn(AndroidSchedulers.mainThread())
    }

}