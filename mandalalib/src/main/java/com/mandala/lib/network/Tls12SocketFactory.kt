package com.mandala.lib.network

import java.lang.Exception
import java.net.InetAddress
import java.net.Socket
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

/**
 * Created by just_ on 20/01/2019.
 */
class Tls12SocketFactory: SSLSocketFactory{

    private val TLS_V12_ONLY = arrayOf("TLSv1.2")
    var delegate: SSLSocketFactory

    constructor(socket: SSLSocketFactory) : super(){
        this.delegate = socket
    }

    override fun getDefaultCipherSuites(): Array<String> {
        return delegate.defaultCipherSuites
    }

    override fun getSupportedCipherSuites(): Array<String> {
        return delegate.supportedCipherSuites
    }

    override fun createSocket(s: Socket, host: String, port: Int, autoClose: Boolean): Socket? {
        try {
            return patch(delegate.createSocket(s, host, port, autoClose))
        }catch (ex: Exception){
            throw ex
        }
    }

    override fun createSocket(host: String, port: Int): Socket? {
        try {
            return patch(delegate.createSocket(host, port))
        }catch (ex: Exception){
            throw ex
        }
    }

    override fun createSocket(host: String, port: Int, localHost: InetAddress, localPort: Int): Socket? {
        try {
            return patch(delegate.createSocket(host, port, localHost, localPort))
        }catch (ex: Exception){
            throw ex
        }
    }

    override fun createSocket(host: InetAddress, port: Int): Socket? {
        try {
            return patch(delegate.createSocket(host, port))
        }catch (ex: Exception){
            throw ex
        }
    }

    override fun createSocket(address: InetAddress, port: Int, localAddress: InetAddress, localPort: Int): Socket? {
        try {
            return patch(delegate.createSocket(address, port, localAddress, localPort))
        }catch (ex: Exception){
            throw ex
        }
    }

    private fun patch(s: Socket): Socket {
        if (s is SSLSocket) {
            s.enabledProtocols = TLS_V12_ONLY
        }
        return s
    }
}