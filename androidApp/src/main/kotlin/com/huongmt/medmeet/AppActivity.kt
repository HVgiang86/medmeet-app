package com.huongmt.medmeet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import cafe.adriel.voyager.navigator.Navigator
import com.huongmt.medmeet.rootview.RootView
import io.github.aakira.napier.Napier

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Napier.d(tag = "AppActivity", message = "onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            Navigator(RootView())
        }
    }
}
