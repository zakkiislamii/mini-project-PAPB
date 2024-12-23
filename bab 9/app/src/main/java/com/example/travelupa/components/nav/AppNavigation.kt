package com.example.travelupa.components.nav

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.travelupa.components.auth.LoginScreen
import com.example.travelupa.components.auth.RegisterScreen
import com.example.travelupa.components.cameraX.GalleryScreen
import com.example.travelupa.components.greet.GreetingScreen
import com.example.travelupa.components.wisata.RekomendasiTempatScreen
import com.example.travelupa.database.dao.ImageDao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

sealed class Screen(val route: String) {
    data object Login : Screen("login")
    data object RekomendasiTempat : Screen("rekomendasiTempat")
    data object Register : Screen("register")
    data object Greeting : Screen("greeting")
}

@Composable
fun AppNavigation(currentUser: FirebaseUser?, firestore: FirebaseFirestore, imageDao: ImageDao) {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = if (currentUser != null) Screen.RekomendasiTempat.route else Screen.Greeting.route
    ) {
        composable(Screen.Greeting.route) {
            GreetingScreen(
                onStart = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Greeting.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.RekomendasiTempat.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RekomendasiTempat.route) {
            RekomendasiTempatScreen(
                firestore = firestore,
                onBackToLogin = {
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate(Screen.Greeting.route) {
                        popUpTo(Screen.RekomendasiTempat.route) {
                            inclusive = true
                        }
                    }
                },
                onGallerySelected = {
                    navController.navigate("gallery")
                }
            )
        }
        composable("gallery") {
            GalleryScreen(imageDao = imageDao, onImageSelected = { _ ->

            }, onBack = {
                navController.popBackStack()
            })
        }
    }
}