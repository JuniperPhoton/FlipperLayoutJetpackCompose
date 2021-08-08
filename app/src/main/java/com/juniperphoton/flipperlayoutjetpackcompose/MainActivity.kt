package com.juniperphoton.flipperlayoutjetpackcompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.juniperphoton.flipperlayoutjetpackcompose.flipper.FlipperLayout
import com.juniperphoton.flipperlayoutjetpackcompose.flipper.FlipperLayoutSide
import com.juniperphoton.flipperlayoutjetpackcompose.flipper.flip
import com.juniperphoton.flipperlayoutjetpackcompose.ui.theme.FlipperLayoutJetpackComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FlipperLayoutJetpackComposeTheme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
                    Scaffold(topBar = {
                        TopAppBar(title = {
                            Text("FlipperLayout Sample")
                        })
                    }) {
                        FlipperLayoutSample()
                    }
                }
            }
        }
    }
}

@Composable
fun FlipperLayoutSample() {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            var flipperSide by remember {
                mutableStateOf(FlipperLayoutSide.Front)
            }

            Spacer(modifier = Modifier.height(12.dp))

            FlipperContent(flipperSide) {
                flipperSide = flipperSide.flip()
            }

            Spacer(modifier = Modifier.height(12.dp))
            Row {
                Text("Click the view to flip content")
                Spacer(modifier = Modifier.width(20.dp))
                Switch(checked = flipperSide == FlipperLayoutSide.Back, onCheckedChange = {
                    flipperSide = flipperSide.flip()
                })
            }

            Spacer(modifier = Modifier.height(12.dp))
        }
    }

}

@Composable
fun FlipperContent(
    flipperSide: FlipperLayoutSide,
    onFlipperSideChanged: () -> Unit
) {
    FlipperLayout(
        modifier = Modifier.clickable {
            onFlipperSideChanged()
        },
        flipperSide = flipperSide,
        animationSpec = tween(300)
    ) {
        val text = if (it == FlipperLayoutSide.Front) "Front" else "Back"
        Box(
            Modifier
                .fillMaxWidth(0.5f)
                .aspectRatio(1f)
        ) {
            Image(
                painter = painterResource(
                    if (it == FlipperLayoutSide.Front) {
                        R.drawable.image0
                    } else {
                        R.drawable.image1
                    }
                ),
                contentDescription = text,
                contentScale = ContentScale.Crop
            )
            Text(
                text.uppercase(),
                modifier = Modifier.padding(12.dp),
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    FlipperLayoutJetpackComposeTheme {
        FlipperLayoutSample()
    }
}