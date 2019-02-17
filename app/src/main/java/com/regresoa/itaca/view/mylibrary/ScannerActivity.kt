package com.regresoa.itaca.view.mylibrary

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView

/**
 * Created by just_ on 09/02/2019.
 */
class ScannerActivity : AppCompatActivity(), ZXingScannerView.ResultHandler{

    companion object {
        val ISBN = "ISBN"
    }

    lateinit var mScannerView: ZXingScannerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mScannerView = ZXingScannerView(this)
        setContentView(mScannerView)
    }

    override fun onResume() {
        super.onResume()
        mScannerView.setResultHandler(this)
        mScannerView.startCamera()
    }

    override fun onPause() {
        super.onPause()
        mScannerView.stopCamera()
    }

    override fun handleResult(result: Result?) {
        Log.d("handleResult", result?.text)
        Log.d("handleResult",result?.barcodeFormat.toString())
        val bundle = Bundle().apply{
            putString(ISBN, result?.text)
        }
        val intent = Intent()
        intent.putExtras(bundle)
        setResult(Activity.RESULT_OK, intent)
        finish()
//        mScannerView.resumeCameraPreview(this)
    }
}