package com.sizeofanton.mappeddatabaseandroid.ui.map

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.MapContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
@SuppressLint("CheckResult")
class MapViewModel(val app: Application) :
    AndroidViewModel(app),
    MapContract.ViewModel,
    LifecycleObserver,
    KoinComponent {

    private val model: MapContract.Model by inject()

    private val locationsList = MutableLiveData<List<LocationInfo>>()
    private val snackMessage = MutableLiveData<String>()
    private val networkStatus = MutableLiveData<Boolean>().apply {
        value = true
    }

    private var subscription = model.loadLocationsList()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe({ list ->
            locationsList.value = list
        }, { throwable ->
            snackMessage.value = throwable.message
        })


    override fun logout(user: String) {
        model.logout(CurrentUser.getUser()).subscribe({
            snackMessage.value = "Logout ok"
        },{
            snackMessage.value = "Logout error - ${it.message}"
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun stopUpdateTimer() = subscription.dispose()

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun startUpdateTimer() {
        subscription = model.loadLocationsList()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ list ->
                locationsList.value = list
            }, { throwable ->
                snackMessage.value = throwable.message
            })
    }

    override fun locationsGetErrorCallback(errorMsg: String) {
        snackMessage.value = app.getString(R.string.error_getting_locations, errorMsg)
    }

    override fun networkErrorCallback(errorMsg: String){
        snackMessage.value = app.getString(R.string.network_error, errorMsg)
    }

    override fun manyNetworkErrorsCallback(){
        networkStatus.value = false
    }

    override fun getLocationsList(): LiveData<List<LocationInfo>> = locationsList
    override fun getMessage(): LiveData<String>  = snackMessage
    override fun getNetworkStatus(): LiveData<Boolean> = networkStatus
}
