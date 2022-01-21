package com.a1573595.parkinglotdemo

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.disposables.Disposable

abstract class BaseViewModel(application: Application) : AndroidViewModel(application) {
    private val disposable: CompositeDisposable = CompositeDisposable()

    internal fun addDisposable(d: Disposable) {
        disposable.add(d)
    }

    override fun onCleared() {
        disposable.clear()
        super.onCleared()
    }
}