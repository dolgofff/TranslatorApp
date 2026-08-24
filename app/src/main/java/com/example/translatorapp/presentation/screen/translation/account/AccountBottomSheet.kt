package com.example.translatorapp.presentation.screen.translation.account

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.translatorapp.presentation.ui.theme.MainColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountBottomSheet(
    photoUrl: String,
    name: String,
    email: String,
    onDismissRequest: () -> Unit,
    onSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            AccountSheetHeader(onDoneClick = onDismissRequest)

            AccountCard(
                photoUrl = photoUrl,
                name = name,
                email = email
            )

            HorizontalDivider(modifier = Modifier.padding(top = 8.dp))

            AccountActionItem(
                icon = Icons.Outlined.Settings,
                text = "Settings",
                onItemClick = onSettingsClick
            )

            AccountActionItem(
                icon = Icons.AutoMirrored.Outlined.Logout,
                text = "Log out",
                onItemClick = onLogoutClick
            )
        }
    }
}

@Composable
private fun AccountSheetHeader(onDoneClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Select an Account",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.align(Alignment.Center)
        )

        TextButton(
            onClick = onDoneClick,
            modifier = Modifier.align(Alignment.CenterEnd)
        ) {
            Text(
                text = "Done",
                color = MainColor,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
private fun AccountCard(photoUrl: String, name: String, email: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AccountPhoto(
            photoUrl = photoUrl,
            modifier = Modifier.size(44.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(
            verticalArrangement = Arrangement.Center
        ) {
            if (name.isNotBlank()) {
                Text(
                    text = name,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(2.dp))
            }

            Text(
                text = email,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccountPhoto(photoUrl: String?, modifier: Modifier = Modifier) {
    val placeholderColor = MaterialTheme.colorScheme.surfaceVariant

    Box(
        modifier = modifier
            .clip(CircleShape)
            .background(placeholderColor),
        contentAlignment = Alignment.Center
    ) {
        if (!photoUrl.isNullOrBlank()) {
            AsyncImage(
                model = photoUrl,
                contentDescription = "Profile photo",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun AccountActionItem(icon: ImageVector, text: String, onItemClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .clickable(onClick = onItemClick)
            .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MainColor,
            modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}