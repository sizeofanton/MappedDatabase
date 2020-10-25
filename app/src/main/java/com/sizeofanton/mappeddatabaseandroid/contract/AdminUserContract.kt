package com.sizeofanton.mappeddatabaseandroid.contract

import androidx.lifecycle.LiveData
import com.sizeofanton.mappeddatabaseandroid.data.pojo.UserInfo
import io.reactivex.Completable
import io.reactivex.Observable

interface AdminUserContract {
    interface View {
        fun addUserClicked(username: String, password: String, isAdmin: Boolean)
        fun editUserClicked(id: Int, password: String, isAdmin: Boolean, isActive: Boolean)
        fun deleteUserClicked(id: Int)
        fun backToLogin()
        fun showSnack(msg: String)
    }

    interface ViewModel {
        fun addNewUser(user: String, password: String, isAdmin: Boolean)
        fun editUser(id: Int, password: String, isAdmin: Boolean, isActive: Boolean)
        fun deleteUser(id: Int)
        fun getUsersList(): LiveData<List<UserInfo>>
        fun getMessage(): LiveData<String>
        fun getNetworkErrors(): LiveData<Int>
    }

    interface Model {
        fun getUsers(): Observable<List<UserInfo>>
        fun addUser(user: String, password: String, isAdmin: Boolean): Completable
        fun editUser(id: Int, password: String, isAdmin: Boolean, isActive: Boolean): Completable
        fun deleteUser(id: Int): Completable
    }
}
