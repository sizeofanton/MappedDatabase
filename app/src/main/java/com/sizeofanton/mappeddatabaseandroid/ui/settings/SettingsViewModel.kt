package com.sizeofanton.mappeddatabaseandroid.ui.settings

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.SettingsContract
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject
import timber.log.Timber

@SuppressLint("CheckResult")
class SettingsViewModel(val app: Application) :
    AndroidViewModel(app),
    SettingsContract.ViewModel,
    KoinComponent {

    private val model: SettingsContract.Model by inject()
    private val snackMessage = MutableLiveData<String>()


    override fun changePassword(oldPassword: String, newPassword: String) {
        Timber.d("OLD PASS - $oldPassword, NEW PASS - $newPassword")
        model.changePassword(oldPassword, newPassword)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                snackMessage.value = app.getString(R.string.password_changed_successfully)
            },{
                snackMessage.value = app.getString(R.string.password_changing_error, it.message)
            })
    }

    override fun showToast(msg: String) {
        snackMessage.value = msg
    }

    override fun getMessage(): LiveData<String> = snackMessage
}