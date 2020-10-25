package com.sizeofanton.mappeddatabaseandroid.ui.create_account

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.sizeofanton.mappeddatabaseandroid.contract.CreateAccountContract
import com.sizeofanton.mappeddatabaseandroid.data.model.CreateAccountModel


class CreateAccountViewModel : ViewModel(), CreateAccountContract.ViewMode {

    private val model = CreateAccountModel()
    private val message = MutableLiveData<String>()

    @SuppressLint("CheckResult")
    override fun createAccount(username: String, password: String) {
        model.createAccount(username, password).subscribe(
            { message.postValue("Account created successfully") },
            { message.postValue("Account creation error: ${it.message}") })
    }

    fun getMessage(): LiveData<String> = message
}