package com.samuelcruz.qrchuyenkhoan.ui

import android.view.WindowManager
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.samuelcruz.qrchuyenkhoan.R
import com.samuelcruz.qrchuyenkhoan.data.ImageExport
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(result: QrResult, onBack: () -> Unit) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbar = remember { SnackbarHostState() }
    val image = remember(result) { result.bitmap.asImageBitmap() }
    val accent = bankColor(result.card.bank)
    val background = MaterialTheme.colorScheme.background
    FullBrightness()

    Surface(color = background, modifier = Modifier.fillMaxSize()) {
        Box(Modifier.fillMaxSize()) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .background(Brush.verticalGradient(listOf(accent, accent.copy(alpha = 0.55f), background))),
            )
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .statusBarsPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Quay lại", tint = Color.White)
                    }
                    Text("Mã QR của bạn", style = MaterialTheme.typography.titleLarge, color = Color.White)
                }
                Text(
                    "Đưa mã cho người chuyển quét, hoặc lưu / chia sẻ ảnh",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.9f),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 12.dp, bottom = 16.dp),
                )
                Card(
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Image(
                        bitmap = image,
                        contentDescription = "Mã QR chuyển khoản đến ${result.card.accountName}",
                        contentScale = ContentScale.FillWidth,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                val saved = ImageExport.saveToGallery(context, result.bitmap, result.fileName)
                                snackbar.showSnackbar(
                                    if (saved) "Đã lưu vào Ảnh › ${ImageExport.ALBUM}" else "Không lưu được ảnh",
                                )
                            }
                        },
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(56.dp),
                    ) {
                        Icon(painterResource(R.drawable.ic_download), contentDescription = null, modifier = Modifier.size(20.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Lưu ảnh", style = MaterialTheme.typography.titleSmall)
                    }
                    GradientButton(
                        text = "Chia sẻ",
                        icon = rememberVectorPainter(Icons.Filled.Share),
                        onClick = { scope.launch { ImageExport.share(context, result.bitmap, result.fileName) } },
                        brush = Brush.linearGradient(listOf(accent, accent.copy(alpha = 0.8f))),
                        modifier = Modifier.weight(1f),
                    )
                }
                TextButton(onClick = onBack, modifier = Modifier.padding(top = 8.dp)) {
                    Text("Tạo mã khác", style = MaterialTheme.typography.titleSmall)
                }
                Spacer(Modifier.navigationBarsPadding().height(16.dp))
            }
            SnackbarHost(
                snackbar,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(16.dp),
            )
        }
    }
}

/** Turns the screen to full brightness while the code is shown, so it scans well; restores it on leaving. */
@Composable
private fun FullBrightness() {
    val window = LocalActivity.current?.window ?: return
    DisposableEffect(window) {
        val before = window.attributes.screenBrightness
        window.attributes = window.attributes.apply { screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL }
        onDispose {
            window.attributes = window.attributes.apply { screenBrightness = before }
        }
    }
}
