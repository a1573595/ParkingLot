package com.a1573595.parkinglotdemo.tool

import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.tan

class LatLngCoding {
    companion object {
        private const val a = 6378137.0
        private const val b = 6356752.314245
        private const val lon0 = 121 * Math.PI / 180
        private const val k0 = 0.9999
        private const val dx = 250000

        fun calTWD97ToLonLat(x: Double, y: Double): String {
            var x = x
            var y = y
            val dy = 0.0
            val e = (1 - b.pow(2.0) / a.pow(2.0)).pow(0.5)
            x -= dx.toDouble()
            y -= dy

            // Calculate the Meridional Arc
            val M = y / k0

            // Calculate Footprint Latitude
            val mu =
                M / (a * (1.0 - e.pow(2.0) / 4.0 - 3 * e.pow(4.0) / 64.0 - 5 * e.pow(6.0) / 256.0))
            val e1 = (1.0 - (1.0 - e.pow(2.0)).pow(0.5)) / (1.0 + (1.0 - e.pow(2.0)).pow(0.5))
            val j1 = 3 * e1 / 2 - 27 * e1.pow(3.0) / 32.0
            val j2 = 21 * e1.pow(2.0) / 16 - 55 * e1.pow(4.0) / 32.0
            val j3 = 151 * e1.pow(3.0) / 96.0
            val j4 = 1097 * e1.pow(4.0) / 512.0
            val fp =
                mu + j1 * sin(2 * mu) + j2 * sin(4 * mu) + j3 * sin(6 * mu) + j4 * sin(
                    8 * mu
                )

            // Calculate Latitude and Longitude
            val e2 = (e * a / b).pow(2.0)
            val c1 = (e2 * cos(fp)).pow(2.0)
            val t1 = tan(fp).pow(2.0)
            val r1 = a * (1 - e.pow(2.0)) / (1 - e.pow(2.0) * sin(fp).pow(2.0)).pow(3.0 / 2.0)
            val n1 = a / (1 - e.pow(2.0) * sin(fp).pow(2.0)).pow(0.5)
            val D = x / (n1 * k0)

            // 計算緯度
            val q1 = n1 * tan(fp) / r1
            val q2 = D.pow(2.0) / 2.0
            val q3 =
                (5 + 3 * t1 + 10 * c1 - 4 * c1.pow(2.0) - 9 * e2) * D.pow(4.0) / 24.0
            val q4 =
                (61 + 90 * t1 + 298 * c1 + 45 * t1.pow(2.0) - 3 * c1.pow(2.0) - 252 * e2) * D.pow(
                    6.0
                ) / 720.0
            var lat = fp - q1 * (q2 - q3 + q4)

            // 計算經度
            val q6 = (1 + 2 * t1 + c1) * D.pow(3.0) / 6
            val q7 =
                (5 - 2 * c1 + 28 * t1 - 3 * c1.pow(2.0) + 8 * e2 + 24 * t1.pow(2.0)) * D.pow(5.0) / 120.0
            var lon = lon0 + (D - q6 + q7) / cos(fp)
            lat = lat * 180 / Math.PI //緯
            lon = lon * 180 / Math.PI //經
            return "$lat,$lon"
        }
    }

    // 給WGS84經緯度度分秒轉成TWD97坐標
    fun lonLatToTWD97(lonD: Int, lonM: Int, lonS: Int, latD: Int, latM: Int, latS: Int): String {
        val radianLon = lonD.toDouble() + lonM.toDouble() / 60 + lonS.toDouble() / 3600
        val radianLat = latD.toDouble() + latM.toDouble() / 60 + latS.toDouble() / 3600
        return calLonLatToTWD97(radianLon, radianLat)
    }

    // 給WGS84經緯度弧度轉成TWD97坐標
    fun lonLatToWED97(radianLon: Double, radianLat: Double): String {
        return calLonLatToTWD97(radianLon, radianLat)
    }

    // 給TWD97坐標 轉成 WGS84 度分秒字串  (type1傳度分秒   2傳弧度)
    fun twd97ToLonLat(x: Double, y: Double, type: Int): String {
        var lonLat = ""
        if (type == 1) {
            val answer = calTWD97ToLonLat(x, y).split(",".toRegex()).toTypedArray()
            val lonDValue = answer[0].toInt()
            val lonMValue = ((answer[0].toDouble() - lonDValue) * 60).toInt()
            val lonSValue = (((answer[0].toDouble() - lonDValue) * 60 - lonMValue) * 60).toInt()
            val latDValue = answer[1].toInt()
            val latMValue = ((answer[1].toDouble() - latDValue) * 60).toInt()
            val latSValue = (((answer[1].toDouble() - latDValue) * 60 - latMValue) * 60).toInt()
            lonLat =
                lonDValue.toString() + "度" + lonMValue + "分" + lonSValue + "秒," + latDValue + "度" + latMValue + "分" + latSValue + "秒,"
        } else if (type == 2) {
            lonLat = calTWD97ToLonLat(x, y)
        }
        return lonLat
    }

    private fun calLonLatToTWD97(lon: Double, lat: Double): String {
        val lon = lon / 180 * Math.PI
        val lat = lat / 180 * Math.PI

        //---------------------------------------------------------
        val e = (1 - b.pow(2.0) / a.pow(2.0)).pow(0.5)
        val e2 = e.pow(2.0) / (1 - e.pow(2.0))
        val n = (a - b) / (a + b)
        val nu = a / (1 - e.pow(2.0) * sin(lat).pow(2.0)).pow(0.5)
        val p = lon - lon0
        val A =
            a * (1 - n + 5 / 4 * (n.pow(2.0) - n.pow(3.0)) + 81 / 64 * (n.pow(4.0) - n.pow(5.0)))
        val B =
            3 * a * n / 2.0 * (1 - n + 7 / 8.0 * (n.pow(2.0) - n.pow(3.0)) + 55 / 64.0 * (n.pow(4.0) - n.pow(
                5.0
            )))
        val C = 15 * a * n.pow(2.0) / 16.0 * (1 - n + 3 / 4.0 * (n.pow(2.0) - n.pow(3.0)))
        val D =
            35 * a * n.pow(3.0) / 48.0 * (1 - n + 11 / 16.0 * (n.pow(2.0) - n.pow(3.0)))
        val E = 315 * a * n.pow(4.0) / 51.0 * (1 - n)
        val S =
            A * lat - B * sin(2 * lat) + C * sin(4 * lat) - D * sin(6 * lat) + E * sin(
                8 * lat
            )

        //計算Y值
        val k1 = S * k0
        val k2 = k0 * nu * sin(2 * lat) / 4.0
        val k3 =
            k0 * nu * sin(lat) * cos(lat).pow(3.0) / 24.0 * (5 - tan(lat).pow(2.0) + 9 * e2 * cos(
                lat
            ).pow(2.0) + 4 * e2.pow(2.0) * cos(lat).pow(4.0))
        val y = k1 + k2 * p.pow(2.0) + k3 * p.pow(4.0)

        //計算X值
        val k4 = k0 * nu * cos(lat)
        val k5 =
            k0 * nu * cos(lat).pow(3.0) / 6.0 * (1 - tan(lat).pow(2.0) + e2 * cos(lat).pow(2.0))
        val x = k4 * p + k5 * p.pow(3.0) + dx
        return "$x,$y"
    }
}