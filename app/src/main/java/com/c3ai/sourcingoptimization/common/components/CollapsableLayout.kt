package com.c3ai.sourcingoptimization.common.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

private const val EXPAND_ANIMATION_DURATION = 300
private const val COLLAPSE_ANIMATION_DURATION = 300
private const val FADE_IN_ANIMATION_DURATION = 350
private const val FADE_OUT_ANIMATION_DURATION = 300

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun CollapsableLayout(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    expandableContent: @Composable (() -> Unit),
) {
    Column(
        modifier = modifier
    ) {
        ExpandableContent(
            visible = expanded,
            initialVisibility = expanded,
        ) { expandableContent() }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ExpandableContent(
    modifier: Modifier = Modifier,
    visible: Boolean = true,
    initialVisibility: Boolean = true,
    content: @Composable AnimatedVisibilityScope.() -> Unit
) {
    val enterFadeIn = remember {
        fadeIn(
            animationSpec = TweenSpec(
                durationMillis = FADE_IN_ANIMATION_DURATION,
                easing = FastOutLinearInEasing
            )
        )
    }
    val enterExpand = remember {
        expandVertically(animationSpec = tween(EXPAND_ANIMATION_DURATION))
    }
    val exitFadeOut = remember {
        fadeOut(
            animationSpec = TweenSpec(
                durationMillis = FADE_OUT_ANIMATION_DURATION,
                easing = LinearOutSlowInEasing
            )
        )
    }
    val exitCollapse = remember {
        shrinkVertically(animationSpec = tween(COLLAPSE_ANIMATION_DURATION))
    }
    val transitionState = remember { MutableTransitionState(initialState = initialVisibility) }
        .apply { targetState = visible }
    AnimatedVisibility(
        visibleState = transitionState,
        modifier = modifier,
        enter = enterExpand + enterFadeIn,
        exit = exitCollapse + exitFadeOut,
        content = content
    )
}