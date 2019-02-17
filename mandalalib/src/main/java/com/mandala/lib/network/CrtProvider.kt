package com.mandala.lib.network

import java.io.InputStream

/**
 * Created by just_ on 20/01/2019.
 */
interface CrtProvider {

    fun getCrtInputStream(): InputStream
    fun getEntryName(): String

}