package com.gianghv.kmachat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.rememberScaffoldState
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.navigator.Navigator
import com.gianghv.kmachat.composeui.HomeScreen
import com.gianghv.kmachat.theme.AppTheme
import io.github.aakira.napier.Napier

class AppActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        Napier.d(tag = "AppActivity", message = "onCreate")
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                val scaffoldState = rememberScaffoldState()
                Box(
                    Modifier.padding(
                        WindowInsets.systemBars
                            .only(WindowInsetsSides.Start + WindowInsetsSides.End)
                            .asPaddingValues(),
                    ),
                ) {
                    Navigator(HomeScreen())
                }
            }
        }
    }
}
