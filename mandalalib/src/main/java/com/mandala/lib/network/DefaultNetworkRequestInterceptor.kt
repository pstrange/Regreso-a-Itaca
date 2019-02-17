package com.mandala.lib.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * Created by just_ on 20/01/2019.
 */
class DefaultNetworkRequestInterceptor: NetworkRequestInterceptor {

    constructor(network: Network) : super(network)

    override fun interceptResponse(chain: Interceptor.Chain?): Response {
        return chain!!.proceed(chain.request())
    }

}