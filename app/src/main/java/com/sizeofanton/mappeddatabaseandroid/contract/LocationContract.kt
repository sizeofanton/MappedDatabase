package com.sizeofanton.mappeddatabaseandroid.contract

import androidx.lifecycle.LiveData
import com.sizeofanton.mappeddatabaseandroid.data.pojo.ItemInfo
import io.reactivex.Completable
import io.reactivex.Observable

interface LocationContract {

    interface View {
        fun createItemClick(locationId: Int, title: String, count: Int, isRequired: Boolean)
        fun editItemClick(id: Int, title: String, count: Int, isRequired: Boolean)
        fun deleteItemClick(id: Int)
        fun showSnack(msg: String)
        fun backToLogin()
    }

    interface ViewModel {
        fun createItem(locationId: Int, title: String, count: Int, isRequired: Boolean)
        fun editItem(id: Int, title: String, count: Int, isRequired: Boolean)
        fun deleteItem(id: Int)
        fun setLocationId(id: Int)
        fun getItemsList(): LiveData<List<ItemInfo>>
        fun getMessage(): LiveData<String>
        fun getNetworkStatus(): LiveData<Boolean>
    }

    interface Model {
        fun setLocationId(id: Int)
        fun createNewItem(
            locationId: Int,
            title: String,
            count: Int,
            isRequired: Boolean
        ): Completable
        fun editItem(id: Int, title: String, count: Int, isRequired: Boolean): Completable
        fun deleteItem(id: Int): Completable
        fun getItems(): Observable<List<ItemInfo>>
    }
}
