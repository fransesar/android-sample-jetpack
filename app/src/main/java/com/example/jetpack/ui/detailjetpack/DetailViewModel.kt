package com.example.jetpack.ui.detailjetpack

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.example.jetpack.data.JetpackDatabase
import com.example.jetpack.data.JetpackModel
import com.example.jetpack.BaseViewModel
import kotlinx.coroutines.launch

class DetailViewModel(application: Application): BaseViewModel(application) {
    val jetpackLiveData = MutableLiveData<JetpackModel>()

    fun fetch(uuid: Int){
        launch {
            val jetpack = JetpackDatabase(getApplication()).jetpackDao().getData(uuid)
            jetpackLiveData.value = jetpack
        }
    }
}