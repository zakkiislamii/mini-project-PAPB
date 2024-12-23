package com.example.travelupa.components.wisata

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.room.Room
import com.example.travelupa.database.appDatabase.AppDatabase
import com.example.travelupa.database.entity.ImageEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

fun uploadImageToFirestore(
    firestore: FirebaseFirestore,
    context: Context,
    imageUri: Uri,
    tempatWisata: TempatWisata,
    onSuccess: (TempatWisata) -> Unit,
    onFailure: (Exception) -> Unit,
) {
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "travelupa-database"
    ).build()
    val imageDao = db.imageDao()
    val localPath = saveImageLocally(context, imageUri)

    CoroutineScope(Dispatchers.IO).launch {
        imageDao.insert(ImageEntity(localPath = localPath))
        val updateTempatWisata = tempatWisata.copy(gambarUriString = localPath)
        firestore.collection("tempat_wisata")
            .add(updateTempatWisata)
            .addOnSuccessListener {
                onSuccess(updateTempatWisata)
            }
            .addOnFailureListener { e ->
                onFailure(e)
            }
    }
}

private fun saveImageLocally(context: Context, uri: Uri): String {
    try {
        val inputStream = context.contentResolver.openInputStream(uri)
        val file = File(context.filesDir, "image_${System.currentTimeMillis()}.jpg")
        inputStream?.use { input ->
            file.outputStream().use { ouput ->
                input.copyTo(ouput)
            }
        }
        Log.d("ImageSave", "Image saved successfully to ${file.absolutePath}")
        return file.absolutePath
    } catch (e: Exception) {
        Log.e("ImageSave", "Error saving image", e)
        throw e
    }
}