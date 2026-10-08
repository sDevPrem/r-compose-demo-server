package com.sdevprem

import androidx.compose.remote.creation.dsl.*
import androidx.compose.remote.creation.modifiers.RoundedRectShape
import com.sdevprem.FestiveColors.ctaBGColor
import java.awt.Color

/**
 * URLs are deliberately kept in one place. Replace these with the final campaign assets when
 * they are available; the cards are already sized to use a centre crop.
 */
private object FestiveImages {
    const val campaign = "https://i.ibb.co/QwtGBpj/Durga-Puja.png"
    const val earningCalendar = "https://i.ibb.co/ymYdmhLr/336-X152-2.png"
    const val coach = "https://i.ibb.co/ymYdmhLr/336-X152-2.png"
    const val referrals = "https://i.ibb.co/ymYdmhLr/336-X152-2.png"
    const val bazaarProduct = "https://i.ibb.co/tTw6fHL8/Frame-2147205260.png"
    const val riderStory = "https://i.ibb.co/KphPWW3C/Landing-Page.png"
    const val bottomImg = "https://i.ibb.co/S4zR7Ydr/Frame-2147227287.png"
}

private object FestiveColors {
    const val page = 0xFFC40D12.toInt()
    const val darkSection = 0xFFA7070A.toInt()
    const val card = 0xFF870004.toInt()
    const val cream = 0xFFFFF5E9.toInt()
    const val gold = 0xFFFFD35D.toInt()
    const val blue = 0xFF1B8BFF.toInt()
    const val ctaBGColor = 0xFF750708.toInt()
}

/**
 * Remote Compose alpha20 writes dp/sp wrappers as raw values. Convert the design values here
 * instead of relying on the player's incomplete density conversion support.
 */
private class FestiveScale(val widthPx: Int, density: Float) {
    private val widthScale = widthPx / (360f * density)
    private val pixelsPerDesignUnit = density * widthScale

    fun dp(value: Float): RcDp = (value * pixelsPerDesignUnit).rdp
    fun sp(value: Float): RcSp = (value * pixelsPerDesignUnit).rsp
}

fun festivePage(width: Int, height: Int, density: Float): ByteArray {
    val scale = FestiveScale(width, density)
    return prepareDoc(width, height, density) {
        Box(
            modifier = Modifier.fillMaxSize().background(FestiveColors.page),
            horizontal = RcHorizontalPositioning.Center,
            vertical = RcVerticalPositioning.Bottom,
        ) {
            Column {
                // Only this column scrolls; the primary action below remains visible.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .verticalScroll(),
                    horizontal = RcHorizontalPositioning.Center,
                    vertical = RcColumnVerticalPositioning.Top,
                ) {

                    // Existing campaign header content is intentionally kept intact.
                    Image(
                        remoteBitmapUrlWithBounds(url = FestiveImages.campaign, 720, 656),
                        modifier = Modifier.fillParentMaxWidth().height((width / (720 / 656)).rdp),
                        contentScale = RcContentScale.Crop,
                    )

                    Text(
                        text = "Earn up to ₹25,000",
                        weight = RcWeight.of(600f),
                        color = Color.white.rgb,
                        textAlign = RcTextAlign.Center,
                        fontSize = 84.rsp,
                    )

                    Text(
                        text = "this Durga Puja",
                        weight = RcWeight.of(600f),
                        color = Color.white.rgb,
                        textAlign = RcTextAlign.Center,
                        fontSize = 64.rsp,
                    )

                    Text(
                        text = "from 10th - 20th Oct",
                        weight = RcWeight.of(400f),
                        color = Color.white.rgb,
                        textAlign = RcTextAlign.Center,
                        fontSize = 44.rsp,
                        modifier = Modifier.padding(top = 8.rdp.value),
                    )

                    Text(
                        text = "OFFER STARTS IN",
                        weight = RcWeight.of(400f),
                        color = Color.white.rgb,
                        textAlign = RcTextAlign.Center,
                        fontSize = 50.rsp,
                        modifier = Modifier.padding(top = scale.dp(44f).value, bottom = scale.dp(12f).value),
                    )

                    liveCountdown(scale)
                    progressSummary(scale)
                    earningSection(scale)
                    benefitsSection(scale)
                    bazaarSection(scale)
                    riderStoriesSection(scale)
                    footerImage(scale)
                }
                stickyBookGigsButton(scale)
            }
        }
    }
}

