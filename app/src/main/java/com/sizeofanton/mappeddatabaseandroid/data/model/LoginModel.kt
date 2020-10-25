package com.sizeofanton.mappeddatabaseandroid.data.model

import com.sizeofanton.mappeddatabaseandroid.contract.LoginContract
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LoginInfo
import com.sizeofanton.mappeddatabaseandroid.data.remote.NetworkService
import io.reactivex.Single
import io.reactivex.SingleObserver
import org.koin.core.KoinComponent
import org.koin.core.inject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginModel : LoginContract.Model, KoinComponent {

    val networkService: NetworkService by inject()

    override fun handleLogin(user: String, password: String): Single<Response<LoginInfo>>
    {
        return object : Single<Response<LoginInfo>>() {
            override fun subscribeActual(observer: SingleObserver<in Response<LoginInfo>>) {
                networkService.getAuthApi()
                    .login(user, password)
                    .enqueue(object : Callback<LoginInfo> {
                        override fun onFailure(call: Call<LoginInfo>, t: Throwable) {
                            observer.onError(t)
                        }

                        override fun onResponse(
                            call: Call<LoginInfo>,
                            response: Response<LoginInfo>
                        ) {
                            observer.onSuccess(response)
                        }
                    })
            }
        }


    }
}
