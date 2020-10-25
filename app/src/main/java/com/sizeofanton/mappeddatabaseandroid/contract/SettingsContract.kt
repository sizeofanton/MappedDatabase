package com.sizeofanton.mappeddatabaseandroid.contract

import androidx.lifecycle.LiveData
import com.sizeofanton.mappeddatabaseandroid.util.LimitedCompletable

interface SettingsContract {

    interface View

    interface ViewModel {
        fun changePassword(oldPassword: String, newPassword: String)
        fun showToast(msg: String)
        fun getMessage(): LiveData<String>
    }

    interface Model {
        fun changePassword(oldPassword: String, newPassword: String): LimitedCompletable
    }

}