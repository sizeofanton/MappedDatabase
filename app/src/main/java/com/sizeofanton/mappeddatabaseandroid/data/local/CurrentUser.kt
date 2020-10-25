package com.sizeofanton.mappeddatabaseandroid.data.local

class CurrentUser {

    companion object {

        private var user: String? = null
        private var token: String? = null
        private var isAdmin = false

        fun set(user: String, token: String, isAdmin: Boolean) {
            Companion.user = user
            Companion.token = token
            Companion.isAdmin = isAdmin
        }

        fun clear() {
            user = null
            token = null
            isAdmin = false
        }

        fun getUser(): String = user!!
        fun getToken(): String = token!!
        fun getTokenNullable(): String? = token
        fun checkIsAdmin(): Boolean = isAdmin
    }
}
