package com.juniperphoton.flipperlayoutjetpackcompose.flipper

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer

/**
 * @author JuniperPhoton
 * @since 2021-08-09
 */
enum class FlipperLayoutSide {
    Front,
    Back
}

fun FlipperLayoutSide.flip(): FlipperLayoutSide {
    return when (this) {
        FlipperLayoutSide.Front -> FlipperLayoutSide.Back
        else -> FlipperLayoutSide.Front
    }
}

private data class AnimationItem(
    val side: FlipperLayoutSide,
    val content: @Composable () -> Unit
)

/**
 * A layout uses flipping animation on [flipperSide] changed.
 *
 * You can specify the animation using [animationSpec] like duration. And describe your
 * composable content using [content], which you can use different composable content
 * based on the [flipperSide] in the [content] block.
 *
 * The caller side should like this:
 *
 * ```
 * var flipperSide by remember {
 *    mutableStateOf(FlipperLayoutSide.Front)
 * }
 *
 * FlipperContent(flipperSide) {
 *    flipperSide = flipperSide.flip()
 * }
 *
 * @Composable
 * fun FlipperContent(
 *     flipperSide: FlipperLayoutSide,
 *     onFlipperSideChanged: () -> Unit
 * ) {
 *     val interactionSource = remember { MutableInteractionSource() }
 *
 *     FlipperLayout(
 *         modifier = Modifier.clickable(interactionSource = interactionSource, indication = null) {
 *             onFlipperSideChanged()
 *         },
 *         flipperSide = flipperSide,
 *         animationSpec = tween(300)
 *     ) {
 *        // your content
 *     }
 * }
 * ```
 */
@Composable
fun FlipperLayout(
    flipperSide: FlipperLayoutSide,
    modifier: Modifier = Modifier,
    animationSpec: FiniteAnimationSpec<Float> = tween(),
    content: @Composable (FlipperLayoutSide) -> Unit
) {
    val items = remember { mutableStateListOf<AnimationItem>() }

    val transitionState = remember { MutableTransitionState(flipperSide) }
    val targetChanged = (flipperSide != transitionState.targetState)
    transitionState.targetState = flipperSide

    val transition = updateTransition(transitionState, label = "")

    if (targetChanged || items.isEmpty()) {
        // Only manipulate the list when the state is changed, or in the first run.
        items.clear()
        FlipperLayoutSide.values().mapTo(items) { s ->
            AnimationItem(s) {
                val rotationX by transition.animateFloat(
                    transitionSpec = { animationSpec }, label = ""
                ) { side ->
                    when (s) {
                        FlipperLayoutSide.Front -> {
                            if (side == s) 0f else 180f
                        }
                        FlipperLayoutSide.Back -> {
                            if (side == s) 0f else -180f
                        }
                    }
                }

                val alpha by transition.animateFloat(
                    transitionSpec = { animationSpec },
                    label = ""
                ) { side ->
                    if (s == side) {
                        1f
                    } else {
                        0f
                    }
                }

                Box(Modifier.graphicsLayer {
                    this.rotationX = rotationX
                    this.alpha = alpha
                }) {
                    content(s)
                }
            }
        }
    } else if (transitionState.currentState == transitionState.targetState) {
        // Remove all the intermediate items from the list once the animation is finished.
        items.removeAll { it.side != transitionState.targetState }
    }

    Box(modifier) {
        items.forEach {
            key(it.side) {
                it.content()
            }
        }
    }
}