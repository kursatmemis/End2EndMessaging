package com.kursatmemis.end2endmessaging.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.util.Log
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.snackbar.Snackbar
import com.kursatmemis.end2endmessaging.util.userfeedback.SnackBarAction
import com.kursatmemis.end2endmessaging.util.userfeedback.showSnackBar

class ImagePickerFromGallery(
    private val context: Context,
    private val activity: Activity,
    private val view: View,
    private val activityResultLauncher: ActivityResultLauncher<Intent>,
    private val permissionLauncher: ActivityResultLauncher<String>
) {

    // SDK Version numarası 33'den küçük olan cihazlar için bu method kullanılarak resim seçtirilir.
    fun selectImageForPreSDK33() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("mKm - izin", "burda")
            // Izin verilmemiş.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                // Izin isterken kullanıcıya bir mantık göstermeliyiz. (İznin neden istendiğini açıkla)
                showSnackBar(
                    view,
                    "You have to give a permission to select an image from your gallery.",
                    Snackbar.LENGTH_INDEFINITE,
                    SnackBarAction("Give Permission") {
                        permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                )
            } else {
                // Izin isterken kullanıcıya bir mantık göstermemize gerek yok.

                permissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            // Izin verilmiş.
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

    // SDK Version numarası 33'den büyük olan cihazlar için bu method kullanılarak resim seçtirilir.
    fun selectImageForSDK33AndAbove() {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.w("mKm - izin", "burda2")
            // Izin verilmemiş.
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    activity,
                    Manifest.permission.READ_MEDIA_IMAGES
                )
            ) {
                // Izin istersen kullanıcıya bir mantık göstermeliyiz. (İznin neden istendiğini açıkla)
                Snackbar.make(
                    view,
                    "You have to give a permission to select an image from your gallery.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Give Permission", View.OnClickListener {
                    permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }).show()
            } else {
                // Izin istersen kullanıcıya bir mantık göstermemize gerek yok.
                permissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else {
            // Izin verilmiş.
            val intentToGallery =
                Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            activityResultLauncher.launch(intentToGallery)
        }
    }

}