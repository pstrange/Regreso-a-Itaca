package com.regresoa.itaca.model.entities

import com.google.gson.annotations.SerializedName

/**
 * Created by just_ on 27/01/2019.
 */
class IndustryIdentifier(
        @SerializedName("type") var type: String = "",
        @SerializedName("identifier") var identifier: String = ""
)