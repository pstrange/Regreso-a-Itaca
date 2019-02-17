package com.regresoa.itaca.model.entities

import com.google.firebase.database.Exclude
import com.google.gson.annotations.SerializedName

/**
 * Created by just_ on 27/01/2019.
 */
class VolumeInfo(
        @SerializedName("title") var title: String = "",
        @SerializedName("authors") var authors: List<String>? = null,
        @SerializedName("publisher") var publisher: String = "",
        @SerializedName("publishedDate") var publishedDate: String = "",
        @SerializedName("description") var description: String = "",
        @SerializedName("industryIdentifiers") var industryIdentifiers: List<IndustryIdentifier>? = null,
        @SerializedName("pageCount") var pageCount: Int = 0,
        @SerializedName("categories") var categories: List<String>? = null,
        @SerializedName("imageLinks") var imageLinks: ImageLinks? = null,
        @SerializedName("language") var language: String = ""
){
    @set:Exclude
    var sIdentifiers : String
            get(){
                val builder = StringBuilder()
                industryIdentifiers?.map { it.identifier }?.forEach { builder.append(it+" ") }
                return builder.toString()
            }
            set(value) {
                sIdentifiers = value
            }

    @set:Exclude
    var sAuthors : String
        get(){
            val builder = StringBuilder()
            authors?.forEach { builder.append(it+" ") }
            return builder.toString()
        }
        set(value) {
            sAuthors = value
        }
}