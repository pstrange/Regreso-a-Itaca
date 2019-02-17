package com.regresoa.itaca.model.entities

import com.google.gson.annotations.SerializedName

/**
 * Created by just_ on 27/01/2019.
 */
class SearchResult(
        @SerializedName("kind") val kind: String,
        @SerializedName("totalItems") val totalItems: Int,
        @SerializedName("items") val items: Array<Book>
)