package com.regresoa.itaca.model.entities

import com.google.gson.annotations.SerializedName

/**
 * Created by just_ on 27/01/2019.
 */
class ImageLinks(
        @SerializedName("smallThumbnail") var smallThumbnail: String = "",
        @SerializedName("thumbnail") var thumbnail: String = ""
)