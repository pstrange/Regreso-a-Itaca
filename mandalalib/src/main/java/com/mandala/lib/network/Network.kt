package com.mandala.lib.network

/**
 * Created by just_ on 20/01/2019.
 */
interface Network {
    fun isConnected(): Boolean
    fun connectivityType(): Int
    fun isReachable(host: String, timeout: Int): Boolean
}