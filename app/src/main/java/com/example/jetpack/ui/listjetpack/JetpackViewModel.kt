package com.example.jetpack.ui.listjetpack

import android.app.Application
import android.widget.Toast
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import com.example.jetpack.data.ApiService
import com.example.jetpack.data.JetpackDatabase
import com.example.jetpack.data.JetpackModel
import com.example.jetpack.util.NotificationsHelper
import com.example.jetpack.util.SharedPreferencesHelper
import com.example.jetpack.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch

class JetpackViewModel @ViewModelInject constructor (application: Application) : BaseViewModel(application) {
    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L

    private val jetpackApiService = ApiService()
    private val disposable = CompositeDisposable()

    val jetpack = MutableLiveData<List<JetpackModel>>()
    val jetpackLoadError = MutableLiveData<Boolean>()
    val loading = MutableLiveData<Boolean>()

    override fun onCleared() {
        super.onCleared()
        disposable.clear()
    }

    fun refresh() {
        checkCacheDuration()
        val updateTime = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime) {
            fetchFromDatabase()
        } else {
            fetchFromRemote()
        }
    }

    private fun checkCacheDuration() {
        val cachePreference = prefHelper.getCacheDuration()

        try {
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime = cachePreferenceInt.times(1000 * 1000 * 1000L)
        } catch (e: NumberFormatException) {
            e.printStackTrace()
        }
    }

    fun refreshByPassCache() {
        fetchFromRemote()
    }

    private fun fetchFromDatabase() {
        loading.value = true
        launch {
            val jetpack = JetpackDatabase(getApplication()).jetpackDao().getAllData()
            jetpackRetrieved(jetpack)
            Toast.makeText(getApplication(), "Data retrieved from database", Toast.LENGTH_SHORT)
                .show()
            NotificationsHelper(getApplication()).createNotification()
        }
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            jetpackApiService.getData()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<JetpackModel>>() {
                    override fun onSuccess(it: List<JetpackModel>) {
                        storeJetpackLocally(it)
                    }

                    override fun onError(e: Throwable) {
                        jetpackLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }
                })
        )
    }

    private fun jetpackRetrieved(jetpackList: List<JetpackModel>) {
        jetpack.value = jetpackList
        jetpackLoadError.value = false
        loading.value = false
    }

    private fun storeJetpackLocally(list: List<JetpackModel>) {
        launch {
            val dao = JetpackDatabase(getApplication()).jetpackDao()
            dao.deleteAllData()

            val result = dao.insertAll(*list.toTypedArray())
            var i = 0
            while (i < list.size) {
                list[i].uuid = result[i].toInt()
                i++
            }
            jetpackRetrieved(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime())
    }
}