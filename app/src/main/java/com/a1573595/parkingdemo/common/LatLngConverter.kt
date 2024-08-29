package com.a1573595.parkingdemo.common

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

class LatLngConverter {
    companion object {
        private const val a = 6378137.0
        private const val b = 6356752.314245
        private const val lon0 = 121 * Math.PI / 180
        private const val k0 = 0.9999
        private const val dx = 250000

        fun twd97ToLonLat(x: Double, y: Double): Pair<Double, Double> {
            var x = x
            var y = y
            val dy = 0.0
            val e = (1 - b.pow(2.0) / a.pow(2.0)).pow(0.5)
            x -= dx.toDouble()
            y -= dy

            // Calculate the Meridional Arc
            val M = y / k0

            // Calculate Footprint Latitude
            val mu = M / (a * (1.0 - e.pow(2.0) / 4.0 - 3 * e.pow(4.0) / 64.0 - 5 * e.pow(6.0) / 256.0))
            val e1 = (1.0 - (1.0 - e.pow(2.0)).pow(0.5)) / (1.0 + (1.0 - e.pow(2.0)).pow(0.5))
            val j1 = 3 * e1 / 2 - 27 * e1.pow(3.0) / 32.0
            val j2 = 21 * e1.pow(2.0) / 16 - 55 * e1.pow(4.0) / 32.0
            val j3 = 151 * e1.pow(3.0) / 96.0
            val j4 = 1097 * e1.pow(4.0) / 512.0
            val fp = mu + j1 * sin(2 * mu) + j2 * sin(4 * mu) + j3 * sin(6 * mu) + j4 * sin(8 * mu)

            // Calculate Latitude and Longitude
            val e2 = (e * a / b).pow(2.0)
            val c1 = (e2 * cos(fp)).pow(2.0)
            val t1 = tan(fp).pow(2.0)
            val r1 = a * (1 - e.pow(2.0)) / (1 - e.pow(2.0) * sin(fp).pow(2.0)).pow(3.0 / 2.0)
            val n1 = a / (1 - e.pow(2.0) * sin(fp).pow(2.0)).pow(0.5)
            val D = x / (n1 * k0)

            // Calculate latitude
            val q1 = n1 * tan(fp) / r1
            val q2 = D.pow(2.0) / 2.0
            val q3 = (5 + 3 * t1 + 10 * c1 - 4 * c1.pow(2.0) - 9 * e2) * D.pow(4.0) / 24.0
            val q4 = (61 + 90 * t1 + 298 * c1 + 45 * t1.pow(2.0) - 3 * c1.pow(2.0) - 252 * e2) * D.pow(6.0) / 720.0
            var lat = fp - q1 * (q2 - q3 + q4)

            // Calculate longitude
            val q6 = (1 + 2 * t1 + c1) * D.pow(3.0) / 6
            val q7 = (5 - 2 * c1 + 28 * t1 - 3 * c1.pow(2.0) + 8 * e2 + 24 * t1.pow(2.0)) * D.pow(5.0) / 120.0
            var lon = lon0 + (D - q6 + q7) / cos(fp)

            // Convert to degrees
            lat = lat * 180 / Math.PI
            lon = lon * 180 / Math.PI

            return Pair(lat, lon)
        }
    }
}