package com.mandala.lib.network

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.util.Log
import java.io.IOException
import java.net.InetAddress

/**
 * Created by just_ on 20/01/2019.
 */
class DefaultNetwork: BroadcastReceiver, Network {

    val TAG: String = "DefaultNetwork"
    val TYPE_NONE = -1
    var connectivityManager: ConnectivityManager? = null

    constructor(context: Context?): super(){
        connectivityManager = context?.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    override fun onReceive(context: Context?, intent: Intent?) {}

    @SuppressLint("MissingPermission")
    override fun isConnected(): Boolean {
        val activeNetwork = if (connectivityManager != null)
            connectivityManager?.activeNetworkInfo else null

        return null != activeNetwork && activeNetwork.isConnectedOrConnecting
    }

    @SuppressLint("MissingPermission")
    override fun connectivityType(): Int {
        val activeNetwork = if (connectivityManager != null)
            connectivityManager?.activeNetworkInfo else null

        return activeNetwork?.type ?: TYPE_NONE
    }

    override fun isReachable(host: String, timeout: Int): Boolean {
        if (!isConnected()) return false

        var reachable: Boolean
        try {
            reachable = InetAddress.getByName(host).isReachable(timeout)
        } catch (e: IOException) {
            Log.e(TAG, Log.getStackTraceString(e))
            reachable = false
        }
        return reachable
    }
}