package com.juanpablo0612.carpool.presentation.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.dont_have_account
import enrutadoseia.composeapp.generated.resources.login_button
import enrutadoseia.composeapp.generated.resources.login_title
import com.juanpablo0612.carpool.presentation.state.AuthAction
import com.juanpablo0612.carpool.presentation.ui.components.EmailTextField
import com.juanpablo0612.carpool.presentation.ui.components.ErrorMessage
import com.juanpablo0612.carpool.presentation.ui.components.PasswordTextField
import com.juanpablo0612.carpool.presentation.ui.components.PrimaryButton
import com.juanpablo0612.carpool.presentation.ui.error.asStringResource
import com.juanpablo0612.carpool.presentation.viewmodel.AuthViewModel
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = koinViewModel(),
    onNavigateToRegister: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Scaffold { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(Res.string.login_title),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(32.dp))

                EmailTextField(
                    value = email,
                    onValueChange = { email = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                PasswordTextField(
                    value = password,
                    onValueChange = { password = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                state.error?.let {
                    ErrorMessage(message = stringResource(it.asStringResource()))
                }

                Spacer(modifier = Modifier.height(24.dp))

                PrimaryButton(
                    text = stringResource(Res.string.login_button),
                    onClick = { viewModel.onAction(AuthAction.Login(email, password)) },
                    enabled = !state.isLoading
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(Res.string.dont_have_account),
                    modifier = Modifier.clickable { onNavigateToRegister() },
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            if (state.isLoading) {
                CircularProgressIndicator()
            }
        }
    }
}
