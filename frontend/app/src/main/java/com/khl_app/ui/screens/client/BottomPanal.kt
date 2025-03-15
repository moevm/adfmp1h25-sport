package com.khl_app.ui.screens.client

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.motion.widget.MotionScene.Transition.TransitionOnClick
import com.khl_app.ui.themes.KhlAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomPanel() {
    val panelState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomPanel by remember { mutableStateOf(false) }
    ModalBottomSheet(
        onDismissRequest = {
            showBottomPanel = false
        },
        sheetState = panelState,
        containerColor = Color(0xFF6C5CE7)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {
            BottomPanelElement("Calendar", Icons.Filled.DateRange, {})
            BottomPanelElement("Trackable", Icons.Filled.Favorite, {})
            BottomPanelElement("Profile", Icons.Filled.Person, {})
            BottomPanelElement("Logout", Icons.AutoMirrored.Filled.ExitToApp, {})
        }

    }
}

@Composable
fun BottomPanelElement(
    text: String,
    image: ImageVector,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth()
            .padding(PaddingValues(bottom = 5.dp))
            .clickable(
            onClick = {
                onClick()
            },
        )
            .padding(vertical = 5.dp)

    ) {
        Image(
            image,
            contentDescription = text,
            modifier = Modifier.padding(horizontal = 10.dp)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            textAlign = TextAlign.Left,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
}

@Preview(showBackground = true)
@Composable
fun BottomPanelPreview() {
    KhlAppTheme {
        BottomPanel()
    }
}