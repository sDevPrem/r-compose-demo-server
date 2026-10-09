package com.sdevprem.demo

import androidx.compose.remote.creation.dsl.*
import com.sdevprem.common.ScreenScale
import com.sdevprem.common.prepareDoc
import java.awt.Color

fun counter(width: Int, height: Int, density: Float) =
    prepareDoc(width, height, density) {
        val scale = ScreenScale(width, density)
        val counterState = remoteNamedInteger("counter", 0)
        val counter = remoteNamedFloat("c", 0f)
        StateLayout(counterState) {
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                horizontal = RcHorizontalPositioning.Center,
                vertical = RcVerticalPositioning.Center
            ) {
                Text(
                    remoteText("hello: ") + counter.format(2, 0),
                    fontSize = scale.sp(16f),
                    modifier = Modifier
                        .wrapContentSize()
                        .background(color = Color.gray.rgb)
                        .padding(scale.dp(16f))
                        .onClick { incrementCounter(counter) }
                )
            }
        }
    }

fun RcActionScope.incrementCounter(counter: RcFloat) {
    setValue(counter, counter + 1f)
}