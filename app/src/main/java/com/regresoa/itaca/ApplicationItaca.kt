package com.regresoa.itaca

import android.arch.lifecycle.Lifecycle
import android.arch.lifecycle.LifecycleObserver
import android.arch.lifecycle.OnLifecycleEvent
import android.support.multidex.MultiDexApplication
import com.mandala.lib.network.RetrofitFactory
import com.regresoa.itaca.utils.AppPreferences

/**
 * Created by just_ on 26/01/2019.
 */
class ApplicationItaca: MultiDexApplication(), LifecycleObserver {

    override fun onCreate() {
        super.onCreate()

        AppPreferences.init(this)

        RetrofitFactory.initHttpClient(
                RetrofitFactory
                    .getHttpClient(this, true)
                    .build())

//        RetrofitFactory.retrofit("http://apiurl.com").create(HomeActivity::class.java)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy(){

    }
}