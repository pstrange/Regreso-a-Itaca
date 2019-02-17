package com.mandala.lib.network

import android.content.Context
import android.os.Build
import android.util.Log
import com.ihsanbal.logging.Level
import com.ihsanbal.logging.LoggingInterceptor
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.TlsVersion
import okhttp3.internal.platform.Platform
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.BufferedInputStream
import java.lang.Exception
import java.security.KeyStore
import java.security.cert.CertificateFactory
import java.util.*
import java.util.concurrent.TimeUnit
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.TrustManagerFactory
import javax.net.ssl.X509TrustManager

/**
 * Created by just_ on 20/01/2019.
 */
class RetrofitFactory{

    companion object {

        lateinit var httpClient :OkHttpClient

        fun initHttpClient(httpClient :OkHttpClient){
            this.httpClient = httpClient
        }

        fun retrofit(baseUrl: String): Retrofit{
            if(httpClient == null)
                throw Exception("First call initHttpClient method")

            return Retrofit.Builder()
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient)
                    .baseUrl(baseUrl)
                    .build()
        }

        fun getHttpClient(context: Context, showLog: Boolean) : OkHttpClient.Builder {
            return getHttpClient(context, 35, 35, 35, showLog)
        }

        fun getHttpClient(context: Context, timeout: Long, showLog: Boolean) : OkHttpClient.Builder {
            return getHttpClient(context, timeout, timeout, timeout, showLog)
        }

        fun getHttpClient(context: Context, connTimeout: Long, readTimeout: Long, writeTimeout: Long, showLog: Boolean) : OkHttpClient.Builder {
            val builder: OkHttpClient.Builder = OkHttpClient.Builder()
            builder.connectTimeout(connTimeout, TimeUnit.SECONDS)
            builder.readTimeout(readTimeout, TimeUnit.SECONDS)
            builder.writeTimeout(writeTimeout, TimeUnit.SECONDS)
            builder.addInterceptor(DefaultNetworkRequestInterceptor(DefaultNetwork(context)))
            if(showLog){
                builder.addInterceptor(LoggingInterceptor
                        .Builder()
                            .loggable(showLog)
                            .setLevel(Level.BODY)
                            .log(Platform.INFO)
                            .request("Request")
                            .response("Response")
                        .build())
            }
            return builder
        }

        fun configCertificade(builder: OkHttpClient.Builder, crtProvider: CrtProvider): OkHttpClient.Builder{
            installCert(builder, crtProvider)
            installCertPreLollipop(builder, crtProvider)
            return builder
        }

        private fun installCert(builder: OkHttpClient.Builder, crtProvider: CrtProvider) {
            val sslContext: SSLContext
            val trustManagers: Array<TrustManager>
            try {
                trustManagers = getTrustManagers(crtProvider)
                sslContext = SSLContext.getInstance("TLS")
                sslContext.init(null, trustManagers, null)
            } catch (e: Exception) {
                e.printStackTrace() //TODO replace with real exception handling tailored to your needs
                return
            }

            builder.sslSocketFactory(sslContext.socketFactory, trustManagers[0] as X509TrustManager)
        }

        private fun installCertPreLollipop(builder: OkHttpClient.Builder, crtProvider: CrtProvider){
            if (Build.VERSION.SDK_INT in 16..21) {
                val trustManagers: Array<TrustManager>
                try {
                    trustManagers = getTrustManagers(crtProvider)
                    val sc = SSLContext.getInstance("TLSv1.2")
                    sc.init(null, trustManagers, null)
                    builder.sslSocketFactory(Tls12SocketFactory(sc.socketFactory))
                    val cs = ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                            .tlsVersions(TlsVersion.TLS_1_2)
                            .build()

                    val specs = ArrayList<ConnectionSpec>()
                    specs.add(cs)
                    specs.add(ConnectionSpec.COMPATIBLE_TLS)
                    specs.add(ConnectionSpec.CLEARTEXT)

                    builder.connectionSpecs(specs)
                } catch (exc: Exception) {
                    Log.e("OkHttpTLSCompat", "Error while setting TLS 1.2", exc)
                }
            }
        }

        private fun getTrustManagers(crtProvider: CrtProvider): Array<TrustManager> {
            try {
                val keyStore = KeyStore.getInstance(KeyStore.getDefaultType())
                keyStore.load(null, null)
                val certInputStream = crtProvider.getCrtInputStream()
                val bis = BufferedInputStream(certInputStream)
                val certificateFactory = CertificateFactory.getInstance("X.509")
                while (bis.available() > 0) {
                    val cert = certificateFactory.generateCertificate(bis)
                    keyStore.setCertificateEntry(crtProvider.getEntryName(), cert)
                }
                val trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm())
                trustManagerFactory.init(keyStore)
                return trustManagerFactory.trustManagers
            }catch (ex: Exception){
                throw ex
            }
        }

    }

}