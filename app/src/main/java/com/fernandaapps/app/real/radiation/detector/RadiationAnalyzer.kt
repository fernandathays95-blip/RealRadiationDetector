package com.fernandaapps.app.real.radiation.detector

import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy

class RadiationAnalyzer(
    private val update: (Int, Int) -> Unit
) : ImageAnalysis.Analyzer {

    private var cps = 0
    private var cpm = 0
    private var lastTime = System.currentTimeMillis()
    private val ignored = HashSet<Int>()

    override fun analyze(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val data = ByteArray(buffer.remaining())
        buffer.get(data)

        var events = 0

        for (i in data.indices) {
            val v = data[i].toInt() and 0xFF
            if (v > 250 && !ignored.contains(i)) {
                events++
                ignored.add(i)
            }
        }

        cps += events

        val now = System.currentTimeMillis()
        if (now - lastTime >= 1000) {
            cpm += cps
            update(cps, cpm)
            cps = 0
            lastTime = now
            ignored.clear()
        }

        image.close()
    }
}
