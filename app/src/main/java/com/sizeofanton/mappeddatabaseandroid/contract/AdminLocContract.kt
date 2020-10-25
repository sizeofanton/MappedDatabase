package com.sizeofanton.mappeddatabaseandroid.contract

import androidx.lifecycle.LiveData
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LocationInfo
import io.reactivex.Completable
import io.reactivex.Observable

interface AdminLocContract {
    interface View {
        fun addLocationClick(title: String, latitude: Double, longitude: Double)
        fun editLocationClick(id: Int, title: String, latitude: Double, longitude: Double)
        fun deleteLocationClick(id: Int)
        fun showSnack(msg: String)
        fun backToLogin()
    }

    interface ViewModel {
        fun addLocation(title: String, latitude: Double, longitude: Double)
        fun editLocation(id: Int, title: String, latitude: Double, longitude: Double)
        fun deleteLocation(id: Int)
        fun getLocationsList(): LiveData<List<LocationInfo>>
        fun getMessage(): LiveData<String>
        fun getNetworkStatus(): LiveData<Boolean>
    }

    interface Model {
        fun getLocations(): Observable<List<LocationInfo>>
        fun editLocation(id: Int, title: String, latitude: Double, longitude: Double): Completable
        fun addLocation(title: String, latitude: Double, longitude: Double): Completable
        fun deleteLocation(id: Int): Completable
    }
}
