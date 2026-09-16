package com.tarikturkdil.photoProject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.tarikturkdil.photoProject.ui.navigation.NavGraph
import com.tarikturkdil.photoProject.ui.theme.PhotoProjectTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PhotoProjectTheme(dynamicColor = false) {
                Scaffold(modifier = Modifier.fillMaxSize()) { _ ->
                    NavGraph()
                }
            }
        }
    }
}