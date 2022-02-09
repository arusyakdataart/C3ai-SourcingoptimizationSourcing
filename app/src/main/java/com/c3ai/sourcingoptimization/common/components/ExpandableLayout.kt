package com.c3ai.sourcingoptimization.common.components

import android.annotation.SuppressLint
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.stringResource
import com.c3ai.sourcingoptimization.R

private const val EXPAND_ANIMATION_DURATION = 300
private const val COLLAPSE_ANIMATION_DURATION = 300
private const val FADE_IN_ANIMATION_DURATION = 350
private const val FADE_OUT_ANIMATION_DURATION = 300

@SuppressLint("UnusedTransitionTargetStateParameter")
@Composable
fun ExpandableLayout(
    modifier: Modifier = Modifier,
    expanded: Boolean,
    onClick: () -> Unit,
    content: @Composable (() -> Unit),
    expandableContent: @Composable (() -> Unit),
) {
    val transitionState = remember {
        MutableTransitionState(expanded).apply {
            targetState = !expanded
        }
    }
    val transition = updateTransition(transitionState, label = "")
//    val cardBgColor by transition.animateColor({
//        tween(durationMillis = EXPAND_ANIMATION_DURATION)
//    }) {
//        if (expanded) cardExpandedBackgroundColor else cardCollapsedBackgroundColor
//    }
//    val cardPaddingHorizontal by transition.animateDp({
//        tween(durationMillis = EXPAND_ANIMATION_DURATION)
//    }) {
//        if (expanded) 48.dp else 24.dp
//    }
//    val cardElevation by transition.animateDp({
//        tween(durationMillis = EXPAND_ANIMATION_DURATION)
//    }) {
//        if (expanded) 24.dp else 4.dp
//    }
//    val cardRoundedCorners by transition.animateDp({
//        tween(
//            durationMillis = EXPAND_ANIMATION_DURATION,
//            easing = FastOutSlowInEasing
//        )
//    }) {
//        if (expanded) 0.dp else 16.dp
//    }
    val arrowRotationDegree by transition.animateFloat({
        tween(durationMillis = EXPAND_ANIMATION_DURATION)
    }, label = "ArrowRotationAnimation") {
        if (expanded) 180f else 0f
    }

    Column(
        modifier = modifier.clickable(onClick = onClick),
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            content()
            Icon(
                imageVector = Icons.Filled.ExpandMore,
                contentDescription = stringResource(R.string.cd_expandable_arrow),
                modifier = Modifier.rotate(arrowRotationDegree),
            )
        }
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
    initialVisibility: Boolean = false,
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