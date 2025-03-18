package com.khl_app.ui.screens.client

import android.graphics.BitmapFactory
import android.util.Base64
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.khl_app.domain.models.FollowerResponse
import com.khl_app.domain.models.LevelSystem

@Composable
fun FollowerItem(
    follower: FollowerResponse,
    onMessageClick: (String) -> Unit,
    onDeleteClick: (String) -> Unit,
    onItemClick: (String) -> Unit,
    isYou: Boolean,
    isTopFollower: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .background(Color.Transparent)
            .clickable { onItemClick(follower.id) }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp),
                contentAlignment = Alignment.Center
            ) {
                // Золотая корона для первого в списке
                if (isTopFollower) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = "Лидер",
                        tint = Color(0xFFFFD700), // Золотой цвет
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = (0).dp, x = 40.dp)
                            .size(24.dp)
                            .zIndex(1f)
                    )
                }

                Box(
                    modifier = Modifier
                        .size(70.dp)
                        .clip(CircleShape)
                        .background(LevelSystem.getAvatarBorderColor(follower.points)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .background(Color.DarkGray),
                        contentAlignment = Alignment.Center
                    ) {
                        if (follower.avatar != null && follower.avatar.isNotEmpty()) {
                            val avatarBitmap = remember(follower.avatar) {
                                try {
                                    val imageBytes = Base64.decode(follower.avatar, Base64.DEFAULT)
                                    BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size).asImageBitmap()
                                } catch (e: Exception) {
                                    null
                                }
                            }

                            if (avatarBitmap != null) {
                                Image(
                                    bitmap = avatarBitmap,
                                    contentDescription = "Аватар пользователя",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = follower.name.first().toString(),
                                    color = Color.White,
                                    fontSize = 22.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Text(
                                text = follower.name.first().toString(),
                                color = Color.White,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                Text(
                    text = follower.points.toString(),
                    color = LevelSystem.getPointsColor(follower.points),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "очков",
                    color = Color.LightGray,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = follower.name,
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = LevelSystem.getLevelLabel(follower.points),
                    color = LevelSystem.getPointsColor(follower.points),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            if(!isYou) {
                Row {
                    IconButton(
                        onClick = { onMessageClick(follower.id) },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "просмотр",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    IconButton(
                        onClick = { onDeleteClick(follower.id) },
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "удалить",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                }
            } else {
                Text(
                    "Это вы",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.width(16.dp))
            }
        }
    }
}