package com.regresoa.itaca.view.books

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.gson.Gson
import com.regresoa.itaca.R
import com.regresoa.itaca.model.entities.LocalInfo
import kotlinx.android.synthetic.main.dialog_local_info.*

/**
 * Created by just_ on 11/02/2019.
 */
class LocalInfoDialog : DialogFragment(){

    companion object {

        val JSON_INFO = "jsonInfo"

        fun newInstance() : LocalInfoDialog{
            return newInstance(LocalInfo())
        }

        fun newInstance(info: LocalInfo) : LocalInfoDialog{
            val bundle = Bundle().apply {
                putString(JSON_INFO, Gson().toJson(info))
            }
            val fragment = LocalInfoDialog()
            fragment.arguments = bundle
            return fragment
        }

    }

    private lateinit var localInfo: LocalInfo
    lateinit var listener: OnLocalInfoEdit

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_local_info, null)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        localInfo = Gson().fromJson(arguments!!.getString(JSON_INFO), LocalInfo::class.java)

        text_stock.text = localInfo.stock.toString()
        button_less.setOnClickListener {
            if(localInfo.stock > 1){
                localInfo.stock -= 1
                text_stock.text = localInfo.stock.toString()
            }
        }
        button_more.setOnClickListener {
            localInfo.stock += 1
            text_stock.text = localInfo.stock.toString()
        }
        edit_price.setText(localInfo.price)
        edit_notes.setText(localInfo.note)

        button_save.setOnClickListener {
            listener?.let {
                dismiss()
                localInfo.price = edit_price.text.toString()
                localInfo.note = edit_notes.text.toString()
                listener.onLocalInfoSave(localInfo)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
    }

    interface OnLocalInfoEdit{
        fun onLocalInfoSave(localInfo: LocalInfo)
    }
}