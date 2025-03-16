package com.khl_app.ui.screens.client

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.khl_app.domain.models.FollowerResponse

@Composable
fun FollowerItem(
    follower: FollowerResponse,
    onMessageClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF303030))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar with colored border
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(getAvatarBorderColor(follower.points)),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(CircleShape)
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "avatar",
                        color = Color.White,
                        fontSize = 10.sp
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Name
            Text(
                text = follower.name,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )

            // Points with color based on value
            Text(
                text = follower.points.toString(),
                color = getPointsColor(follower.points),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(end = 12.dp)
            )

            // Action buttons
            Row {
                IconButton(
                    onClick = { onMessageClick(follower.id) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = "просмотр",
                        tint = Color.Gray
                    )
                }

                IconButton(
                    onClick = { onDeleteClick(follower.id) },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "удалить",
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

// Функция для определения цвета очков
private fun getPointsColor(points: Int): Color {
    return when {
        points >= 1000 -> Color(0xFFFFFF00) // Желтый
        points >= 500 -> Color(0xFFFFAA00)  // Оранжевый
        else -> Color.White
    }
}

// Функция для определения цвета рамки аватара
private fun getAvatarBorderColor(points: Int): Color {
    return when {
        points >= 1000 -> Color(0xFFFF5555) // Красный
        points >= 800 -> Color(0xFFFFFF00)  // Желтый
        points >= 500 -> Color(0xFF00FF00)  // Зеленый
        else -> Color(0xFF999999)           // Серый
    }
}