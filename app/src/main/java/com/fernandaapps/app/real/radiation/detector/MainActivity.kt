package com.fernandaapps.app.real.radiation.detector

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val cpsText = findViewById<TextView>(R.id.cps)
        val cpmText = findViewById<TextView>(R.id.cpm)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val analysis = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            analysis.setAnalyzer(
                Executors.newSingleThreadExecutor(),
                RadiationAnalyzer { cps, cpm ->
                    runOnUiThread {
                        cpsText.text = "CPS: $cps"
                        cpmText.text = "CPM: $cpm"
                    }
                }
            )

            cameraProvider.bindToLifecycle(
                this,
                CameraSelector.DEFAULT_BACK_CAMERA,
                analysis
            )

        }, ContextCompat.getMainExecutor(this))
    }
}
