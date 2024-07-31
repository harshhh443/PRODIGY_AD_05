package com.example.qrscanner

import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.zxing.ResultPoint
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import android.Manifest
import android.content.Intent
import android.net.Uri
import android.webkit.URLUtil

class MainActivity : AppCompatActivity() {

    private lateinit var barcodeView: DecoratedBarcodeView
    private val CAMERA_PERMISSION_REQUEST = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        barcodeView = findViewById(R.id.barcode_scanner)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            startScanning()
        }

    }

    private fun startScanning() {
        barcodeView.decodeContinuous(object : BarcodeCallback {
            override fun barcodeResult(result: BarcodeResult) {
                // Handle the scanned QR code result
                val scannedText = result.text
                if (URLUtil.isValidUrl(scannedText)) {
                    // If it's a valid URL, open it in a browser
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(scannedText))
                    startActivity(intent)
                } else {
                    // If it's not a valid URL, show the scanned text in a Toast
                    Toast.makeText(applicationContext, "Scanned: $scannedText", Toast.LENGTH_LONG).show()
                }
            }
            override fun possibleResultPoints(resultPoints: List<ResultPoint>) {}
        })
    }

    override fun onResume() {
        super.onResume()
        barcodeView.resume()
    }

    override fun onPause() {
        super.onPause()
        barcodeView.pause()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanning()
            } else {
                Toast.makeText(this, "Camera permission is required", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }
}