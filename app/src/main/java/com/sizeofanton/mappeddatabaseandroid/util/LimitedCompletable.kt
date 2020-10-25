package com.sizeofanton.mappeddatabaseandroid.util

import io.reactivex.Completable
import io.reactivex.CompletableObserver

class LimitedCompletable: Completable() {
    private var o: CompletableObserver? = null
    override fun subscribeActual(observer: CompletableObserver?) {
        if (o == null) o = observer
    }
    fun complete() = o?.onComplete()
    fun error(t: Throwable) = o?.onError(t)
}