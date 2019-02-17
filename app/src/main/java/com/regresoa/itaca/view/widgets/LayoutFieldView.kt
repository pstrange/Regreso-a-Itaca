package com.regresoa.itaca.view.widgets

import android.content.Context
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.regresoa.itaca.R
import kotlinx.android.synthetic.main.layout_item_extrainfo.view.*

/**
 * Created by just_ on 10/02/2019.
 */
class LayoutFieldView : LinearLayout{

    constructor(context: Context, fieldName: String) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.layout_item_extrainfo, this, true)
        text_field_name.text = fieldName+":"
    }

    constructor(context: Context, fieldName: String, fieldValue: String) : super(context) {
        LayoutInflater.from(context).inflate(R.layout.layout_item_extrainfo, this, true)
        text_field_name.text = fieldName+":"
        text_field_content.text = fieldValue
    }

    fun setValue(value: String){
        text_field_content.text = value
    }
}