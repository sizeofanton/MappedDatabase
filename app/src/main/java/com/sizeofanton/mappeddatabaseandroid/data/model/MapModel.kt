package com.sizeofanton.mappeddatabaseandroid.data.model

import android.annotation.SuppressLint
import com.sizeofanton.mappeddatabaseandroid.contract.MapContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import com.sizeofanton.mappeddatabaseandroid.util.HttpStatusCode
import com.sizeofanton.mappeddatabaseandroid.util.LimitedCompletable
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
class MapModel() : MapContract.Model, KoinComponent {

    private val networkService: NetworkService by inject()

    override fun loadLocationsList(): Observable<List<LocationInfo>> {
        return Observable.create(object: ObservableOnSubscribe<List<LocationInfo>> {
            val executor = Executors.newSingleThreadScheduledExecutor()
            override fun subscribe(emitter: ObservableEmitter<List<LocationInfo>>) {
                val future = executor.scheduleAtFixedRate({
                    networkService.getUserApi()
                        .getLocations(CurrentUser.getToken())
                        .enqueue(object: Callback<List<LocationInfo>> {
                            override fun onResponse(
                                call: Call<List<LocationInfo>>,
                                response: Response<List<LocationInfo>>
                            ) {
                                if (response.code() == HttpStatusCode.OK) {
                                    emitter.onNext(response.body() as List<LocationInfo>)
                                }
                            }

                            override fun onFailure(call: Call<List<LocationInfo>>, t: Throwable) {
                                emitter.onError(t)
                            }
                        })
                }, 0, 5000, TimeUnit.MILLISECONDS)

                emitter.setCancellable {future.cancel(false)}
            }
        })
    }

    override fun logout(user: String): LimitedCompletable {
        val completable = LimitedCompletable()
        networkService.getAuthApi()
            .logout(user)
            .enqueue(object : Callback<String> {
                override fun onResponse(call: Call<String>, response: Response<String>) {
                    completable.complete()
                }

                override fun onFailure(call: Call<String>, t: Throwable) {
                    completable.error(t)
                }
            })


        return completable
    }
}
