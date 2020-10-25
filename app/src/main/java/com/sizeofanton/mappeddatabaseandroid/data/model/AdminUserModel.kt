package com.sizeofanton.mappeddatabaseandroid.data.model

import android.annotation.SuppressLint
import com.sizeofanton.mappeddatabaseandroid.contract.AdminUserContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.pojo.UserInfo
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import com.sizeofanton.mappeddatabaseandroid.util.HttpStatusCode
import com.sizeofanton.mappeddatabaseandroid.util.LimitedCompletable
import com.sizeofanton.mappeddatabaseandroid.util.NonCriticalError
import com.sizeofanton.mappeddatabaseandroid.util.ext.toInt
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class AdminUserModel: AdminUserContract.Model, KoinComponent {

    private val networkService: NetworkService by inject()


    override fun getUsers(): Observable<List<UserInfo>> {
        return Observable.create(object: ObservableOnSubscribe<List<UserInfo>> {
            val executor = Executors.newSingleThreadScheduledExecutor()
            override fun subscribe(emitter: ObservableEmitter<List<UserInfo>>) {
                val future = executor.scheduleAtFixedRate({
                    networkService.getAdminApi()
                        .getUsers(CurrentUser.getToken())
                        .enqueue(object: Callback<List<UserInfo>> {
                            override fun onFailure(call: Call<List<UserInfo>>, t: Throwable) {
                                emitter.onError(t)
                            }

                            override fun onResponse(
                                call: Call<List<UserInfo>>,
                                response: Response<List<UserInfo>>
                            ) {
                                emitter.onNext(response.body() as List<UserInfo>)
                            }
                        })
                }, 0, 1, TimeUnit.SECONDS)

            }
        })
    }

    override fun addUser(user: String, password: String, isAdmin: Boolean): Completable {
        val completable = LimitedCompletable()

        networkService.getAdminApi()
            .addUser(CurrentUser.getToken(), user, password, isAdmin.toInt())
            .enqueue(object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) completable.complete()
                    else completable.error(NonCriticalError(response.message()))
                }
            })

        return completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    override fun editUser(id: Int, password: String, isAdmin: Boolean, isActive: Boolean): Completable {
        val completable = LimitedCompletable()

        networkService.getAdminApi()
            .editUser(CurrentUser.getToken(), id, password, isAdmin.toInt(), isActive.toInt())
            .enqueue(object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) completable.complete()
                    else completable.error(NonCriticalError(response.message()))
                }
            })

        return completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }


    override fun deleteUser(id: Int): Completable {
        val completable = LimitedCompletable()

        networkService.getAdminApi()
            .removeUser(CurrentUser.getToken(), id)
            .enqueue(object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) completable.complete()
                    else completable.error(NonCriticalError(response.message()))
                }
            })

        return completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }
}