/** A player-side countdown that resets at midnight and refreshes every second. */
private fun RcColumnScope.liveCountdown(scale: FestiveScale) {
    val secondsRemaining = 86399f - (continuousSeconds() % 86400f)
    val hours = floor(secondsRemaining / 3600f)
    val minutes = floor((secondsRemaining % 3600f) / 60f)
    val seconds = floor(secondsRemaining % 60f)

    Row(
        modifier = Modifier.padding(top = scale.dp(6f).value),
        horizontal = RcRowHorizontalPositioning.Center,
        vertical = RcVerticalPositioning.Center,
    ) {
        countdownUnit(hours, "HRS", scale)
        countdownSeparator(scale)
        countdownUnit(minutes, "MINS", scale)
        countdownSeparator(scale)
        countdownUnit(seconds, "SECS", scale)
    }
}

private fun RcRowScope.countdownUnit(value: RcFloat, label: String, scale: FestiveScale) {
    Column(horizontal = RcHorizontalPositioning.Center) {
        Box(
            modifier = Modifier
                .width(scale.dp(48f))
                .wrapContentHeight()
                .clip(rounded(scale.dp(8f).value))
                .background(FestiveColors.cream)
                .padding(top = scale.dp(12f).value, bottom = scale.dp(12f).value),
            horizontal = RcHorizontalPositioning.Center,
            vertical = RcVerticalPositioning.Center,
        ) {
            Text(
                text = value.format(whole = 2, decimal = 0),
                color = FestiveColors.card,
                fontSize = scale.sp(26f),
                fontWeight = RcFontWeight.Bold,
                textAlign = RcTextAlign.Center,
            )
        }
        Text(
            text = label,
            color = Color.white.rgb,
            fontSize = scale.sp(10f),
            fontWeight = RcFontWeight.Medium,
            textAlign = RcTextAlign.Center,
            modifier = Modifier.padding(top = scale.dp(4f).value),
        )
    }
}

private fun RcRowScope.countdownSeparator(scale: FestiveScale) {
    Text(
        text = ":",
        color = FestiveColors.cream,
        fontSize = scale.sp(22f),
        fontWeight = RcFontWeight.Bold,
        modifier = Modifier.padding(
            start = scale.dp(4f).value,
            end = scale.dp(4f).value,
            bottom = scale.dp(16f).value,
        ),
    )
}

