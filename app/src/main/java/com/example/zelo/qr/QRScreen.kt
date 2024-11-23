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
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController

@Composable
fun QRScannerScreen() {
    val navController: NavController = rememberNavController()
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
    var isCameraActive by remember { mutableStateOf(true) } // To control the camera
    var cameraProvider: ProcessCameraProvider? by remember { mutableStateOf(null) }
    var previewView: PreviewView? by remember { mutableStateOf(null) }
    val coroutineScope = rememberCoroutineScope()

    // Request camera permission
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

    // Show Dialog when QR code is scanned
    val showDialog = scannedResult != null

    // Initialize camera
    LaunchedEffect(key1 = isCameraActive) {
        if (isCameraActive) {
            val cameraProvider = cameraProviderFuture.get()
            this@LaunchedEffect.runCatching {
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
                    navController.navigate("home/transference/form?email={$scannedResult}")
                    // Pause the camera immediately after scan
                }

                imageAnalysis.setAnalyzer(
                    Executors.newSingleThreadExecutor(),
                    QRCodeAnalyzer(context, onQrCodeScanned)
                )

                // Bind the camera to the lifecycle
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
                        previewView = previewViewInstance // Save the previewView for later use
                        previewViewInstance
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

    // Show Dialog with the scanned data
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { scannedResult = null }, // Dismiss dialog
            title = { Text("Scanned QR Code") },
            text = { Text("Scanned Data: $scannedResult") },
            confirmButton = {
                Button(onClick = {
                    // Close dialog and resume the camera
                    scannedResult = null
                    isCameraActive = true // Resume the camera after confirmation
                }) {
                    Text("OK")
                }
            }
        )
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
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            // Start scanning the image
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        barcode.rawValue?.let { result ->
                            // Save the result locally
                            saveScannedData(context, result)

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

fun saveScannedData(context: Context, data: String) {
    val sharedPreferences = context.getSharedPreferences("QRData", Context.MODE_PRIVATE)
    val editor = sharedPreferences.edit()
    editor.putString("last_scanned_qr", data)
    editor.apply()
}