package com.example.travelupa

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.room.Room
import com.example.travelupa.components.nav.AppNavigation
import com.example.travelupa.database.appDatabase.AppDatabase
import com.example.travelupa.database.dao.ImageDao
import com.example.travelupa.ui.theme.TravelupaTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : ComponentActivity() {
    private lateinit var firestore: FirebaseFirestore
    private lateinit var imageDao: ImageDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        firestore = FirebaseFirestore.getInstance()
        val db =
            Room.databaseBuilder(applicationContext, AppDatabase::class.java, "travelupa-database")
                .build()
        imageDao = db.imageDao()
        val currentUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        enableEdgeToEdge()
        setContent {
            TravelupaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White
                ) {
                    AppNavigation(
                        currentUser = currentUser,
                        firestore = firestore,
                        imageDao = imageDao
                    )
                }
            }
        }
    }
}
