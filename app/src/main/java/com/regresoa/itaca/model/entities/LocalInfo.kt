package com.regresoa.itaca.model.entities

import com.google.gson.annotations.SerializedName

/**
 * Created by just_ on 10/02/2019.
 */
class LocalInfo(
        @SerializedName("stock") var stock: Int = 1,
        @SerializedName("price") var price: String = "",
        @SerializedName("note") var note: String = ""
)