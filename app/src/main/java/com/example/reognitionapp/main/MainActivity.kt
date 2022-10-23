package com.example.reognitionapp.main

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.*
import android.media.Image
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.example.reognitionapp.R
import com.example.reognitionapp.api.ApiResponseStatus
import com.example.reognitionapp.api.ApiServiceInterceptor
import com.example.reognitionapp.auth.LoginActivity
import com.example.reognitionapp.databinding.ActivityMainBinding
import com.example.reognitionapp.dogdetail.DogDetailActivity
import com.example.reognitionapp.dogdetail.DogDetailActivity.Companion.DOG_KEY
import com.example.reognitionapp.dogdetail.DogDetailActivity.Companion.IS_RECOGNITION_KEY
import com.example.reognitionapp.doglist.DogListActivity
import com.example.reognitionapp.domain.Dog
import com.example.reognitionapp.domain.User
import com.example.reognitionapp.machinelearning.Classifier
import com.example.reognitionapp.machinelearning.DogRecognition
import com.example.reognitionapp.setVisibility
import com.example.reognitionapp.settings.SettingsActivity
import dagger.hilt.android.AndroidEntryPoint
import org.tensorflow.lite.support.common.FileUtil
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                setupCamera()
            } else {
                Toast.makeText(
                    this,
                    "You need to accept camera permission to use this app",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    private lateinit var binding: ActivityMainBinding
    private lateinit var imageCapture: ImageCapture
    private lateinit var cameraExecutor: ExecutorService

    private var isCameraReady = false

    private lateinit var classifier: Classifier

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val user = User.getLoggedInUser(this)

        if (user == null) {
            openLoginActivity()
            return
        } else {
            ApiServiceInterceptor.setSessionToken(user.authenticationToken)
        }

        viewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseStatus.Loading -> setVisibility(binding.progressBar, true)
                is ApiResponseStatus.Success -> setVisibility(binding.progressBar, false)
                is ApiResponseStatus.Error -> {
                    setVisibility(binding.progressBar, false)
                    //showErrorDialog(status.messageId)
                }
            }
        }

        viewModel.dog.observe(this) { dog ->
            if (dog != null) {
                openDogDetailActivity(dog)
            }
        }

        viewModel.dogRecognition.observe(this) { dogRecognition ->
            enableTakePhotoButton(dogRecognition)
        }

        setupClickListeners()
        requestCameraPermission()
    }

    private fun openDogDetailActivity(dog: Dog) {
        val intent = Intent(this, DogDetailActivity::class.java)
        intent.putExtra(DOG_KEY, dog)
        intent.putExtra(IS_RECOGNITION_KEY, true)
        startActivity(intent)
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(Runnable {
            val cameraProvider = cameraProviderFuture.get()

            //Preview
            val preview = Preview.Builder().build()
            preview.setSurfaceProvider(binding.cameraPreview.surfaceProvider)

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            val imageAnalysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
                viewModel.recognizeImage(imageProxy)
            }

            cameraProvider.bindToLifecycle(
                this,
                cameraSelector,
                preview,
                imageCapture,
                imageAnalysis
            )

        }, ContextCompat.getMainExecutor(this))
    }

    private fun enableTakePhotoButton(dogRecognition: DogRecognition) {

        if (dogRecognition.confidence > 70.0) {
            binding.takePhotoFab.alpha = 1f
            binding.takePhotoFab.setOnClickListener {
                viewModel.getDogByMlId(dogRecognition.id)
            }
        } else {
            binding.takePhotoFab.alpha = 0.2f
            binding.takePhotoFab.setOnClickListener(null)
        }
    }

    private fun setupClickListeners() {

        binding.settingsFab.setOnClickListener {
            openSettingsActivity()
        }

        binding.dogListFab.setOnClickListener {
            openDogListActivity()
        }
    }

    private fun setupCamera() {
        binding.cameraPreview.post {
            imageCapture = ImageCapture.Builder()
                .setTargetRotation(binding.cameraPreview.display.rotation)
                .build()

            cameraExecutor = Executors.newSingleThreadExecutor()
            startCamera()
            isCameraReady = true
        }
    }

    /*private fun takePhoto() {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(getOutputPhotoFile()).build()
        imageCapture.takePicture(outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    Toast.makeText(this@MainActivity, "Error ${error.message}", Toast.LENGTH_SHORT)
                        .show()
                }

                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                }
            })
    }

    private fun getOutputPhotoFile(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name) + ".jpg").apply { mkdir() }
        }
        return if (mediaDir != null && mediaDir.exists()) {
            mediaDir
        } else {
            filesDir
        }
    }*/

    private fun openDogListActivity() {
        startActivity(Intent(this, DogListActivity::class.java))
    }

    private fun openSettingsActivity() {
        startActivity(Intent(this, SettingsActivity::class.java))
    }

    private fun openLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()

        if (::cameraExecutor.isInitialized)
            cameraExecutor.shutdown()
    }

    override fun onStart() {
        super.onStart()
        viewModel.setupClassifier(
            FileUtil.loadMappedFile(this@MainActivity, "model.tflite"),
            FileUtil.loadLabels(this@MainActivity, "labels.txt")
        )
    }

    private fun requestCameraPermission() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    setupCamera()
                }
                shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                    AlertDialog.Builder(this)
                        .setTitle(getString(R.string.permission_camera_title))
                        .setMessage(getString(R.string.permission_camera_message))
                        .setPositiveButton(android.R.string.ok) { _, _ ->
                            requestPermissionLauncher.launch(
                                Manifest.permission.CAMERA
                            )
                        }
                        .setNegativeButton(android.R.string.cancel) { _, _ ->
                        }
                        .show()
                }
                else -> {
                    requestPermissionLauncher.launch(
                        Manifest.permission.CAMERA
                    )
                }
            }
        } else {
            setupCamera()
        }
    }
}