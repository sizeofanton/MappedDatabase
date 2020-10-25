package com.sizeofanton.mappeddatabaseandroid.contract

import io.reactivex.Completable

interface CreateAccountContract {

    interface View {
        fun createAccountClick()
    }
    interface ViewMode {
        fun createAccount(username: String, password: String)
    }
    interface Model {
        fun createAccount(username: String, password: String): Completable
    }

}