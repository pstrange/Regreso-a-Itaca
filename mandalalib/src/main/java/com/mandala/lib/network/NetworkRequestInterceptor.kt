package com.mandala.lib.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

/**
 * Created by just_ on 20/01/2019.
 */
abstract class NetworkRequestInterceptor: Interceptor{

    private var network: Network

    constructor(network: Network){
        this.network = network
    }

    override fun intercept(chain: Interceptor.Chain?): Response {
        return if (null != network && network.isConnected()) {
            return interceptResponse(chain)
        } else {
            throw NetworkException()
        }
    }

    class NetworkException: IOException {
        constructor() : super("Parece que su conexión a Internet está desactivada." + "\n\nPor favor, enciéndala y vuelva a intentarlo.")
    }

    internal abstract fun interceptResponse(chain: Interceptor.Chain?): Response
}