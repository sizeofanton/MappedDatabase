package com.sizeofanton.mappeddatabaseandroid.ui.admin_usr

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.*
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.AdminUserContract
import com.sizeofanton.mappeddatabaseandroid.data.pojo.UserInfo
import com.sizeofanton.mappeddatabaseandroid.util.NonCriticalError
import org.koin.core.KoinComponent
import org.koin.core.inject

@SuppressLint("CheckResult")
class AdminUserViewModel(val app: Application) :
    AndroidViewModel(app),
    AdminUserContract.ViewModel,
    LifecycleObserver,
    KoinComponent {

    val model: AdminUserContract.Model by inject()

    private val usersList = MutableLiveData<List<UserInfo>>()
    private val snackMessage = MutableLiveData<String>()
    private var networkErrors = MutableLiveData(0)

    private var subscription = model.getUsers()
        .subscribe {
            usersList.value = it
        }

    override fun addNewUser(user: String, password: String, isAdmin: Boolean) {
        model.addUser(user, password, isAdmin).subscribe({
            snackMessage.value = app.getString(R.string.user_added_successfully)
        }, {
            if (it !is NonCriticalError) networkErrors.value?.inc()
            snackMessage.value = app.getString(R.string.user_adding_error, it.message)
        })
    }

    override fun editUser(id: Int, password: String, isAdmin: Boolean, isActive: Boolean) {
        model.editUser(id, password, isAdmin, isActive).subscribe({
            snackMessage.value = app.getString(R.string.user_edited_successfully)
        }, {
            if (it !is NonCriticalError) networkErrors.value?.inc()
            snackMessage.value = app.getString(R.string.user_editing_error, it.message)
        })
    }
    override fun deleteUser(id: Int) {
        model.deleteUser(id). subscribe({
            snackMessage.value = app.getString(R.string.user_deleted_successfully)
        }, {
            if (it !is NonCriticalError) networkErrors.value?.inc()
            snackMessage.value = app.getString(R.string.user_deleting_error, it.message)
        })
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        subscription.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        subscription = model.getUsers()
            .subscribe {
                usersList.value = it
            }
    }


    override fun getUsersList(): LiveData<List<UserInfo>> = usersList
    override fun getMessage(): LiveData<String> = snackMessage
    override fun getNetworkErrors(): LiveData<Int> = networkErrors
}
