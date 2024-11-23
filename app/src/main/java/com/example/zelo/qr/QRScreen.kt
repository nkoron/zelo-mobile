package com.example.zelo.qr

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.zelo.MyApplication
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import com.example.zelo.R
import com.example.zelo.dashboard.DashboardViewModel
import com.example.zelo.profile.ProfileViewModel

@Composable
fun QRCodeDialog(
    onDismiss: () -> Unit,
    content: String
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(16.dp))
                Image(
                    bitmap = generateQRCode(content).asImageBitmap(),
                    contentDescription = stringResource(R.string.qr_code),
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextButton(onClick = onDismiss) {
                    Text(stringResource(R.string.cancel))
                }
            }
        }
    }
}

@Composable
fun QRScannerScreen(
    onSuccess: (String) -> Unit,
    viewModel: ProfileViewModel = viewModel(factory = ProfileViewModel.provideFactory(LocalContext.current.applicationContext as MyApplication))
) {
    val email = viewModel.uiState.user?.email
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    var hasCameraPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        )
    }
    var scannedResult by remember { mutableStateOf<String?>(null) }
    var isCameraActive by remember { mutableStateOf(true) }
    var showQr by remember { mutableStateOf(false) } // Estado para controlar el diálogo de "Mostrar mi QR"
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasCameraPermission = granted
        }
    )

    LaunchedEffect(key1 = true) {
        if (!hasCameraPermission) {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    LaunchedEffect(key1 = isCameraActive) {
        if (isCameraActive) {
            val cameraProvider = cameraProviderFuture.get()
            runCatching {
                val preview = Preview.Builder().build()
                previewView?.let {
                    preview.setSurfaceProvider(it.surfaceProvider)
                }
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                    .build()

                val imageAnalysis = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()

                val onQrCodeScanned: (String) -> Unit = { result ->
                    scannedResult = result
                    isCameraActive = false
                }

                imageAnalysis.setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    QRCodeAnalyzer(context, onQrCodeScanned)
                )

                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            }
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (hasCameraPermission && isCameraActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                AndroidView(
                    factory = { ctx ->
                        val previewViewInstance = PreviewView(ctx)
                        previewView = previewViewInstance
                        previewViewInstance
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        Button(
            onClick = { showQr = true },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            )
        ) {
            Text(
                text = "Mostrar mi QR",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
        }

        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
        )
    }

    if (showQr) {
        QRCodeDialog(
            content = email.toString(), // Reemplaza con el contenido adecuado
            onDismiss = { showQr = false }
        )
    }

    if (scannedResult != null) {
        onSuccess(scannedResult!!)
        scannedResult = null
    }
}

class QRCodeAnalyzer(
    private val context: Context,
    private val onQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @androidx.camera.core.ExperimentalGetImage
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image =
                InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Start scanning the image
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { result ->
                            // Save the result locally
                            // Log and pass the result to the Composable function
                            onQrCodeScanned(result)
                        }
                    }
                }
                .addOnFailureListener {
                    // Handle any failure (e.g., logging or error handling)
                    println("QR Code Scan Failed")
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        }
    }
}
