package com.huongmt.medmeet.ui.auth

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.huongmt.medmeet.R
import com.huongmt.medmeet.component.BaseInputText
import com.huongmt.medmeet.component.PasswordField
import com.huongmt.medmeet.component.PrimaryButton
import com.huongmt.medmeet.shared.app.AuthAction
import com.huongmt.medmeet.shared.app.AuthStore
import com.huongmt.medmeet.shared.utils.validate.Validator
import com.huongmt.medmeet.theme.Grey_900
import io.github.aakira.napier.Napier

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background), store: AuthStore,
) {
    LoginScreenContent(modifier, store)
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LoginScreenContent(modifier: Modifier = Modifier.background(MaterialTheme.colorScheme.background), store: AuthStore) {
    val email = remember { mutableStateOf("lan@gmail.com") }
    val password = remember { mutableStateOf("123456") }

    val state by store.observeState().collectAsState()

    LaunchedEffect(Unit) {
        if (state.rememberEmail != null) {
            email.value = state.rememberEmail!!
        }
    }

    Scaffold { paddingValues ->
        Column(modifier = modifier.padding(32.dp)) {
            Spacer(Modifier.height(32.dp))

            LogoHeader()

            Spacer(Modifier.height(32.dp))

            Text("Chào bạn!", style = MaterialTheme.typography.titleMedium, modifier = Modifier.align(
                Alignment.CenterHorizontally))

            Text(
                "Ngày mới tốt lành!",
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Normal),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(Modifier.weight(1f))

            Column {
                BaseInputText(modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Email),
                    default = email.value,
                    hint = "email",
                    onTextChanged = {
                        email.value = it
                    },
                    description = "Email",
                    onImeAction = {
                        email.value = it
                        Napier.d("email: $it")
                    },
                    validator = {
                        Validator.validateEmail(it)
                    },
                    leadingIcon = {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_email), contentDescription = null)
                    })

                Spacer(Modifier.height(16.dp))

                PasswordField(modifier = Modifier.fillMaxWidth().fillMaxWidth(),
                    hint = "password",
                    default = password.value,
                    onImeAction = {
                        password.value = it
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Go, keyboardType = KeyboardType.Password),
                    shape = RoundedCornerShape(10.dp),
                    onTextChanged = { it ->
                        password.value = it
                    },
                    validator = {
                        Validator.validatePassword(it)
                    },
                    leadingIcon = {
                        Icon(imageVector = ImageVector.vectorResource(R.drawable.ic_lock), contentDescription = null)
                    })

                Spacer(Modifier.height(16.dp))


                PrimaryButton(modifier = Modifier.fillMaxWidth().height(52.dp).padding(bottom = 8.dp), onClick = {
                    // do login
                    store.sendAction(AuthAction.RequestLogin(email.value, password.value))
                }, text = {
                    Text(text = "Đăng nhập", color = Color.White)
                })

                Spacer(Modifier.height(24.dp))

                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    HorizontalDivider(modifier = Modifier.fillMaxWidth().height(1.dp))
                    Box(modifier = Modifier.wrapContentWidth().background(MaterialTheme.colorScheme.background).padding(start = 8.dp, end = 8.dp)) {
                        Text(text = "hoặc", color = Color.Black, modifier = Modifier.padding(start = 8.dp, end = 8.dp))
                    }
                }

                Spacer(Modifier.height(24.dp))


                Row(modifier = Modifier.fillMaxWidth().wrapContentHeight(), horizontalArrangement = Arrangement.Center) {
                    Text("Chưa có tài khoản? ", style = MaterialTheme.typography.bodyMedium)
                    Text("Đăng ký", style = MaterialTheme.typography.bodyMedium, color = Color.Blue, modifier = Modifier.clickable {
                        store.sendAction(AuthAction.DisplaySignUp)
                    })
                }
            }

            Spacer(Modifier.weight(1f))
        }
    }
}

@Composable
fun ColumnScope.LogoHeader() {
    Image(
        imageVector = ImageVector.vectorResource(R.drawable.ic_logo), contentDescription = null, modifier = Modifier.size(96.dp).align(
            Alignment.CenterHorizontally).padding(
            top = 32.dp
        )
    )
    Spacer(Modifier.height(16.dp))
    Text(text = "MedMeet", style = MaterialTheme.typography.titleLarge, color = Grey_900, modifier = Modifier.align(
        Alignment.CenterHorizontally))
}
