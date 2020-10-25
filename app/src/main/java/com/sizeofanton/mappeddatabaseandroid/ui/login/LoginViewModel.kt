package com.sizeofanton.mappeddatabaseandroid.ui.login

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.LoginContract
import com.sizeofanton.mappeddatabaseandroid.data.local.CurrentUser
import com.sizeofanton.mappeddatabaseandroid.data.pojo.LoginInfo
import com.sizeofanton.mappeddatabaseandroid.util.HttpStatusCode
import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject


class LoginViewModel(val app: Application) :
    AndroidViewModel(app),
    LoginContract.ViewModel,
    LifecycleObserver,
    KoinComponent {

    private val model: LoginContract.Model by inject()
    private val snackMessage = MutableLiveData<String>()


    enum class Event {
        ProgressVisible,
        ProgressInvisible,
        LoginSuccessful
    }

    class Events: ObservableOnSubscribe<Event> {
        private lateinit var view: ObservableEmitter<Event>

        override fun subscribe(emitter: ObservableEmitter<Event>) {
            view = emitter
        }

        fun loginSuccess() = view.onNext(Event.LoginSuccessful)
        fun progressVisible() = view.onNext(Event.ProgressVisible)
        fun progressInvisible() = view.onNext(Event.ProgressInvisible)
    }

    private val events = Events()
    val eventBus = Observable.create(events)

    @SuppressLint("CheckResult")
    fun handleLogin(user: String, password: String) {
        startProgressBar()
        model.handleLogin(user, password)
            .observeOn(Schedulers.io())
            .subscribeOn(AndroidSchedulers.mainThread())
            .subscribe({
                when (it.code()) {
                        HttpStatusCode.OK -> {
                            val loginInfo: LoginInfo = it.body() as LoginInfo
                            CurrentUser.set(user, loginInfo.token, loginInfo.isAdmin)
                            events.loginSuccess()
                        }
                        HttpStatusCode.Unauthorized -> snackMessage.postValue(app.getString(R.string.wrong_password))
                        HttpStatusCode.NotFound -> snackMessage.postValue(app.getString(R.string.no_such_user))
                        HttpStatusCode.Forbidden -> snackMessage.postValue(app.getString(R.string.user_is_blocked))
                }
            }, {
                snackMessage.postValue(it.message)
                stopProgressBar()
            })
    }

    override fun startProgressBar() = events.progressVisible()
    override fun stopProgressBar() = events.progressInvisible()
    override fun getMessage(): LiveData<String> = snackMessage


}
