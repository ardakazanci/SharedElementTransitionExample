package com.ardakazanci.sharedelementtransitionexample

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ardakazanci.sharedelementtransitionexample.DummyPokeData.pokemons
import com.ardakazanci.sharedelementtransitionexample.MatrixUtils.partialHueRotateMatrix
import com.ardakazanci.sharedelementtransitionexample.ui.theme.SharedElementTransitionExampleTheme
import kotlin.math.cos
import kotlin.math.sin

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SharedElementTransitionExampleTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PokemonPhotoGallery()
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalSharedTransitionApi::class)
@Preview(showBackground = true)
@Composable
fun PokemonPhotoGallery() {

    var selectedPoke by remember { mutableStateOf<Poke?>(null) }

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(pokemons.size) { index ->
                val poke = pokemons[index]
                AnimatedVisibility(
                    visible = poke != selectedPoke,
                    enter = fadeIn() + scaleIn(),
                    exit = fadeOut() + scaleOut(),
                    modifier = Modifier.animateItem()
                ) {
                    Box(
                        modifier = Modifier
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "${poke.id}-bounds"),
                                animatedVisibilityScope = this,
                                clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(12.dp))
                            )
                            .background(Color.White, RoundedCornerShape(12.dp))
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        PokemonCardItem(
                            poke = poke,
                            modifier = Modifier.sharedElement(
                                state = rememberSharedContentState(key = poke.id),
                                animatedVisibilityScope = this@AnimatedVisibility
                            ),
                            onClick = { selectedPoke = poke },
                            applyHue = false
                        )
                    }
                }
            }
        }
        PokemonDetailCardItem(
            poke = selectedPoke,
            onDismiss = { selectedPoke = null }
        )
    }
}

@Composable
fun PokemonCardItem(
    poke: Poke,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    applyHue: Boolean = false
) {
    val infiniteTransition = rememberInfiniteTransition()

    val shadowModifier = Modifier
        .shadow(4.dp, RoundedCornerShape(8.dp))
        .background(Color.White)

    val hueMatrix = if (applyHue) {
        val hueRotation by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 360f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 4000, easing = LinearEasing),
                repeatMode = RepeatMode.Restart
            )
        )
        partialHueRotateMatrix(hueRotation, fraction = 0.3f)
    } else {
        ColorMatrix()
    }

    val brush = if (applyHue) {
        val holographicProgress by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(durationMillis = 2000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )
        val offsetX = with(LocalDensity.current) { 300.dp.toPx() } * holographicProgress
        // Holog-Color Graph.
        Brush.linearGradient(
            colors = listOf(
                Color(0xFFFF0000),
                Color(0xFFFFFF00),
                Color(0xFF00FF00),
                Color(0xFF00FFFF),
                Color(0xFF0000FF),
                Color(0xFFFF00FF),
                Color(0xFFFF0000)
            ),
            start = Offset(offsetX, 0f),
            end = Offset(offsetX + 600f, 600f),
            tileMode = androidx.compose.ui.graphics.TileMode.Mirror
        )
    } else {
        null
    }



    Column(
        modifier = modifier.clickable { onClick() }
    ) {
        Box(modifier = shadowModifier) {
            Image(
                painter = painterResource(id = poke.image),
                contentDescription = poke.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .let { baseModifier ->
                        if (applyHue && brush != null) {
                            baseModifier.drawWithContent {
                                drawContent()
                                drawRect(
                                    brush = brush,
                                    alpha = 0.3f,
                                    blendMode = BlendMode.Overlay
                                )
                            }
                        } else baseModifier
                    },
                colorFilter = if (applyHue) ColorFilter.colorMatrix(hueMatrix) else null
            )
        }
        Text(
            text = poke.name,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(8.dp)
        )
    }
}


@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.PokemonDetailCardItem(
    poke: Poke?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit
) {
    AnimatedContent(
        modifier = modifier,
        targetState = poke,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "PokemonDetailCardItem"
    ) { poke ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (poke != null) {

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onDismiss() }
                        .background(Color.Black.copy(alpha = 0.6f))
                )

                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "${poke.id}-bounds"),
                            animatedVisibilityScope = this@AnimatedContent,
                            clipInOverlayDuringTransition = OverlayClip(RoundedCornerShape(14.dp))
                        )
                        .background(Color.White, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp))
                ) {
                    PokemonCardItem(
                        poke = poke,
                        modifier = Modifier.sharedElement(
                            state = rememberSharedContentState(key = poke.id),
                            animatedVisibilityScope = this@AnimatedContent
                        ),
                        onClick = onDismiss,
                        applyHue = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = poke.description,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        TextButton(onClick = onDismiss) {
                            Text("Close")
                        }
                    }
                }
            }
        }
    }
}

