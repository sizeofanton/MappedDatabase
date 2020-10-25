package com.sizeofanton.mappeddatabaseandroid.ui.location

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.LocationContract
import com.sizeofanton.mappeddatabaseandroid.data.pojo.ItemInfo
import com.sizeofanton.mappeddatabaseandroid.util.NonCriticalError
import org.koin.core.KoinComponent
import org.koin.core.inject

@SuppressLint("CheckResult")
class LocationViewModel(val app: Application) :
    AndroidViewModel(app),
    LocationContract.ViewModel,
    LifecycleObserver,
    KoinComponent {

    private val model: LocationContract.Model by inject()

    private val itemsList = MutableLiveData<List<ItemInfo>>()
    private val snackMessage = MutableLiveData<String>()
    private val networkStatus =
        MutableLiveData<Boolean>().apply { value = true }

    private var subscription = model.getItems()
        .subscribe {
            itemsList.value = it
        }

    private var networkErrors = 0
        set(value) {
            if (field > 5) networkStatus.value = false
            field = 0
        }


    override fun createItem(locationId: Int, title: String, count: Int, isRequired: Boolean) {
        model.createNewItem(locationId, title, count, isRequired)
            .subscribe({
                snackMessage.value = app.getString(R.string.item_added_successfully)
            }, {
                if (it !is NonCriticalError) networkErrors++
                snackMessage.value = app.getString(R.string.error_adding_item, it.message)
            })
    }

    override fun editItem(id: Int, title: String, count: Int, isRequired: Boolean) {
        model.editItem(id, title, count, isRequired)
            .subscribe({
                snackMessage.value = app.getString(R.string.item_edited_successfully)
            }, {
                if (it !is NonCriticalError) networkErrors++
                snackMessage.value = app.getString(R.string.error_editing_item, it.message)
            })
    }

    override fun deleteItem(id: Int) {
        model.deleteItem(id)
            .subscribe({
                snackMessage.value = app.getString(R.string.item_deleted_successfully)
            },{
                if (it !is NonCriticalError) networkErrors++
                snackMessage.value = app.getString(R.string.error_deleting_tem, it.message)
            })
    }

    override fun setLocationId(id: Int) {
        model.setLocationId(id)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        subscription.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        subscription = model.getItems()
            .subscribe {
                itemsList.value = it
            }
    }


    override fun getItemsList(): LiveData<List<ItemInfo>> = itemsList
    override fun getMessage(): LiveData<String> = snackMessage
    override fun getNetworkStatus(): LiveData<Boolean> = networkStatus
}
