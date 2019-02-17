package com.regresoa.itaca.model.entities

import com.google.gson.annotations.SerializedName

/**
 * Created by just_ on 27/01/2019.
 */
class Book(@SerializedName("id") val id: String = "",
           @SerializedName("etag") val etag: String = "",
           @SerializedName("creationDate") var creationDate: Long = 0L,
           @SerializedName("localInfo") var localInfo: LocalInfo? = null,
           @SerializedName("volumeInfo") var volumeInfo: VolumeInfo? = null)