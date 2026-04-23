package com.juanpablo0612.carpool.presentation.role_selector

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juanpablo0612.carpool.domain.auth.model.User
import com.juanpablo0612.carpool.presentation.ui.theme.CarpoolTheme
import enrutadoseia.composeapp.generated.resources.Res
import enrutadoseia.composeapp.generated.resources.location_on_24px
import enrutadoseia.composeapp.generated.resources.person_24px
import enrutadoseia.composeapp.generated.resources.role_selector_driver_subtitle
import enrutadoseia.composeapp.generated.resources.role_selector_driver_title
import enrutadoseia.composeapp.generated.resources.role_selector_greeting
import enrutadoseia.composeapp.generated.resources.role_selector_passenger_subtitle
import enrutadoseia.composeapp.generated.resources.role_selector_passenger_title
import enrutadoseia.composeapp.generated.resources.role_selector_subtitle
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource

@Composable
fun RoleSelectorScreen(
    user: User,
    onSelectDriver: () -> Unit,
    onSelectPassenger: () -> Unit
) {
    Scaffold { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(Res.string.role_selector_greeting, user.name ?: ""),
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.role_selector_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(48.dp))

            RoleCard(
                title = stringResource(Res.string.role_selector_driver_title),
                subtitle = stringResource(Res.string.role_selector_driver_subtitle),
                icon = vectorResource(Res.drawable.location_on_24px),
                onClick = onSelectDriver
            )

            Spacer(modifier = Modifier.height(16.dp))

            RoleCard(
                title = stringResource(Res.string.role_selector_passenger_title),
                subtitle = stringResource(Res.string.role_selector_passenger_subtitle),
                icon = vectorResource(Res.drawable.person_24px),
                onClick = onSelectPassenger
            )
        }
    }
}

@Preview
@Composable
private fun RoleSelectorScreenPreview() {
    CarpoolTheme {
        RoleSelectorScreen(
            user = User(
                id = "1",
                email = "usuario@eia.edu.co",
                name = "Juan Pablo",
                isEmailVerified = true,
                isPassenger = true,
                isDriver = true
            ),
            onSelectDriver = {},
            onSelectPassenger = {}
        )
    }
}

@Composable
private fun RoleCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold)
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
