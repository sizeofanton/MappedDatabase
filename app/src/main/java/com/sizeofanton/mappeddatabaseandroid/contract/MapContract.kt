package com.sizeofanton.mappeddatabaseandroid.contract

import androidx.lifecycle.LiveData
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import com.sizeofanton.mappeddatabaseandroid.util.LimitedCompletable
import io.reactivex.Observable

interface MapContract {

    interface View {
        fun updateMarkersAndColors(list: List<LocationInfo>)
        fun showSnack(msg: String)
        fun backToLogin()
        fun onLocationInfoClick()
        fun myLocationClick()
    }

    interface ViewModel {
        fun stopUpdateTimer()
        fun startUpdateTimer()
        fun locationsGetErrorCallback(errorMsg: String)
        fun networkErrorCallback(errorMsg: String)
        fun manyNetworkErrorsCallback()
        fun getLocationsList(): LiveData<List<LocationInfo>>
        fun getMessage(): LiveData<String>
        fun getNetworkStatus(): LiveData<Boolean>
        fun logout(user: String)
    }

    interface Model {
        fun loadLocationsList(): Observable<List<LocationInfo>>
        fun logout(user: String): LimitedCompletable
    }
}
