package cl.duoc.bitacoraescolar.ui.dashboard

import android.view.MotionEvent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInteropFilter

/** Realce de cursor y pulsación sin consumir el evento click del componente. */
@OptIn(ExperimentalComposeUiApi::class)
fun Modifier.interactiveFeedback(): Modifier = composed {
    val interaction = remember { MutableInteractionSource() }
    val hovered by interaction.collectIsHoveredAsState()
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (pressed) 0.94f else if (hovered) 1.035f else 1f,
        animationSpec = spring(dampingRatio = 0.58f, stiffness = 520f),
        label = "escalaClick"
    )
    val alpha by animateFloatAsState(
        targetValue = if (pressed) 0.82f else 1f,
        label = "brilloClick"
    )
    this.hoverable(interaction)
        .pointerInteropFilter { event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN -> pressed = true
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> pressed = false
            }
            false
        }
        .graphicsLayer { scaleX = scale; scaleY = scale; this.alpha = alpha }
}
