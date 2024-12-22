package com.example.travelupa.components.wisata

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun TambahTempatWisataDialog(
    firestore: FirebaseFirestore,
    onDismiss: () -> Unit,
    onTambah: (String, String, Uri?) -> Unit,
) {
    var nama by remember { mutableStateOf("") }
    var deskripsi by remember { mutableStateOf("") }
    var gambarUri by remember { mutableStateOf<Uri?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val gambarLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            gambarUri = uri
        }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Tambah Tempat Wisata Baru",
                fontSize = 20.sp
            )
        },
        text = {
            Column {
                TextField(
                    value = nama,
                    onValueChange = { nama = it },
                    label = { Text(text = "Nama Tempat") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = deskripsi,
                    onValueChange = { deskripsi = it },
                    label = { Text(text = "Deskripsi") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                )
                Spacer(modifier = Modifier.height(8.dp))
                gambarUri?.let { uri ->
                    Image(
                        painter = rememberAsyncImagePainter(model = uri),
                        contentDescription = "Gambar yang dipilih",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Button(
                    onClick = { gambarLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isUploading
                ) {
                    Text(text = "Pilih Gambar")
                }
            }
        },
        confirmButton = {
            androidx.compose.material3.Button(
                onClick = {
                    if (nama.isNotBlank() && deskripsi.isNotBlank() && gambarUri != null) {
                        isUploading = true
                        val tempatWisata = TempatWisata(nama, deskripsi)
                        UploadImageToFirestore(
                            firestore = firestore,
                            context = context,
                            imageUri = gambarUri!!,
                            tempatWisata = tempatWisata,
                            onSuccess = { uploadedTempat ->
                                isUploading = false
                                onTambah(nama, deskripsi, Uri.parse(uploadedTempat.gambarUriString))
                                onDismiss()
                            },
                            onFailure = { _ ->
                                isUploading = false
                            }
                        )
                    }
                },
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color = Color.White
                    )
                } else {
                    Text(text = "Tambah")
                }
            }
        },
        dismissButton = {
            androidx.compose.material3.Button(
                onClick = onDismiss,
                enabled = !isUploading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                )
            ) {
                Text(text = "Batal")
            }
        }
    )
}