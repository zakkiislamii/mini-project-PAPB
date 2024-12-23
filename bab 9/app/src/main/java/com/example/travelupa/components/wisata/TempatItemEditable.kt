package com.example.travelupa.components.wisata

import android.net.Uri
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.room.Room
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.travelupa.R
import com.example.travelupa.database.appDatabase.AppDatabase
import com.example.travelupa.database.entity.ImageEntity
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun TempatItemEditable(
    tempat: TempatWisata,
    onDelete: () -> Unit,
) {
    val firestore = FirebaseFirestore.getInstance()
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val db = Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "travelupa-database"
    ).build()
    val imageDao = db.imageDao()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                tempat.gambarUriString?.let { uriString ->
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(Uri.parse(uriString))
                            .crossfade(true)
                            .build(),
                        contentDescription = tempat.nama,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        error = painterResource(id = R.drawable.default_image)
                    )
                } ?: Image(
                    painter = tempat.gambarResId?.let {
                        painterResource(id = it)
                    } ?: painterResource(id = R.drawable.default_image),
                    contentDescription = tempat.nama,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
            }

            Box(modifier = Modifier.fillMaxWidth()) {
                if (tempat.nama == null && tempat.deskripsi == null &&
                    (tempat.gambarUriString != null || tempat.gambarResId != null)
                ) {
                    Text(
                        text = "Tempat wisata kosong",
                        style = MaterialTheme.typography.h6,
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(vertical = 16.dp)
                    )
                } else {
                    Column(
                        modifier = Modifier.align(Alignment.CenterStart)
                    ) {
                        Text(
                            text = tempat.nama,
                            style = MaterialTheme.typography.h6,
                            modifier = Modifier.padding(bottom = 8.dp, top = 12.dp)
                        )
                        Text(
                            text = tempat.deskripsi,
                            style = MaterialTheme.typography.body2,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }

                Box(modifier = Modifier.align(Alignment.TopEnd)) {
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        offset = DpOffset(x = (-8).dp, y = 0.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Delete", color = Color.Black) },
                            onClick = {
                                expanded = false
                                scope.launch(Dispatchers.IO) {
                                    try {
                                        imageDao.delete(
                                            ImageEntity(
                                                localPath = tempat.gambarUriString ?: ""
                                            )
                                        )
                                        withContext(Dispatchers.Main) {
                                            firestore.collection("tempat_wisata")
                                                .document(tempat.nama)
                                                .delete()
                                                .addOnSuccessListener {
                                                    onDelete()
                                                }
                                                .addOnFailureListener { e ->
                                                    Log.w(
                                                        "TempatItemEditable",
                                                        "Error deleting document",
                                                        e
                                                    )
                                                }
                                        }

                                    } catch (e: Exception) {
                                        Log.e("TempatItemEditable", "Error deleting data", e)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}