private fun RcColumnScope.progressSummary(scale: FestiveScale) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = scale.dp(12f).value,
                end = scale.dp(12f).value,
                top = scale.dp(22f).value,
            )
            .wrapContentHeight()
            .padding(bottom = scale.dp(16f).value)
            .clip(rounded(scale.dp(16f).value))
            .background(FestiveColors.card)
            .border(scale.dp(1f).value, scale.dp(16f).value, FestiveColors.gold, 2)
            .padding(bottom = scale.dp(16f).value),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = scale.dp(16f).value, end = scale.dp(16f).value),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontal = RcRowHorizontalPositioning.SpaceBetween,
                vertical = RcVerticalPositioning.Center,
            ) {
                Text(
                    text = "Your progress",
                    color = FestiveColors.cream,
                    fontSize = scale.sp(20f),
                    fontWeight = RcFontWeight.Bold,
                )
                Text(
                    text = "›",
                    color = FestiveColors.cream,
                    fontSize = scale.sp(42f),
                    fontWeight = RcFontWeight.Bold,
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                vertical = RcVerticalPositioning.Top,
            ) {
                Text(
                    text = "₹0",
                    color = Color.white.rgb,
                    fontSize = scale.sp(10f),
                    fontWeight = RcFontWeight.Normal,
                    modifier = Modifier.padding(top = scale.dp(2f).value, end = scale.dp(8f).value),
                )

                Column(
                    modifier = Modifier.weight(1f),
                    horizontal = RcHorizontalPositioning.Center,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(scale.dp(12f))
                            .clip(rounded(scale.dp(10f).value))
                            .background(0xFFB80E13.toInt()),
                    ) {
                        Box(
                            modifier = Modifier
                                .fillParentMaxWidth(0.5f)
                                .fillMaxHeight()
                                .background(Color.white.rgb),
                        )
                    }
                }

                Text(
                    text = "₹25,000",
                    color = Color.white.rgb,
                    fontSize = scale.sp(10f),
                    fontWeight = RcFontWeight.Normal,
                    modifier = Modifier.padding(top = scale.dp(2f).value, start = scale.dp(8f).value),
                )
            }
        }
    }
}

private fun RcColumnScope.earningSection(scale: FestiveScale) {
    sectionTitle("❖  How to earn more  ❖", top = 28f, scale = scale)
    promoCard(
        title = "Check high-earning\ndays and plan your\nwork",
        action = "View calendar",
        imageUrl = FestiveImages.earningCalendar,
        scale = scale,
        topPadding = scale.dp(12f).value,
    )
    promoCard(
        title = "Ask coach how to\nboost your earnings",
        action = "☎  Call coach",
        imageUrl = FestiveImages.coach,
        scale = scale,
        topPadding = 0f,
    )
}

private fun RcColumnScope.benefitsSection(scale: FestiveScale) {
    sectionTitle("❖  More benefits  ❖", top = 28f, scale = scale)
    promoCard(
        title = "This festive season,\nearn up to ₹75,000\nwith referrals",
        action = "Refer now",
        imageUrl = FestiveImages.referrals,
        scale = scale,
        topPadding = scale.dp(12f).value,
    )
}

private fun RcColumnScope.sectionTitle(text: String, top: Float, scale: FestiveScale) {
    Text(
        text = text,
        color = FestiveColors.gold,
        fontSize = scale.sp(20f),
        fontWeight = RcFontWeight.Bold,
        textAlign = RcTextAlign.Center,
        modifier = Modifier.padding(top = scale.dp(top).value, bottom = scale.dp(12f).value),
    )
}

private fun RcColumnScope.promoCard(
    title: String,
    action: String,
    imageUrl: String,
    scale: FestiveScale,
    topPadding: Float,
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = scale.dp(12f).value,
                end = scale.dp(12f).value,
                top = topPadding,
                bottom = scale.dp(16f).value
            )
            .height((scale.widthPx / (2.21f)))
            .clip(rounded(scale.dp(12f).value))
            .background(FestiveColors.card)
            .border(scale.dp(1f).value, scale.dp(12f).value, FestiveColors.gold, 2)
    ) {
        // The crop intentionally shares the same source until distinct campaign art is supplied.
        Image(
            remoteBitmapUrlWithBounds(imageUrl, 672, 304),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .clip(rounded(scale.dp(12f).value)),
            contentScale = RcContentScale.Crop,
        )
        Column(
            modifier = Modifier.padding(
                top = scale.dp(12f).value,
                bottom = scale.dp(12f).value,
                start = scale.dp(12f).value
            ).fillMaxWidth(0.6f)
        ) {
            Text(
                text = title,
                color = Color.white.rgb,
                fontSize = scale.sp(18f),
                fontWeight = RcFontWeight.Bold,
                maxLines = 3,
            )
            Spacer(Modifier.weight(1f))
            actionPill(action, scale)
        }
    }
    Spacer(Modifier.height(scale.dp(12f)))
}

