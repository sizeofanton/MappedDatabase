package com.sizeofanton.mappeddatabaseandroid.ui.login

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.google.android.material.snackbar.Snackbar
import com.sizeofanton.mappeddatabaseandroid.R
import com.sizeofanton.mappeddatabaseandroid.contract.LoginContract
import com.sizeofanton.mappeddatabaseandroid.data.local.EncryptedPreferences
import com.sizeofanton.mappeddatabaseandroid.ui.MainActivity
import com.sizeofanton.mappeddatabaseandroid.util.ext.hideKeyboard
import com.squareup.picasso.Picasso
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.login_fragment.*
import org.koin.android.viewmodel.ext.android.viewModel

class LoginFragment : Fragment(), LoginContract.View {

    companion object {
        fun newInstance() =
            LoginFragment()
    }

    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.login_fragment, container, false)
    }

    override fun onStart() {
        super.onStart()
        Picasso.with(context).load(R.drawable.logo).into(ivLogo)
        (activity as MainActivity).title = getString(R.string.log_in)
        initEvents()
        viewModel.stopProgressBar()

        btnLogin.setOnClickListener {
            onLoginClick()
        }

        tvNewAccount.setOnClickListener {
            onCreateAccountClick()
        }

        val savedLogin = EncryptedPreferences
            .getInstance(activity as Context)
            .getString("LOGIN")
        val savedPassword = EncryptedPreferences
            .getInstance(activity as Context)
            .getString("PASSWORD")

        if (savedLogin != "" && savedPassword != "") {
            etUserName.setText(savedLogin)
            etPassword.setText(savedPassword)
            viewModel.handleLogin(savedLogin, savedPassword)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        lifecycle.addObserver(viewModel)
        viewModel.getMessage().observe(viewLifecycleOwner, Observer {
            showSnack(it)
        })
    }


    override fun onLoginClick() {
        hideKeyboard()
        if (cbRemember.isChecked) {
            EncryptedPreferences.getInstance(activity as Context).apply {
                    putString("LOGIN", etUserName.text.toString())
                    putString("PASSWORD", etPassword.text.toString())
            }
        }
        viewModel.handleLogin(etUserName.text.toString(), etPassword.text.toString())
    }

    override fun onCreateAccountClick() {
        (activity as MainActivity).launchNewAccountFragment()
    }

    override fun showSnack(msg: String) = Snackbar.make(requireView(), msg, Snackbar.LENGTH_LONG).show()
    override fun launchMapFragment() = (activity as MainActivity).launchMapFragment()


    @SuppressLint("CheckResult")
    private fun initEvents() {
        viewModel.eventBus
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(object: io.reactivex.Observer<LoginViewModel.Event> {
            override fun onNext(event: LoginViewModel.Event) {
                when(event) {
                    LoginViewModel.Event.LoginSuccessful -> {
                        launchMapFragment()
                    }

                    LoginViewModel.Event.ProgressVisible -> {
                        pbLogin.visibility = View.VISIBLE
                    }

                    LoginViewModel.Event.ProgressInvisible -> {
                        pbLogin.visibility = View.INVISIBLE
                    }
                }
            }

            override fun onComplete() {}
            override fun onSubscribe(d: Disposable) {
                Log.d("Subscription", "Subscription")
            }
            override fun onError(e: Throwable) { showSnack(e.message.toString()) }
        })
    }
}
