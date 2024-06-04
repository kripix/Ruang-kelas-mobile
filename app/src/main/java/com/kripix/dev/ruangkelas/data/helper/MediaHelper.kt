package com.kripix.dev.ruangkelas.data.helper

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import android.util.Base64
import android.widget.ImageView
import java.io.ByteArrayOutputStream

class MediaHelper(private val context: Context) {

    fun getRcGallery(): Int {
        return REQ_CODE_GALLERY
    }

    fun bitmapToString(bmp: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun getBitmapToString(uri: Uri?, imgV: ImageView): String {
        var bmp = MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
        val dim = 720
        if (bmp.height > bmp.width) {
            bmp = Bitmap.createScaledBitmap(bmp, (bmp.width * dim).div(bmp.height), dim, true)
        } else {
            bmp = Bitmap.createScaledBitmap(bmp, dim, (bmp.height * dim).div(bmp.width), true)
        }
        imgV.setImageBitmap(bmp)
        return bitmapToString(bmp)
    }

    fun getRealPathFromURI(contentUri: Uri): String? {
        val cursor = context.contentResolver.query(contentUri, null, null, null, null)
        return if (cursor == null) {
            contentUri.path
        } else {
            cursor.moveToFirst()
            val idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
            val realPath = cursor.getString(idx)
            cursor.close()
            realPath
        }
    }

    companion object {
        const val REQ_CODE_GALLERY = 100
    }
}
