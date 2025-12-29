package com.example.practice.practice

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.io.File

@Composable
fun DownloadedVideoItem(
    filePath: String,
   // onDelete: () -> Unit,
   // onShare: () -> Unit,
   // onSaveToGallery:  () -> Unit,
   // onRename: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val file = File(filePath)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C1C1C))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

           // ▶️ **Video thumbnail / icon**
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = file.name,
                    color = Color.White,
                    maxLines = 1
                )
                Text(
                    text = "${file.length() / 1024 / 1024} MB",
                    color = Color.Gray,
                    fontSize = 12.sp
                )
            }

            IconButton(onClick = { expanded = true }) {
                Icon(
                    imageVector = Icons.Default.MoreVert,
                    contentDescription = null,
                    tint = Color.White
                )
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {

                DropdownMenuItem(
                    text = { Text("Save in Gallery") },
                    onClick = {
                        expanded = false
                       // onSaveToGallery()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Share") },
                    onClick = {
                        expanded = false
                      //  onShare()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Rename") },
                    onClick = {
                        expanded = false
                     //   onRename()
                    }
                )

                DropdownMenuItem(
                    text = { Text("Delete Video") },
                    onClick = {
                        expanded = false
                       // onDelete()
                    }
                )
            }
        }
    }
}
