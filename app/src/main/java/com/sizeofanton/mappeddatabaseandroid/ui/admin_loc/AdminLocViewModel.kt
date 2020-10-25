package com.sizeofanton.mappeddatabaseandroid.ui.admin_loc

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.AdminLocContract
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import com.sizeofanton.mappeddatabaseandroid.util.NonCriticalError
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

@SuppressLint("CheckResult")
class AdminLocViewModel(val app: Application) :
    AndroidViewModel(app),
    AdminLocContract.ViewModel,
    LifecycleObserver,
    KoinComponent {

    private val model: AdminLocContract.Model by inject()

    private val locationsList = MutableLiveData<List<LocationInfo>>()
    private val snackMsg = MutableLiveData<String>()
    private val networkStatus = MutableLiveData<Boolean>().apply {
        value = true
    }

    private var subscription = model.getLocations()
        .subscribe {
            locationsList.value = it
        }

    private var networkErrors = 0
        set(_) {
            if (field > 5) networkStatus.value = false
            field = 0
        }


    override fun addLocation(title: String, latitude: Double, longitude: Double) {
        model.addLocation(title, latitude, longitude)
            .subscribe({
                snackMsg.value = app.getString(R.string.location_added_successfully)
            }, {
                if (it !is NonCriticalError) networkErrors++
                snackMsg.value = app.getString(R.string.error_adding_location, it.message)
            })
    }

    override fun editLocation(id: Int, title: String, latitude: Double, longitude: Double) {
        model.editLocation(id, title, latitude, longitude)
            .subscribe({
                snackMsg.value = app.getString(R.string.location_edited_successfully)
            }, {
                if (it !is NonCriticalError) networkErrors++
                snackMsg.value = app.getString(R.string.error_editing_location, it.message)
            })
    }

    override fun deleteLocation(id: Int) {
        model.deleteLocation(id)
            .subscribe({
                snackMsg.value = app.getString(R.string.location_deleted_successfully)
            }, {
                if (it !is NonCriticalError) networkErrors++
                snackMsg.value = app.getString(R.string.error_deleting_location, it.message)
            })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        subscription.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        subscription = model.getLocations()
            .subscribe {
                locationsList.value = it
            }
    }

    override fun getMessage(): LiveData<String> = snackMsg
    override fun getNetworkStatus(): LiveData<Boolean> = networkStatus
    override fun getLocationsList(): LiveData<List<LocationInfo>> = locationsList


}
