package com.example.myapplication2

import android.Manifest
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Image
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil.compose.rememberImagePainter
import com.example.camerajetpackcomposevideo.CameraView
import com.example.myapplication2.ui.theme.MyApplication2Theme
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class MainActivity : ComponentActivity() {

    private lateinit var outputDirectory: File
    private lateinit var cameraExecutor: ExecutorService

    private var shouldShowCamera: MutableState<Boolean> = mutableStateOf(false)

    private lateinit var photoUri: Uri
    private var shouldShowPhoto: MutableState<Boolean> = mutableStateOf(false)
    private var shouldShowNothing: MutableState<Boolean> = mutableStateOf(true)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestCameraPermission()

        outputDirectory = getOutputDirectory()
        cameraExecutor = Executors.newSingleThreadExecutor()

        setContent {
            MyApplication2Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colorScheme.background) {
                    Column(modifier = Modifier.fillMaxSize()) {
                        Spacer(modifier = Modifier.padding(16.dp))
                        if (shouldShowNothing.value) {
                            Button(
                                onClick = {
                                    shouldShowCamera.value = true
                                    shouldShowNothing.value = false
                                },
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(text = "Take a photo")
                            }
                        } else {
                            if (shouldShowCamera.value) {
                                showCamera()
                            }
                            if (shouldShowPhoto.value) {
                                showPhoto()
                            }
                        }
                    }
                }
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Log.i("kilo", "Permission granted")
            shouldShowCamera.value = false
        } else {
            Log.i("kilo", "Permission denied")
        }
    }


    @Composable
    private fun MainActivityView() {
        if (!shouldShowNothing.value) {
            if (shouldShowCamera.value) {
                CameraView(
                    outputDirectory = outputDirectory,
                    executor = cameraExecutor,
                    onImageCaptured = ::handleImageCapture,
                    onError = { Log.e("kilo", "View error:", it) }
                )
            }

            if (shouldShowPhoto.value) {
                Image(
                    painter = rememberImagePainter(photoUri),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize()
                )

                Button(onClick = { shouldShowPhoto.value = false; shouldShowCamera.value = true }) {
                    Text(text = "Take another photo")
                }
            }
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                Log.i("kilo", "Permission previously granted")
                shouldShowCamera.value = true
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.CAMERA
            ) -> Log.i("kilo", "Show camera permissions dialog")

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    @Composable
    private fun showCamera() {
        CameraView(
            outputDirectory = outputDirectory,
            executor = cameraExecutor,
            onImageCaptured = { uri ->
                shouldShowPhoto.value = true
                shouldShowCamera.value = false
                photoUri = uri
            },
            onError = { Log.e("kilo", "View error:", it) }
        )
        Button(
            onClick = {
                shouldShowCamera.value = false
                shouldShowNothing.value = true
                shouldShowPhoto.value = false
            },
            modifier = Modifier.padding(16.dp)
        ) {
            Text(text = "Hide camera")
        }
    }

    @Composable
    private fun showPhoto() {
        Image(
            painter = rememberImagePainter(photoUri),
            contentDescription = null,
            modifier = Modifier.padding(16.dp)
        )
        Button(
            onClick = {
                shouldShowCamera.value = true
                shouldShowNothing.value = false
                shouldShowPhoto.value = false
            }

        ) {
            Text(text = "Take another photo")
        }
        Button(
            onClick = {
                shouldShowCamera.value = false
                shouldShowNothing.value = true
                shouldShowPhoto.value = false
            }

        ) {
            Text(text = "Back to main")
        }

    }

    private fun handleImageCapture(uri: Uri) {
        Log.i("kilo", "Image captured: $uri")
        shouldShowCamera.value = false

        photoUri = uri
        shouldShowPhoto.value = true
    }

    private fun getOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() }
        }

        return if (mediaDir != null && mediaDir.exists()) mediaDir else filesDir
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}

// preview of the app
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    MyApplication2Theme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column {Modifier.fillMaxSize()
                //Add padding to the top of the screen
                Spacer(modifier = Modifier.fillMaxSize())
                Button(onClick = { /*shouldShowCamera.value = true \*/
                }) {
                    Text(text = "Take a photo")
                }
            }
        }
    }
}