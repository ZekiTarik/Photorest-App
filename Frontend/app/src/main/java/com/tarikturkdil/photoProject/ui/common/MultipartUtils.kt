package com.tarikturkdil.photoProject.ui.common

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.io.FileOutputStream

fun Context.uriToMultipartPart(uri: Uri, partName: String): MultipartBody.Part {
    val inputStream = contentResolver.openInputStream(uri)
        ?: throw IllegalStateException("Dosya açılamadı")

    val originalBitmap = BitmapFactory.decodeStream(inputStream)
    inputStream.close()

    // Çok büyük görselleri makul bir çözünürlüğe küçült (en uzun kenar max 1600px)
    val scaledBitmap = scaleBitmapIfNeeded(originalBitmap, maxDimension = 1600)

    val tempFile = File.createTempFile("upload_", ".jpg", cacheDir)
    FileOutputStream(tempFile).use { output ->
        scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 80, output)
    }

    val requestBody = tempFile.asRequestBody("image/jpeg".toMediaType())
    return MultipartBody.Part.createFormData(partName, tempFile.name, requestBody)
}

private fun scaleBitmapIfNeeded(bitmap: Bitmap, maxDimension: Int): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    if (width <= maxDimension && height <= maxDimension) {
        return bitmap
    }

    val scaleFactor = maxDimension.toFloat() / maxOf(width, height)
    val newWidth = (width * scaleFactor).toInt()
    val newHeight = (height * scaleFactor).toInt()

    return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
}