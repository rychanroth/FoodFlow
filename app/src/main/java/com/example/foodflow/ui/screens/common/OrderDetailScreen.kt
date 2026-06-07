package com.example.foodflow.ui.screens.common

import android.content.Intent
import android.graphics.Bitmap
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.foodflow.data.model.Order
import com.example.foodflow.ui.components.common.OrderShareableCard
import com.example.foodflow.ui.viewmodel.OrderDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    viewModel: OrderDetailViewModel = viewModel()
) {
    val order by viewModel.order.collectAsState()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // This layer records the drawing commands of our Card
    val graphicsLayer = rememberGraphicsLayer()

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        OrderDetailContent(
            order = order!!,
            graphicsLayer = graphicsLayer,
            onBackClick = { navController.popBackStack() },
            onShareClick = {
                scope.launch(Dispatchers.IO) {
                    // 1. Convert the graphics layer to a Bitmap
                    val bitmap = graphicsLayer.toImageBitmap().asAndroidBitmap()

                    // 2. Save Bitmap to cache
                    val file = File(context.cacheDir, "images/order_${order!!.id.takeLast(5)}.png")
                    file.parentFile?.mkdirs()
                    file.outputStream().use { out ->
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }

                    // 3. Get URI via FileProvider
                    val uri = FileProvider.getUriForFile(
                        context,
                        "${context.packageName}.fileprovider",
                        file
                    )

                    // 4. Fire Share Intent
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, uri)
                        type = "image/png"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Share Order Receipt"))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailContent(
    order: Order,
    graphicsLayer: GraphicsLayer,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Order #${order.id.takeLast(5)}") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onShareClick) {
                        Icon(Icons.Default.Share, contentDescription = "Share Receipt")
                    }
                }
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // The Shareable Card with the capture modifier attached
            OrderShareableCard(
                order = order,
                modifier = Modifier.drawWithContent {
                    // Record the drawing into the graphicsLayer
                    graphicsLayer.record {
                        this@drawWithContent.drawContent()
                    }
                    // Actually draw it on screen
                    drawContent()
                }
            )

            // You can still add other non-shareable UI elements below the card here
            // e.g., Earnings Breakdown strictly for the app UI, not the receipt
        }
    }
}