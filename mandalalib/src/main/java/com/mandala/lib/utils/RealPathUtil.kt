package com.mandala.lib.utils

import android.content.Context
import android.net.Uri
import android.os.Build
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.support.v4.content.CursorLoader

/**
 * Created by just_ on 23/02/2019.
 */
class RealPathUtil {

    companion object {

        fun getRealPathFromURI(context: Context, uri: Uri): String?{
            if(uri.toString().isEmpty())
                return ""

            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
                getRealPathFromURI_API19(context, uri)
            }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN_MR2){
                getRealPAthFromURI_API11to18(context, uri)
            }else
                getRealPathFromURI_BelowAPI11(context, uri)
        }

        /**
         * Extracts the absolute file path from a [Uri] string value
         *
         * @param context the [Context] value
         * @param uri the [Uri] value
         * @return the path of the picture taken
         */
        fun getRealPathFromURI_API19(context: Context, uri: Uri): String?{
            var filePath = ""
            val wholeID = DocumentsContract.getDocumentId(uri)
            // Split at colon, use second item in the array
            val id = wholeID.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            val column = arrayOf(MediaStore.Images.Media.DATA)
            // where id is equal to
            val sel = MediaStore.Images.Media._ID + "=?"
            val cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    column, sel, arrayOf(id), null)

            val columnIndex = cursor.getColumnIndex(column[0])
            if (cursor.moveToFirst()) {
                filePath = cursor.getString(columnIndex)
            }
            cursor.close()
            return filePath
        }

        /**
         * Extracts the absolute file path from a [Uri] string value
         *
         * @param context the [Context] value
         * @param uri the [Uri] value
         * @return the path of the picture taken
         */
        fun getRealPAthFromURI_API11to18(context: Context, uri: Uri): String?{
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            var result: String? = null
            val cursorLoader = CursorLoader(
                    context,
                    uri, proj, null, null, null)
            val cursor = cursorLoader.loadInBackground()

            if (cursor != null) {
                val columnIndex = cursor!!.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                cursor!!.moveToFirst()
                result = cursor!!.getString(columnIndex)
            }
            return result
        }

        /**
         * Extracts the absolute file path from a [Uri] string value
         *
         * @param context the [Context] value
         * @param uri the [Uri] value
         * @return the path of the picture taken
         */
        fun getRealPathFromURI_BelowAPI11(context: Context, uri: Uri): String?{
            val proj = arrayOf(MediaStore.Images.Media.DATA)
            val cursor = context.contentResolver.query(uri, proj, null, null, null)
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            cursor.moveToFirst()
            return cursor.getString(columnIndex)
        }

    }

}