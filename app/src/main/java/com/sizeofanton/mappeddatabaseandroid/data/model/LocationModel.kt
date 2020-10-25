package com.sizeofanton.mappeddatabaseandroid.data.model

import android.annotation.SuppressLint
import com.sizeofanton.mappeddatabaseandroid.contract.LocationContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.pojo.ItemInfo
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import com.sizeofanton.mappeddatabaseandroid.util.HttpStatusCode
import com.sizeofanton.mappeddatabaseandroid.util.LimitedCompletable
import com.sizeofanton.mappeddatabaseandroid.util.NonCriticalError
import com.sizeofanton.mappeddatabaseandroid.util.ext.toInt
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

@SuppressLint("CheckResult")
class LocationModel: LocationContract.Model, KoinComponent {

    private val networkService: NetworkService by inject()
    private var locationId: Int = -1

    override fun createNewItem(
        locationId: Int,
        title: String,
        count: Int,
        isRequired: Boolean
    ): Completable {
        val completable = LimitedCompletable()

        networkService.getUserApi()
            .addItem(CurrentUser.getToken(), locationId, title, count, isRequired.toInt())
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) completable.complete()
                    else completable.error(NonCriticalError(response.message()))
                }
            })

        return completable
    }


    override fun editItem(id: Int, title: String, count: Int, isRequired: Boolean): Completable {
        val completable = LimitedCompletable()

        networkService.getUserApi()
            .editItem(CurrentUser.getToken(), id, title, count, isRequired.toInt())
            .enqueue(object: Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) completable.complete()
                    else completable.error(NonCriticalError(response.message()))
                }
            })

        return completable
    }

    override fun deleteItem(id: Int): Completable {
        val completable = LimitedCompletable()

        networkService.getUserApi()
            .removeItem(CurrentUser.getToken(), id)
            .enqueue(object : Callback<String> {
                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.code() == HttpStatusCode.OK) completable.complete()
                    else completable.error(NonCriticalError(response.message()))
                }
            })

        return completable
    }


    override fun getItems(): Observable<List<ItemInfo>> {
        return Observable.create(object : ObservableOnSubscribe<List<ItemInfo>> {
            val executor = Executors.newSingleThreadScheduledExecutor()
            override fun subscribe(emitter: ObservableEmitter<List<ItemInfo>>) {
                val future = executor.scheduleAtFixedRate({
                    networkService.getUserApi()
                        .getItems(CurrentUser.getToken(), locationId)
                        .enqueue(object: Callback<List<ItemInfo>> {
                            override fun onFailure(call: Call<List<ItemInfo>>, t: Throwable) {
                                emitter.onError(t)
                            }

                            override fun onResponse(
                                call: Call<List<ItemInfo>>,
                                response: Response<List<ItemInfo>>
                            ) {
                                emitter.onNext(response.body() as List<ItemInfo>)
                            }
                        })
                }, 0, 1, TimeUnit.SECONDS)
            }
        })
    }

    override fun setLocationId(id: Int) {
        locationId = id
    }
}