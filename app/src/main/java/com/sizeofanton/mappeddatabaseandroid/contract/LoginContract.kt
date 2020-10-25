package com.sizeofanton.mappeddatabaseandroid.contract

import androidx.lifecycle.LiveData
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LoginInfo
import io.reactivex.Single
import retrofit2.Response

interface LoginContract {
    interface View {
        fun showSnack(msg: String)
        fun onLoginClick()
        fun onCreateAccountClick()
        fun launchMapFragment()
    }

    interface ViewModel {
        fun startProgressBar()
        fun stopProgressBar()
        fun getMessage(): LiveData<String>
    }

    interface Model {
        fun handleLogin(user: String, password: String): Single<Response<LoginInfo>>
    }
}
