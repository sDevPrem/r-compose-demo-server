package com.sdevprem.demo

import androidx.compose.remote.creation.dsl.Modifier
import androidx.compose.remote.creation.dsl.RcPaintStyle
import androidx.compose.remote.creation.dsl.RcScope
import androidx.compose.remote.creation.dsl.RcStrokeCap
import androidx.compose.remote.creation.dsl.fillMaxSize
import androidx.compose.remote.creation.dsl.setStrokeCap
import androidx.compose.remote.creation.dsl.setStyle
import com.sdevprem.common.prepareDoc

fun clock(width: Int, height: Int, density: Float) =
    prepareDoc(width, height, density) {
        clock()
    }

private fun RcScope.clock() {
    Box(Modifier.fillMaxSize()) {
        Canvas(Modifier.fillMaxSize()) {
            val w = componentWidth()
            val h = componentHeight()
            val cx = (w / 2f)
            val cy = (h / 2f)
            val rad = min(cx, cy)
            val strokeWidth = (rad / 6f)

            applyPaint {
                setStyle(RcPaintStyle.Stroke)
                setColor(0xFF333333.toInt())
                setStrokeWidth(rad * 0.03f)
            }
            drawCircle(cx, cy, rad * 0.98f)

            val textSize = rad / 5f
            applyPaint {
                setColor(0xFFEEEEEE.toInt())
                setTextSize(textSize)
            }

            applyPaint {
                setColor(0xFF0000FF.toInt())
                setStrokeWidth(strokeWidth)
                setStrokeCap(RcStrokeCap.Round)
            }

            val hrHand = (hour() + (minutes() % 60f) / 60f) * 30f
            save {
                rotate(hrHand, cx, cy)
                drawLine(cx, cy, cx, cy - rad / 3f)
            }

            applyPaint {
                setColor(0xFF00FF00.toInt())
                setStrokeWidth(strokeWidth)
                setStrokeCap(RcStrokeCap.Round)
            }
            save {
                rotate(minutes() * 6f, cx, cy)
                drawLine(cx, cy, cx, cy - rad * 0.6f)
            }

            save {
                rotate(seconds() * 6f, cx, cy)
                val radius = rad * 0.1f
                applyPaint {
                    setStyle(RcPaintStyle.Fill)
                    setColor(0xFFFF0000.toInt())
                }
                drawCircle(cx, cy - rad + (2f.rf * radius), radius)

                rotate(70.rf, cx, cy)
                applyPaint { setColor(0xFF000000.toInt()) }
            }
        }
    }
}
