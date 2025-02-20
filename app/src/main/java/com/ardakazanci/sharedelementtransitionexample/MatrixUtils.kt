package com.ardakazanci.sharedelementtransitionexample

import androidx.compose.ui.graphics.ColorMatrix
import kotlin.math.cos
import kotlin.math.sin

object MatrixUtils {

    fun partialHueRotateMatrix(angle: Float, fraction: Float = 1f): ColorMatrix {
        val hueM = hueRotateMatrix(angle)
        val identityM = ColorMatrix()
        val hueArray = hueM.values
        val identityArray = identityM.values
        val out = FloatArray(20)
        for (i in out.indices) {
            out[i] = identityArray[i] + fraction * (hueArray[i] - identityArray[i])
        }
        return ColorMatrix(out)
    }

    fun hueRotateMatrix(angle: Float): ColorMatrix {
        val rad = Math.toRadians(angle.toDouble())
        val cosVal = cos(rad)
        val sinVal = sin(rad)
        val lumR = 0.213f
        val lumG = 0.715f
        val lumB = 0.072f
        return ColorMatrix(
            floatArrayOf(
                (lumR + cosVal * (1 - lumR) + sinVal * (-lumR)).toFloat(),
                (lumG + cosVal * (-lumG) + sinVal * (-lumG)).toFloat(),
                (lumB + cosVal * (-lumB) + sinVal * (1 - lumB)).toFloat(),
                0f, 0f,
                (lumR + cosVal * (-lumR) + sinVal * 0.143f).toFloat(),
                (lumG + cosVal * (1 - lumG) + sinVal * 0.14f).toFloat(),
                (lumB + cosVal * (-lumB) + sinVal * (-0.283f)).toFloat(),
                0f, 0f,
                (lumR + cosVal * (-lumR) + sinVal * (-(1 - lumR))).toFloat(),
                (lumG + cosVal * (-lumG) + sinVal * lumG).toFloat(),
                (lumB + cosVal * (1 - lumB) + sinVal * lumB).toFloat(),
                0f, 0f, 0f, 0f, 0f,
                1f, 0f
            )
        )
    }
}