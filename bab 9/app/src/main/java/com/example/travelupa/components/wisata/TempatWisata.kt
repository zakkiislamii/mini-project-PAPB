package com.example.travelupa.components.wisata

data class TempatWisata(
    val nama: String = "",
    val deskripsi: String = "",
    val gambarUriString: String? = null,
    val gambarResId: Int? = null,
    val firestoreId: String? = null
)