private fun RcColumnScope.actionPill(label: String, scale: FestiveScale) {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .clip(rounded(scale.dp(50f).value))
            .background(Color.white.rgb),
        horizontal = RcHorizontalPositioning.Center,
        vertical = RcVerticalPositioning.Center,
    ) {
        Text(
            label,
            color = FestiveColors.card,
            fontSize = scale.sp(12f),
            fontWeight = RcFontWeight.Bold,
            modifier = Modifier.padding(
                start = scale.dp(12f).value,
                end = scale.dp(12f).value,
                top = scale.dp(8f).value,
                bottom = scale.dp(8f).value
            ),
        )
    }
}

private fun RcColumnScope.bazaarSection(scale: FestiveScale) {
    bazaarWaveSeparator(scale, isTopEdge = true)
    Column(
        modifier = Modifier
            .fillParentMaxWidth()
            .background(FestiveColors.darkSection)
            .padding(scale.dp(10f).value),
    ) {
        Row(
            modifier = Modifier.fillParentMaxWidth()
                .padding(bottom = scale.dp(16f).value),
            horizontal = RcRowHorizontalPositioning.SpaceBetween,
            vertical = RcVerticalPositioning.Center,
        ) {
            Text(
                "Discounts on bazaar",
                color = Color.white.rgb,
                fontSize = scale.sp(18f),
                fontWeight = RcFontWeight.Bold
            )
            Text("See all  →", color = Color.white.rgb, fontSize = scale.sp(12f), fontWeight = RcFontWeight.Bold)
        }
        Row(modifier = Modifier.horizontalScroll()) {
            Spacer(Modifier.width(scale.dp(8f)))
            bazaarProduct("Noise Earbuds", scale)
            Spacer(Modifier.width(scale.dp(8f)))
            bazaarProduct("Noise Nirvana", scale)
            Spacer(Modifier.width(scale.dp(8f)))
            bazaarProduct("Boat Nirvana", scale)
        }
    }
    bazaarWaveSeparator(scale, isTopEdge = false)
}

private fun RcColumnScope.bazaarWaveSeparator(scale: FestiveScale, isTopEdge: Boolean) {
    Box(modifier = Modifier.fillMaxWidth().height(scale.dp(24f))) {
        Canvas(Modifier.fillMaxSize()) {
            val width = scale.widthPx.toFloat()
            val height = scale.dp(24f).value
            val waveY = height * 0.5f
            val amplitude = scale.dp(6f).value
            val waveLength = scale.dp(48f).value.coerceAtLeast(1f)

            applyPaint {
                setStyle(RcPaintStyle.Fill)
                setColor(FestiveColors.darkSection)
            }
            fun waveLine(path: RcDynamicPath) {
                var x = 0f
                var crestUp = true
                while (x < width) {
                    val nextX = (x + waveLength / 2f).coerceAtMost(width)
                    path.quadTo(
                        x + waveLength / 4f,
                        waveY + if (crestUp) -amplitude else amplitude,
                        nextX,
                        waveY,
                    )
                    x = nextX
                    crestUp = !crestUp
                }
            }

            if (isTopEdge) {
                val bottomPath = remotePath(0f, waveY)
                waveLine(bottomPath)
                bottomPath.lineTo(width, height)
                bottomPath.lineTo(0f, height)
                bottomPath.close()
                drawPath(bottomPath.getPath())
            } else {
                val topPath = remotePath(0f, 0f)
                topPath.lineTo(0f, waveY)
                waveLine(topPath)
                topPath.lineTo(width, 0f)
                topPath.close()
                drawPath(topPath.getPath())
            }
        }
    }
}

