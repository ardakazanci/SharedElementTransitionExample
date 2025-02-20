package com.ardakazanci.sharedelementtransitionexample

import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test

class PokemonGalleryTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testPokemonPhotoGalleryDisplaysPokemonNames() {
        composeTestRule.setContent {
            PokemonPhotoGallery()
        }

        composeTestRule.onNodeWithText("Jigglypuff").assertExists()
        composeTestRule.onNodeWithText("Psyduck").assertExists()
        composeTestRule.onNodeWithText("Machoke").assertExists()
        composeTestRule.onNodeWithText("Slowpoke").assertExists()
    }

    @Test
    fun testPokemonDetailAppearsOnClickAndDismiss() {
        composeTestRule.setContent {
            PokemonPhotoGallery()
        }

        val jigglypuffNode = composeTestRule.onAllNodesWithText("Jigglypuff").onFirst()
        jigglypuffNode.performClick()


        composeTestRule.onNodeWithText(
            "When its huge eyes waver, it sings a mysteriously soothing melody that lulls its enemies to sleep."
        ).assertExists()


        composeTestRule.onNodeWithText("Close").assertExists().performClick()


        composeTestRule.onNodeWithText(
            "When its huge eyes waver, it sings a mysteriously soothing melody that lulls its enemies to sleep."
        ).assertDoesNotExist()
    }

    @Test
    fun testPartialHueRotateMatrixIdentityWhenFractionZero() {

        val angle = 45f
        val matrix = partialHueRotateMatrix(angle, fraction = 0f)
        val identityMatrix = ColorMatrix()


        matrix.values.zip(identityMatrix.values).forEach { (a, b) ->
            assertEquals(b, a, 0.001f)
        }
    }

    @Test
    fun testHueRotateMatrixAtZeroDegrees() {

        val matrix = hueRotateMatrix(0f)
        val identityMatrix = ColorMatrix()

        matrix.values.zip(identityMatrix.values).forEach { (a, b) ->
            assertEquals(b, a, 0.001f)
        }
    }
}