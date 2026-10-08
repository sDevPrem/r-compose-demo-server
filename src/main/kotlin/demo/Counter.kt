package com.sdevprem.demo

import androidx.compose.remote.creation.dsl.Modifier
import androidx.compose.remote.creation.dsl.RcActionScope
import androidx.compose.remote.creation.dsl.RcFloat
import androidx.compose.remote.creation.dsl.RcHorizontalPositioning
import androidx.compose.remote.creation.dsl.RcInteger
import androidx.compose.remote.creation.dsl.RcVerticalPositioning
import androidx.compose.remote.creation.dsl.background
import androidx.compose.remote.creation.dsl.fillMaxSize
import androidx.compose.remote.creation.dsl.onClick
import androidx.compose.remote.creation.dsl.padding
import androidx.compose.remote.creation.dsl.wrapContentHeight
import androidx.compose.remote.creation.dsl.wrapContentSize
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