private fun RcRowScope.bazaarProduct(name: String, scale: FestiveScale) {
    Column(modifier = Modifier.width(scale.dp(120f))) {
        Image(
            remoteBitmapUrlWithBounds(FestiveImages.bazaarProduct, 120, 120),
            modifier = Modifier
                .width(scale.dp(120f))
                .height(scale.dp(120f))
                .clip(rounded(scale.dp(7f).value))
                .background(Color.white.rgb),
            contentScale = RcContentScale.Crop,
        )
        Text(
            name,
            color = Color.white.rgb,
            fontSize = scale.sp(15f),
            fontWeight = RcFontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(top = scale.dp(5f).value),
            overflow = RcTextOverflow.Ellipsis
        )
        Row(
            vertical = RcVerticalPositioning.Bottom
        ) {
            Text(
                "₹199",
                color = Color.white.rgb,
                fontSize = scale.sp(13f),
                fontWeight = RcFontWeight.Bold,
                modifier = Modifier.padding(top = scale.dp(3f).value)
            )
            Text(
                "₹250",
                color = Color.white.rgb,
                fontSize = scale.sp(10f),
                fontWeight = RcFontWeight.Bold,
                modifier = Modifier.padding(top = scale.dp(3f).value, start = scale.dp(2f).value)
            )
        }
        Text(
            "✥ 50% OFF",
            color = FestiveColors.blue,
            fontSize = scale.sp(12f),
            fontWeight = RcFontWeight.Bold,
            modifier = Modifier.padding(top = scale.dp(2f).value)
        )
    }
}

private fun RcColumnScope.riderStoriesSection(scale: FestiveScale) {
    sectionTitle("❖  Rider stories  ❖", top = 28f, scale = scale)
    Row(modifier = Modifier.fillParentMaxWidth()) {
        Spacer(Modifier.width(scale.dp(8f)))
        riderStory("Navratri 🌸\npe crazy offers", scale)
        Spacer(Modifier.width(scale.dp(8f)))
        riderStory("24,000 kamayein\niss tyohar ✨", scale)
        Spacer(Modifier.width(scale.dp(8f)))
        riderStory("Zomato pe\nNavratri special", scale)
    }
}

private fun RcRowScope.riderStory(title: String, scale: FestiveScale) {
    Box(
        modifier = Modifier
            .width(scale.dp(94f))
            .height(scale.dp(94f / 0.56f))
            .clip(rounded(scale.dp(9f).value)),
    ) {
        Image(
            remoteBitmapUrlWithBounds(FestiveImages.riderStory, 254, 452),
            modifier = Modifier.fillMaxSize(),
            contentScale = RcContentScale.Crop,
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
        ) {
            Spacer(Modifier.weight(1f))
            Text(
                text = title,
                color = Color.white.rgb,
                fontSize = scale.sp(10f),
                fontWeight = RcFontWeight.Medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = scale.dp(5f).value,
                        end = scale.dp(5f).value,
                        bottom = scale.dp(8f).value
                    ),
                maxLines = 2,
                overflow = RcTextOverflow.Ellipsis
            )
        }
    }
}

private fun RcColumnScope.stickyBookGigsButton(scale: FestiveScale) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(ctaBGColor)
            .padding(scale.dp(12f))
            .fillMaxWidth()
            .clip(rounded(scale.dp(6f).value))
            .background(Color.white.rgb)
            .padding(scale.dp(12f)),
        horizontal = RcHorizontalPositioning.Center,
        vertical = RcVerticalPositioning.Center,
    ) {
        Text("Book Gigs", color = 0xFF171717.toInt(), fontSize = scale.sp(20f), fontWeight = RcFontWeight.Bold)
    }
}

private fun rounded(radius: Float) = RoundedRectShape(radius, radius, radius, radius)

private fun RcScope.footerImage(scale: FestiveScale) {
    Image(
        remoteBitmapUrlWithBounds(FestiveImages.bottomImg, 720, 280),
        modifier = Modifier
            .padding(top = scale.dp(24f).value)
            .fillMaxWidth()
            .height(scale.widthPx/2.57f),
    )
}
