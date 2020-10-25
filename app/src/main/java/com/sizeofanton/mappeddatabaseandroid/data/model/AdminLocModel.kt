package com.sizeofanton.mappeddatabaseandroid.data.model

import android.annotation.SuppressLint
import com.sizeofanton.mappeddatabaseandroid.contract.AdminLocContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import com.sizeofanton.mappeddatabaseandroid.util.HttpStatusCode
import com.sizeofanton.mappeddatabaseandroid.util.LimitedCompletable
import com.sizeofanton.mappeddatabaseandroid.util.NonCriticalError
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
class AdminLocModel: AdminLocContract.Model, KoinComponent {

    private val networkService: NetworkService by inject()

    override fun getLocations(): Observable<List<LocationInfo>> {
        return Observable.create(object: ObservableOnSubscribe<List<LocationInfo>> {
            val executor = Executors.newSingleThreadScheduledExecutor()
            override fun subscribe(emitter: ObservableEmitter<List<LocationInfo>>) {
                val future = executor.scheduleAtFixedRate({
                    networkService.getUserApi()
                        .getLocations(CurrentUser.getToken())
                        .enqueue(object : Callback<List<LocationInfo>> {
                            override fun onResponse(
                                call: Call<List<LocationInfo>>,
                                response: Response<List<LocationInfo>>
                            ) {
                                if (response.code() == HttpStatusCode.OK) emitter.onNext(response.body() as List<LocationInfo>)
                                else emitter.onError(NonCriticalError(response.message()))
                            }

                            override fun onFailure(call: Call<List<LocationInfo>>, t: Throwable) {
                               emitter.onError(t)
                            }
                        })
                }, 0, 1, TimeUnit.SECONDS)
            }
        })
    }


    override fun editLocation(id: Int, title: String, latitude: Double, longitude: Double): Completable {
        val completable = LimitedCompletable()

        networkService.getAdminApi()
            .editLocation(CurrentUser.getToken(), id, title, latitude, longitude)
            .enqueue(object : Callback<String> {
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

    override fun addLocation(title: String, latitude: Double, longitude: Double): Completable {
        val completable = LimitedCompletable()

        networkService.getAdminApi()
            .addLocation(CurrentUser.getToken(), title, latitude, longitude)
            .enqueue(object : Callback<String> {
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


    override fun deleteLocation(id: Int): Completable {
        val completable = LimitedCompletable()

        networkService.getAdminApi()
            .removeLocation(CurrentUser.getToken(), id)
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == 200) completable.complete()
                    else completable.error(NonCriticalError(response.message()))
                }
            })

        return completable.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
    }

}