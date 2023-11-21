package com.dawe.permissionexplorer.permissionexplorer

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import java.io.IOException
import java.io.FileWriter
import java.io.FileReader
import java.io.BufferedReader
import android.content.Intent
import android.provider.MediaStore
import java.io.File
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class SecondActivity : AppCompatActivity() {

    private val FILE_NAME = "sample.txt"
    private val CAMERA_PERMISSION_REQUEST_CODE = 100
    private val LOCATION_PERMISSION_REQUEST_CODE = 101
    private val STORAGE_PERMISSION_REQUEST_CODE = 102


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        val btnCamera = findViewById<Button>(R.id.btnCamera)
        val btnLocation = findViewById<Button>(R.id.btnLocation)
        val btnStorage = findViewById<Button>(R.id.btnStorage)

        btnCamera.setOnClickListener {
            requestCameraPermission()
        }

        btnLocation.setOnClickListener {
            requestLocationPermission()
        }

        btnStorage.setOnClickListener {
            requestStoragePermission()
        }

        // Add onClickListeners for other UI elements that require permissions
    }

    private fun requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            showToast("Camera permission already granted")
            performCameraAction()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST_CODE
            )
        }
    }


    private fun requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            showToast("Location permission already granted")
            performLocationAction()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }
    }

    private fun requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            showToast("Storage permission already granted")
            performStorageAction()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                STORAGE_PERMISSION_REQUEST_CODE
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            CAMERA_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Camera permission granted")
                    performCameraAction()
                } else {
                    showToast("Camera permission denied")
                }
            }
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Location permission granted")
                    performLocationAction()
                } else {
                    showToast("Location permission denied")
                }
            }
            STORAGE_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showToast("Storage permission granted")
                    performStorageAction()
                } else {
                    showToast("Storage permission denied")
                }
            }
            // Handle other permissions here
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }


    private fun performCameraAction() {
        showToast("Camera action performed")


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)


            if (cameraIntent.resolveActivity(packageManager) != null) {

                startActivityForResult(cameraIntent, CAMERA_PERMISSION_REQUEST_CODE)
            } else {
                showToast("No camera app found")
            }
        } else {
            showToast("Camera permission denied")

        }
    }

    private fun performLocationAction() {
        showToast("Location action performed")


        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


            val locationListener = object : LocationListener {
                override fun onLocationChanged(location: Location) {

                    showToast("Latitude: ${location.latitude}, Longitude: ${location.longitude}")
                }

                override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

                }

                override fun onProviderEnabled(provider: String) {

                }

                override fun onProviderDisabled(provider: String) {
                }
            }

            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                0,
                0f,
                locationListener
            )
        } else {
            showToast("Location permission denied")

        }
    }


    private fun performStorageAction() {
        showToast("Storage action performed")


        val contentToWrite = "Hello, Storage Permission!"
        writeToFile(contentToWrite)


        val contentRead = readFromFile()
        showToast("Content read from file: $contentRead")
    }

    private fun writeToFile(content: String) {
        val externalStorageState = Environment.getExternalStorageState()

        if (externalStorageState == Environment.MEDIA_MOUNTED) {
            val directory = getExternalStorageDirectory()
            val file = File(directory, FILE_NAME)

            try {
                val fileWriter = FileWriter(file)
                fileWriter.write(content)
                fileWriter.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        } else {
            showToast("External storage is not available.")
        }
    }

    private fun readFromFile(): String {
        val externalStorageState = Environment.getExternalStorageState()

        return if (externalStorageState == Environment.MEDIA_MOUNTED) {
            val directory = getExternalStorageDirectory()
            val file = File(directory, FILE_NAME)

            if (file.exists()) {
                try {
                    val fileReader = FileReader(file)
                    val bufferedReader = BufferedReader(fileReader)
                    val content = bufferedReader.readLine()
                    fileReader.close()
                    return content ?: "No content found"
                } catch (e: IOException) {
                    e.printStackTrace()
                    return "Error reading from file"
                }
            } else {
                "File does not exist"
            }
        } else {
            "External storage is not available."
        }
    }

    private fun getExternalStorageDirectory(): File {
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
    }
}
