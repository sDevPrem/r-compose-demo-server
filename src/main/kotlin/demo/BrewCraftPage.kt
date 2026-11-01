package com.sdevprem.demo

import androidx.compose.remote.creation.dsl.*
import androidx.compose.remote.creation.modifiers.RoundedRectShape
import com.sdevprem.common.ScreenScale
import com.sdevprem.common.prepareDoc
import com.sdevprem.common.remoteBitmapUrlWithBounds

/**
 * BrewCraft – coffee & bakery discovery page.
 *
 * Layout (top to bottom):
 *  - fixed top bar (logo, "ORDER AHEAD" tag, search / notifications / avatar)
 *  - scrolling body: category chips, seasonal drop hero with a live countdown, roastery
 *    sessions, daily pairings carousel, "Behind the bar" video tiles
 *  - sticky pickup bar + bottom navigation
 *
 * All sizes are written in the 360dp design grid and converted through [ScreenScale].
 */

/** Free Unsplash photos. Swap these for the real BrewCraft assets when available. */
private object BrewImages {
    private fun unsplash(id: String, w: Int, h: Int) =
        "https://images.unsplash.com/$id?fm=jpg&q=75&w=$w&h=$h&fit=crop&crop=entropy"

    const val HeroW = 720
    const val HeroH = 416
    const val PairingW = 480
    const val PairingH = 270
    const val VideoW = 240
    const val VideoH = 424

    val hero = unsplash("photo-1504469089401-14f795f6ddee", HeroW, HeroH)
    val morningPairing = unsplash("photo-1708723860637-07d7a52fa31d", PairingW, PairingH)
    val coldBrewPairing = unsplash("photo-1486322147346-2b4d93ea6e6f", PairingW, PairingH)
    val latteArt = unsplash("photo-1507915135761-41a0a222c709", VideoW, VideoH)
    val beanSourcing = unsplash("photo-1633627360580-4e9594300341", VideoW, VideoH)
    val lamination = unsplash("photo-1480215825332-69732b261cc2", VideoW, VideoH)
}

private object BrewColors {
    const val page = 0xFFFEF8F5.toInt()
    const val surface = 0xFFFFFFFF.toInt()
    const val primary = 0xFF8F4620.toInt()
    const val primarySoft = 0xFFF9DCCF.toInt()
    const val savePill = 0xFFF9DDC6.toInt()
    const val chip = 0xFFF3ECE8.toInt()
    const val panel = 0xFFF9F2ED.toInt()
    const val track = 0xFFEDE3DC.toInt()
    const val divider = 0xFFF0E7E1.toInt()
    const val ink = 0xFF1E1B18.toInt()
    const val inkMuted = 0xFF6E6763.toInt()
    const val inkSubtle = 0xFF8C8580.toInt()
    const val navInactive = 0xFF4A403A.toInt()
    const val notificationDot = 0xFFD9661F.toInt()
    const val darkTag = 0xE61E1B18.toInt()
    const val white = 0xFFFFFFFF.toInt()
}

private object BrewActionIds {
    const val Search = 2_001
    const val Notifications = 2_002
    const val Profile = 2_003
    const val ReserveBatch = 2_010
    const val ViewAllSessions = 2_020
    const val ReserveMasterclass = 2_021
    const val ViewTastingFlight = 2_022
    const val AddMorningPairing = 2_030
    const val AddColdBrewPairing = 2_031
    const val WatchAll = 2_040
    const val PlayLatteArt = 2_041
    const val PlayBeanSourcing = 2_042
    const val PlayLamination = 2_043
    const val OrderAhead = 2_050
    const val NavDiscover = 2_060
    const val NavMenu = 2_061
    const val NavRewards = 2_062
    const val NavProfile = 2_063
}

private val Categories = listOf("Seasonal Drops", "Espresso Bar", "Cold Brew", "Artisan Bakes", "Merch")

/** Seasonal drop reservations close this long after the page is generated. */
private const val ReservationWindowSeconds = 1L * 86_400 + 18 * 3_600 + 41 * 60 + 17

fun brewCraftPage(width: Int, height: Int, density: Float): ByteArray {
    val scale = ScreenScale(width, density)
    val closesAtMillis = System.currentTimeMillis() + ReservationWindowSeconds * 1_000
    return prepareDoc(width, height, density) {
        Column(Modifier.fillMaxSize().background(BrewColors.page)) {
            topBar(scale)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(),
            ) {
                categoryChips(scale)
                seasonalDropCard(scale, closesAtMillis)
                roasterySessions(scale)
                dailyPairings(scale)
                behindTheBar(scale)
                Spacer(Modifier.height(scale.dp(24f)))
            }
            pickupBar(scale)
            bottomNavigation(scale)
        }
    }
}

// region Top bar

private fun RcColumnScope.topBar(scale: ScreenScale) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrewColors.surface)
            .padding(
                start = scale.px(16f),
                end = scale.px(16f),
                top = scale.px(14f),
                bottom = scale.px(14f),
            ),
        vertical = RcVerticalPositioning.Center,
    ) {
        Text(
            text = "BREWCRAFT",
            color = BrewColors.primary,
            fontSize = scale.sp(19f),
            fontWeight = RcFontWeight.Bold,
        )
        Box(
            modifier = Modifier
                .padding(start = scale.px(8f))
                .clip(rounded(scale.px(4f)))
                .background(BrewColors.primarySoft)
                .padding(
                    start = scale.px(6f),
                    end = scale.px(6f),
                    top = scale.px(2f),
                    bottom = scale.px(2f),
                ),
        ) {
            Text(
                text = "ORDER AHEAD",
                color = BrewColors.primary,
                fontSize = scale.sp(9f),
                fontWeight = RcFontWeight.Bold,
            )
        }
        Spacer(Modifier.weight(1f))
        clickableIcon(scale, 24f, BrewActionIds.Search, "Search") { s -> drawSearchIcon(s, BrewColors.ink) }
        Spacer(Modifier.width(scale.dp(20f)))
        clickableIcon(scale, 24f, BrewActionIds.Notifications, "Notifications") { s ->
            drawBellIcon(s, BrewColors.ink)
        }
        Spacer(Modifier.width(scale.dp(20f)))
        avatar(scale)
    }
    divider(scale)
}

private fun RcRowScope.avatar(scale: ScreenScale) {
    val size = scale.px(34f)
    val metadata = remoteText("Profile")
    Box(
        modifier = Modifier
            .size(size)
            .clip(rounded(size / 2f))
            .background(BrewColors.primary)
            .onClick { hostAction(BrewActionIds.Profile, metadata) },
        horizontal = RcHorizontalPositioning.Center,
        vertical = RcVerticalPositioning.Center,
    ) {
        icon(scale, 18f) { s -> drawPersonIcon(s, BrewColors.white) }
    }
}

// endregion

// region Category chips

/** Each chip switches [StateLayout] to the variant where that chip is selected. */
private fun RcColumnScope.categoryChips(scale: ScreenScale) {
    val selected = remoteNamedInteger("brewcraft_category", 0)
    StateLayout(selected, Modifier.fillMaxWidth()) {
        Categories.indices.forEach { selectedIndex ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll()
                    .padding(top = scale.px(12f), bottom = scale.px(4f)),
                vertical = RcVerticalPositioning.Center,
            ) {
                Spacer(Modifier.width(scale.dp(16f)))
                Categories.forEachIndexed { index, label ->
                    val isSelected = index == selectedIndex
                    Box(
                        modifier = Modifier
                            .clip(rounded(scale.px(20f)))
                            .background(if (isSelected) BrewColors.primary else BrewColors.chip)
                            .onClick { setValue(selected, index) }
                            .padding(
                                start = scale.px(16f),
                                end = scale.px(16f),
                                top = scale.px(9f),
                                bottom = scale.px(9f),
                            ),
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) BrewColors.white else BrewColors.ink,
                            fontSize = scale.sp(13f),
                            fontWeight = RcFontWeight.Medium,
                        )
                    }
                    Spacer(Modifier.width(scale.dp(8f)))
                }
                Spacer(Modifier.width(scale.dp(8f)))
            }
        }
    }
}

// endregion

// region Seasonal drop hero

private fun RcColumnScope.seasonalDropCard(scale: ScreenScale, closesAtMillis: Long) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = scale.px(16f), end = scale.px(16f), top = scale.px(12f))
            .clip(rounded(scale.px(14f)))
            .background(BrewColors.surface),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(scale.contentWidthPx(16f) * BrewImages.HeroH / BrewImages.HeroW),
        ) {
            Image(
                remoteBitmapUrlWithBounds(BrewImages.hero, BrewImages.HeroW, BrewImages.HeroH),
                modifier = Modifier.fillMaxSize(),
                contentScale = RcContentScale.Crop,
            )
            Row(
                modifier = Modifier.fillMaxWidth().padding(scale.px(12f)),
                horizontal = RcRowHorizontalPositioning.SpaceBetween,
                vertical = RcVerticalPositioning.Center,
            ) {
                Box(
                    modifier = Modifier
                        .clip(rounded(scale.px(4f)))
                        .background(BrewColors.darkTag)
                        .padding(
                            start = scale.px(8f),
                            end = scale.px(8f),
                            top = scale.px(4f),
                            bottom = scale.px(4f),
                        ),
                ) {
                    Text(
                        text = "LIMITED BATCH",
                        color = BrewColors.white,
                        fontSize = scale.sp(10f),
                        fontWeight = RcFontWeight.Bold,
                    )
                }
                Row(
                    modifier = Modifier
                        .clip(rounded(scale.px(4f)))
                        .background(BrewColors.surface)
                        .padding(
                            start = scale.px(8f),
                            end = scale.px(8f),
                            top = scale.px(4f),
                            bottom = scale.px(4f),
                        ),
                    vertical = RcVerticalPositioning.Center,
                ) {
                    icon(scale, 11f) { s -> drawFlameIcon(s, BrewColors.primary) }
                    Text(
                        text = "RARE MICROLOT",
                        color = BrewColors.primary,
                        fontSize = scale.sp(10f),
                        fontWeight = RcFontWeight.Bold,
                        modifier = Modifier.padding(start = scale.px(4f)),
                    )
                }
            }
        }
        Box(Modifier.fillMaxWidth().height(scale.dp(2f)).background(BrewColors.primary))

        Column(modifier = Modifier.fillMaxWidth().padding(scale.px(16f))) {
            Text(
                text = "Ethiopia Yirgacheffe",
                color = BrewColors.ink,
                fontSize = scale.sp(20f),
                fontWeight = RcFontWeight.Medium,
            )
            Text(
                text = "Honey Process · Floral Jasmine & Ripe Peach Notes",
                color = BrewColors.inkMuted,
                fontSize = scale.sp(12f),
                maxLines = 1,
                overflow = RcTextOverflow.Ellipsis,
                modifier = Modifier.padding(top = scale.px(4f)),
            )
            reservationCountdown(scale, closesAtMillis)
            batchAllocation(scale, claimedFraction = 0.68f)
            reserveButton(scale)
        }
    }
}

/**
 * Counts down to [closesAtMillis] on the device clock. `continuousSeconds()` only loops within
 * the hour, so a multi-day countdown needs a time attribute against an absolute timestamp.
 */
private fun RcColumnScope.reservationCountdown(scale: ScreenScale, closesAtMillis: Long) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = scale.px(14f))
            .clip(rounded(scale.px(8f)))
            .background(BrewColors.panel)
            .padding(top = scale.px(12f), bottom = scale.px(12f)),
        horizontal = RcHorizontalPositioning.Center,
    ) {
        // TimeAttribute is a paint operation (it also schedules a repaint every second), so it
        // has to live in drawn content. A 1px canvas keeps it in the paint pass.
        val closesAt = remoteLong(closesAtMillis)
        var secondsFromNow: RcFloat? = null
        Canvas(Modifier.size(1f)) {
            secondsFromNow = timeAttribute(closesAt, RcTimeAttr.TimeFromNowSec)
        }
        val remaining = max(floor(secondsFromNow!!), 0f)
        val days = floor(remaining / 86_400f)
        val hours = floor((remaining % 86_400f) / 3_600f)
        val minutes = floor((remaining % 3_600f) / 60f)
        val seconds = remaining % 60f

        Text(
            text = "RESERVATION WINDOW CLOSES IN",
            color = BrewColors.primary,
            fontSize = scale.sp(10f),
            fontWeight = RcFontWeight.SemiBold,
        )
        Row(
            modifier = Modifier.padding(top = scale.px(10f)),
            vertical = RcVerticalPositioning.Top,
        ) {
            countdownUnit(days, "DAYS", BrewColors.ink, scale)
            countdownSeparator(scale)
            countdownUnit(hours, "HRS", BrewColors.ink, scale)
            countdownSeparator(scale)
            countdownUnit(minutes, "MINS", BrewColors.ink, scale)
            countdownSeparator(scale)
            countdownUnit(seconds, "SECS", BrewColors.primary, scale)
        }
    }
}

private fun RcRowScope.countdownUnit(value: RcFloat, label: String, valueColor: Int, scale: ScreenScale) {
    Column(horizontal = RcHorizontalPositioning.Center) {
        Box(
            modifier = Modifier
                .width(scale.dp(44f))
                .height(scale.dp(40f))
                .clip(rounded(scale.px(6f)))
                .background(BrewColors.surface),
            horizontal = RcHorizontalPositioning.Center,
            vertical = RcVerticalPositioning.Center,
        ) {
            Text(
                text = value.format(whole = 2, decimal = 0),
                color = valueColor,
                fontSize = scale.sp(20f),
                fontWeight = RcFontWeight.Medium,
                textAlign = RcTextAlign.Center,
            )
        }
        Text(
            text = label,
            color = BrewColors.inkMuted,
            fontSize = scale.sp(9f),
            fontWeight = RcFontWeight.Medium,
            modifier = Modifier.padding(top = scale.px(6f)),
        )
    }
}

private fun RcRowScope.countdownSeparator(scale: ScreenScale) {
    Box(
        modifier = Modifier.width(scale.dp(18f)).height(scale.dp(40f)),
        horizontal = RcHorizontalPositioning.Center,
        vertical = RcVerticalPositioning.Center,
    ) {
        Text(
            text = ":",
            color = BrewColors.inkMuted,
            fontSize = scale.sp(18f),
            fontWeight = RcFontWeight.Medium,
        )
    }
}

private fun RcColumnScope.batchAllocation(scale: ScreenScale, claimedFraction: Float) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = scale.px(16f)),
        vertical = RcVerticalPositioning.Center,
    ) {
        dot(scale, 6f, BrewColors.primary)
        Text(
            text = "Batch Allocation Filling Fast",
            color = BrewColors.ink,
            fontSize = scale.sp(11f),
            fontWeight = RcFontWeight.SemiBold,
            modifier = Modifier.padding(start = scale.px(6f)),
        )
        Spacer(Modifier.weight(1f))
        Text(
            text = "${(claimedFraction * 100).toInt()}% Claimed",
            color = BrewColors.primary,
            fontSize = scale.sp(11f),
            fontWeight = RcFontWeight.SemiBold,
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = scale.px(8f))
            .height(scale.dp(7f))
            .clip(rounded(scale.px(4f)))
            .background(BrewColors.track),
    ) {
        Box(
            modifier = Modifier
                .fillParentMaxWidth(claimedFraction)
                .fillMaxHeight()
                .clip(rounded(scale.px(4f)))
                .background(BrewColors.primary),
        )
    }
}

private fun RcColumnScope.reserveButton(scale: ScreenScale) {
    val metadata = remoteText("Reserve Ethiopia Yirgacheffe batch bag")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = scale.px(18f))
            .height(scale.dp(46f))
            .clip(rounded(scale.px(8f)))
            .background(BrewColors.primary)
            .onClick { hostAction(BrewActionIds.ReserveBatch, metadata) },
        horizontal = RcRowHorizontalPositioning.Center,
        vertical = RcVerticalPositioning.Center,
    ) {
        Text(
            text = "RESERVE BATCH BAG",
            color = BrewColors.white,
            fontSize = scale.sp(15f),
            fontWeight = RcFontWeight.Medium,
        )
        Text(
            text = "(250g · $22.00)",
            color = 0xE6FFFFFF.toInt(),
            fontSize = scale.sp(12f),
            modifier = Modifier.padding(start = scale.px(8f)),
        )
    }
}

// endregion

// region Roastery sessions

private fun RcColumnScope.roasterySessions(scale: ScreenScale) {
    sectionHeader(scale, "Roastery Sessions", top = 28f) {
        textAction(scale, "VIEW ALL", BrewActionIds.ViewAllSessions)
    }
    sessionCard(
        scale = scale,
        kind = "WORKSHOP",
        kindColor = BrewColors.primary,
        title = "Barista Pour-Over Masterclass",
        details = "Sat, Oct 28 · 10:00 AM · Downtown Roastery",
        action = "Reserve",
        primaryAction = true,
        actionId = BrewActionIds.ReserveMasterclass,
    )
    sessionCard(
        scale = scale,
        kind = "TASTING FLIGHT",
        kindColor = BrewColors.inkMuted,
        title = "Origin Tasting Flight Experience",
        details = "Sun, Oct 29 · 3:00 PM · Cupping Lab 02",
        action = "View",
        primaryAction = false,
        actionId = BrewActionIds.ViewTastingFlight,
    )
}

private fun RcColumnScope.sessionCard(
    scale: ScreenScale,
    kind: String,
    kindColor: Int,
    title: String,
    details: String,
    action: String,
    primaryAction: Boolean,
    actionId: Int,
) {
    val metadata = remoteText("$action: $title")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = scale.px(16f), end = scale.px(16f), bottom = scale.px(10f))
            .clip(rounded(scale.px(12f)))
            .background(BrewColors.surface)
            .padding(scale.px(16f)),
        vertical = RcVerticalPositioning.Center,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = kind,
                color = kindColor,
                fontSize = scale.sp(10f),
                fontWeight = RcFontWeight.SemiBold,
            )
            Text(
                text = title,
                color = BrewColors.ink,
                fontSize = scale.sp(15f),
                fontWeight = RcFontWeight.Medium,
                maxLines = 1,
                overflow = RcTextOverflow.Ellipsis,
                modifier = Modifier.padding(top = scale.px(6f)),
            )
            Text(
                text = details,
                color = BrewColors.inkMuted,
                fontSize = scale.sp(12f),
                maxLines = 1,
                overflow = RcTextOverflow.Ellipsis,
                modifier = Modifier.padding(top = scale.px(3f)),
            )
        }
        Box(
            modifier = Modifier
                .padding(start = scale.px(12f))
                .width(scale.dp(68f))
                .height(scale.dp(34f))
                .clip(rounded(scale.px(8f)))
                .background(if (primaryAction) BrewColors.primary else BrewColors.chip)
                .onClick { hostAction(actionId, metadata) },
            horizontal = RcHorizontalPositioning.Center,
            vertical = RcVerticalPositioning.Center,
        ) {
            Text(
                text = action,
                color = if (primaryAction) BrewColors.white else BrewColors.ink,
                fontSize = scale.sp(12f),
                fontWeight = RcFontWeight.SemiBold,
            )
        }
    }
}

// endregion

// region Daily pairings

private fun RcColumnScope.dailyPairings(scale: ScreenScale) {
    sectionHeader(scale, "Daily Pairings", subtitle = "Freshly baked at 5:30 AM", top = 18f) {
        Box(
            modifier = Modifier
                .clip(rounded(scale.px(4f)))
                .background(BrewColors.savePill)
                .padding(
                    start = scale.px(8f),
                    end = scale.px(8f),
                    top = scale.px(3f),
                    bottom = scale.px(3f),
                ),
        ) {
            Text(
                text = "SAVE 15%",
                color = BrewColors.primary,
                fontSize = scale.sp(10f),
                fontWeight = RcFontWeight.SemiBold,
            )
        }
    }
    Row(modifier = Modifier.fillMaxWidth().horizontalScroll()) {
        Spacer(Modifier.width(scale.dp(16f)))
        pairingCard(
            scale = scale,
            name = "Morning Pairing Combo",
            price = "$8.50",
            imageUrl = BrewImages.morningPairing,
            quantityKey = "morning_pairing",
            actionId = BrewActionIds.AddMorningPairing,
        )
        Spacer(Modifier.width(scale.dp(12f)))
        pairingCard(
            scale = scale,
            name = "Cold Brew & Pastry",
            price = "$16.00",
            imageUrl = BrewImages.coldBrewPairing,
            quantityKey = "cold_brew_pastry",
            actionId = BrewActionIds.AddColdBrewPairing,
        )
        Spacer(Modifier.width(scale.dp(16f)))
    }
}

private fun RcRowScope.pairingCard(
    scale: ScreenScale,
    name: String,
    price: String,
    imageUrl: String,
    quantityKey: String,
    actionId: Int,
) {
    val metadata = remoteText("Added $name")
    val cardWidth = scale.dp(236f)
    Column(
        modifier = Modifier
            .width(cardWidth)
            .clip(rounded(scale.px(12f)))
            .background(BrewColors.surface),
    ) {
        Image(
            remoteBitmapUrlWithBounds(imageUrl, BrewImages.PairingW, BrewImages.PairingH),
            modifier = Modifier
                .width(cardWidth)
                .height(cardWidth.value * BrewImages.PairingH / BrewImages.PairingW),
            contentScale = RcContentScale.Crop,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(scale.px(12f)),
            vertical = RcVerticalPositioning.Center,
        ) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = name,
                    color = BrewColors.ink,
                    fontSize = scale.sp(15f),
                    fontWeight = RcFontWeight.Medium,
                    maxLines = 1,
                    overflow = RcTextOverflow.Ellipsis,
                )
                Text(
                    text = price,
                    color = BrewColors.primary,
                    fontSize = scale.sp(13f),
                    fontWeight = RcFontWeight.SemiBold,
                    modifier = Modifier.padding(top = scale.px(4f)),
                )
            }
            quantityControl(scale, quantityKey, actionId, metadata)
        }
    }
}

/**
 * "+" button that turns into a "−  count  +" stepper once the item is added.
 *
 * The quantity lives in the player as a named integer, so taps update it locally without a
 * server round trip; the host can read/set it by name (`brewcraft_qty_<key>`). Which control
 * shows is derived from the quantity itself via visibility expressions, so pressing "−" at 1
 * collapses back to the "+" button automatically.
 */
private fun RcRowScope.quantityControl(
    scale: ScreenScale,
    quantityKey: String,
    addActionId: Int,
    addMetadata: RcText,
) {
    val quantity = remoteNamedInteger("brewcraft_qty_$quantityKey", 0)
    // Expressions are emitted once here and evaluated by the player at tap time.
    val increment = (quantity + 1).min(MaxQuantity)
    val decrement = (quantity - 1).max(0)
    // Component visibility is an integer: 0 = GONE, 1 = VISIBLE. Both controls stay in the Row
    // and the player toggles them as the quantity changes.
    val stepperVisibility = quantity.min(1)
    val addVisibility = -stepperVisibility + 1
    // Integer → text without a float round-trip: look the count up in a small string table.
    val quantityLabels = remoteArrayOf(*Array(MaxQuantity + 1) { it.toString() })
    val quantityText = textLookup(quantityLabels, quantity)

    val height = scale.px(34f)
    Spacer(Modifier.width(scale.dp(8f)))
    // Add button (quantity == 0)
    Box(
        modifier = Modifier
            .visibility(addVisibility)
            .size(height)
            .clip(rounded(height / 2f))
            .background(BrewColors.primary)
            .onClick {
                setValue(quantity, 1)
                hostAction(addActionId, addMetadata)
            },
        horizontal = RcHorizontalPositioning.Center,
        vertical = RcVerticalPositioning.Center,
    ) {
        icon(scale, 14f) { s -> drawPlusIcon(s, BrewColors.white) }
    }
    // − count + stepper (quantity >= 1)
    Row(
        modifier = Modifier
            .visibility(stepperVisibility)
            .width(scale.dp(100f))
            .height(height)
            .clip(rounded(height / 2f))
            .background(BrewColors.primary),
        vertical = RcVerticalPositioning.Center,
    ) {
        stepperButton(
            scale = scale,
            size = height,
            drawIcon = { s -> drawMinusIcon(s, BrewColors.white) },
            onTap = { setValue(quantity, decrement) },
        )
        Box(
            modifier = Modifier.weight(1f),
            horizontal = RcHorizontalPositioning.Center,
            vertical = RcVerticalPositioning.Center,
        ) {
            Text(
                text = quantityText,
                color = BrewColors.white,
                fontSize = scale.sp(14f),
                fontWeight = RcFontWeight.SemiBold,
                textAlign = RcTextAlign.Center,
            )
        }
        stepperButton(
            scale = scale,
            size = height,
            drawIcon = { s -> drawPlusIcon(s, BrewColors.white) },
            onTap = { setValue(quantity, increment) },
        )
    }
}

private const val MaxQuantity = 20

private fun RcRowScope.stepperButton(
    scale: ScreenScale,
    size: Float,
    drawIcon: RcCanvasScope.(size: Float) -> Unit,
    onTap: RcActionScope.() -> Unit,
) {
    Box(
        modifier = Modifier.size(size).onClick(onTap),
        horizontal = RcHorizontalPositioning.Center,
        vertical = RcVerticalPositioning.Center,
    ) {
        icon(scale, 12f, drawIcon)
    }
}

// endregion

// region Behind the bar

private fun RcColumnScope.behindTheBar(scale: ScreenScale) {
    sectionHeader(scale, "Behind The Bar", top = 28f) {
        textAction(scale, "WATCH", BrewActionIds.WatchAll, color = BrewColors.ink)
    }
    val gap = 8f
    val tileWidthPx = (scale.contentWidthPx(16f) - 2 * scale.px(gap)) / 3f
    val tileHeightPx = tileWidthPx * BrewImages.VideoH / BrewImages.VideoW
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = scale.px(16f), end = scale.px(16f)),
    ) {
        videoTile(scale, "Latte Art Pour", BrewImages.latteArt, BrewActionIds.PlayLatteArt, tileWidthPx, tileHeightPx)
        Spacer(Modifier.width(scale.dp(gap)))
        videoTile(scale, "Bean Sourcing", BrewImages.beanSourcing, BrewActionIds.PlayBeanSourcing, tileWidthPx, tileHeightPx)
        Spacer(Modifier.width(scale.dp(gap)))
        videoTile(scale, "Lamination Craft", BrewImages.lamination, BrewActionIds.PlayLamination, tileWidthPx, tileHeightPx)
    }
}

private fun RcRowScope.videoTile(
    scale: ScreenScale,
    title: String,
    imageUrl: String,
    actionId: Int,
    widthPx: Float,
    heightPx: Float,
) {
    val metadata = remoteText("Play: $title")
    Box(
        modifier = Modifier
            .width(widthPx)
            .height(heightPx)
            .clip(rounded(scale.px(12f)))
            .onClick { hostAction(actionId, metadata) },
    ) {
        Image(
            remoteBitmapUrlWithBounds(imageUrl, BrewImages.VideoW, BrewImages.VideoH),
            modifier = Modifier.fillMaxSize(),
            contentScale = RcContentScale.Crop,
        )
        // Bottom scrim so the white caption stays readable on any frame.
        Canvas(Modifier.fillMaxSize()) {
            val scrimTop = heightPx * 0.5f
            applyPaint {
                setStyle(RcPaintStyle.Fill)
                setLinearGradient(
                    0f, scrimTop, 0f, heightPx,
                    intArrayOf(0x00000000, 0xB3000000.toInt()),
                    floatArrayOf(0f, 1f),
                    0,
                )
            }
            drawRect(0f, scrimTop, widthPx, heightPx)
        }
        Column(Modifier.fillMaxSize().padding(scale.px(8f))) {
            Row(Modifier.fillMaxWidth(), horizontal = RcRowHorizontalPositioning.End) {
                icon(scale, 22f) { s -> drawPlayBadge(s) }
            }
            Spacer(Modifier.weight(1f))
            Text(
                text = title,
                color = BrewColors.white,
                fontSize = scale.sp(11f),
                fontWeight = RcFontWeight.SemiBold,
                maxLines = 1,
                overflow = RcTextOverflow.Ellipsis,
            )
        }
    }
}

// endregion

// region Sticky bottom: pickup bar + navigation

private fun RcColumnScope.pickupBar(scale: ScreenScale) {
    divider(scale)
    val metadata = remoteText("Order ahead · Downtown Roastery")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrewColors.surface)
            .padding(
                start = scale.px(16f),
                end = scale.px(16f),
                top = scale.px(10f),
                bottom = scale.px(10f),
            ),
        vertical = RcVerticalPositioning.Center,
    ) {
        dot(scale, 10f, BrewColors.primary)
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = scale.px(12f)),
        ) {
            Text(
                text = "Pickup Ready · 8-12 min",
                color = BrewColors.ink,
                fontSize = scale.sp(13f),
                fontWeight = RcFontWeight.SemiBold,
            )
            Text(
                text = "Downtown Roastery · 0.4 mi",
                color = BrewColors.inkMuted,
                fontSize = scale.sp(12f),
                modifier = Modifier.padding(top = scale.px(2f)),
            )
        }
        Box(
            modifier = Modifier
                .width(scale.dp(120f))
                .height(scale.dp(38f))
                .clip(rounded(scale.px(8f)))
                .background(BrewColors.primary)
                .onClick { hostAction(BrewActionIds.OrderAhead, metadata) },
            horizontal = RcHorizontalPositioning.Center,
            vertical = RcVerticalPositioning.Center,
        ) {
            Text(
                text = "ORDER AHEAD",
                color = BrewColors.white,
                fontSize = scale.sp(13f),
                fontWeight = RcFontWeight.Medium,
            )
        }
    }
}

private fun RcColumnScope.bottomNavigation(scale: ScreenScale) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(BrewColors.surface)
            .padding(top = scale.px(8f), bottom = scale.px(10f)),
        vertical = RcVerticalPositioning.Center,
    ) {
        navItem(scale, "Discover", BrewActionIds.NavDiscover, selected = true) { s, c -> drawCupIcon(s, c) }
        navItem(scale, "Menu", BrewActionIds.NavMenu) { s, c -> drawUtensilsIcon(s, c) }
        navItem(scale, "Rewards", BrewActionIds.NavRewards) { s, c -> drawTagIcon(s, c) }
        navItem(scale, "Profile", BrewActionIds.NavProfile) { s, c -> drawProfileIcon(s, c) }
    }
}

private fun RcRowScope.navItem(
    scale: ScreenScale,
    label: String,
    actionId: Int,
    selected: Boolean = false,
    draw: RcCanvasScope.(size: Float, color: Int) -> Unit,
) {
    val color = if (selected) BrewColors.primary else BrewColors.navInactive
    val metadata = remoteText(label)
    Column(
        modifier = Modifier
            .weight(1f)
            .onClick { hostAction(actionId, metadata) },
        horizontal = RcHorizontalPositioning.Center,
    ) {
        icon(scale, 22f) { s -> draw(s, color) }
        Text(
            text = label,
            color = color,
            fontSize = scale.sp(11f),
            fontWeight = if (selected) RcFontWeight.SemiBold else RcFontWeight.Medium,
            modifier = Modifier.padding(top = scale.px(4f)),
        )
    }
}

// endregion

// region Shared building blocks

private fun RcColumnScope.sectionHeader(
    scale: ScreenScale,
    title: String,
    subtitle: String? = null,
    top: Float,
    trailing: RcRowScope.() -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = scale.px(16f),
                end = scale.px(16f),
                top = scale.px(top),
                bottom = scale.px(12f),
            ),
        vertical = RcVerticalPositioning.Center,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = BrewColors.ink,
                fontSize = scale.sp(19f),
                fontWeight = RcFontWeight.Medium,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = BrewColors.inkMuted,
                    fontSize = scale.sp(12f),
                    modifier = Modifier.padding(top = scale.px(2f)),
                )
            }
        }
        trailing()
    }
}

private fun RcRowScope.textAction(
    scale: ScreenScale,
    label: String,
    actionId: Int,
    color: Int = BrewColors.primary,
) {
    val metadata = remoteText(label)
    Text(
        text = label,
        color = color,
        fontSize = scale.sp(11f),
        fontWeight = RcFontWeight.SemiBold,
        modifier = Modifier
            .onClick { hostAction(actionId, metadata) }
            .padding(start = scale.px(8f), top = scale.px(4f), bottom = scale.px(4f)),
    )
}

private fun RcColumnScope.divider(scale: ScreenScale) {
    Box(Modifier.fillMaxWidth().height(scale.dp(1f)).background(BrewColors.divider))
}

private fun RcRowScope.dot(scale: ScreenScale, sizeDp: Float, color: Int) {
    val size = scale.px(sizeDp)
    Box(Modifier.size(size).clip(rounded(size / 2f)).background(color))
}

private fun RcScope.clickableIcon(
    scale: ScreenScale,
    sizeDp: Float,
    actionId: Int,
    description: String,
    draw: RcCanvasScope.(size: Float) -> Unit,
) {
    val metadata = remoteText(description)
    val size = scale.px(sizeDp)
    Box(Modifier.size(size).onClick { hostAction(actionId, metadata) }) {
        Canvas(Modifier.size(size)) { draw(size) }
    }
}

/** A square canvas of [sizeDp]; [draw] receives the side length in pixels. */
private fun RcScope.icon(scale: ScreenScale, sizeDp: Float, draw: RcCanvasScope.(size: Float) -> Unit) {
    val size = scale.px(sizeDp)
    Canvas(Modifier.size(size)) { draw(size) }
}

private fun ScreenScale.px(value: Float): Float = dp(value).value

/** Width available to content that has [horizontalPaddingDp] on both sides. */
private fun ScreenScale.contentWidthPx(horizontalPaddingDp: Float): Float =
    widthPx - 2 * px(horizontalPaddingDp)

private fun rounded(radius: Float) = RoundedRectShape(radius, radius, radius, radius)

// endregion

// region Vector icons (drawn in a 0..s box)

private fun RcCanvasScope.stroke(color: Int, width: Float) {
    applyPaint {
        setStyle(RcPaintStyle.Stroke)
        setColor(color)
        setStrokeWidth(width)
        setStrokeCap(RcStrokeCap.Round)
        setStrokeJoin(RcStrokeJoin.Round)
    }
}

private fun RcCanvasScope.fill(color: Int) {
    applyPaint {
        setStyle(RcPaintStyle.Fill)
        setColor(color)
    }
}

private fun RcCanvasScope.drawSearchIcon(s: Float, color: Int) {
    stroke(color, s * 0.09f)
    drawCircle(s * 0.43f, s * 0.43f, s * 0.27f)
    drawLine(s * 0.63f, s * 0.63f, s * 0.86f, s * 0.86f)
}

private fun RcCanvasScope.drawBellIcon(s: Float, color: Int) {
    stroke(color, s * 0.08f)
    val bell = remotePath(s * 0.18f, s * 0.74f)
    bell.lineTo(s * 0.27f, s * 0.62f)
    bell.lineTo(s * 0.27f, s * 0.44f)
    bell.quadTo(s * 0.27f, s * 0.18f, s * 0.5f, s * 0.18f)
    bell.quadTo(s * 0.73f, s * 0.18f, s * 0.73f, s * 0.44f)
    bell.lineTo(s * 0.73f, s * 0.62f)
    bell.lineTo(s * 0.82f, s * 0.74f)
    bell.close()
    drawPath(bell.getPath())
    drawLine(s * 0.42f, s * 0.86f, s * 0.58f, s * 0.86f)
    // Unread indicator
    fill(BrewColors.notificationDot)
    drawCircle(s * 0.78f, s * 0.2f, s * 0.13f)
}

private fun RcCanvasScope.drawPersonIcon(s: Float, color: Int) {
    stroke(color, s * 0.1f)
    drawCircle(s * 0.5f, s * 0.34f, s * 0.17f)
    val shoulders = remotePath(s * 0.18f, s * 0.86f)
    shoulders.quadTo(s * 0.18f, s * 0.6f, s * 0.5f, s * 0.6f)
    shoulders.quadTo(s * 0.82f, s * 0.6f, s * 0.82f, s * 0.86f)
    drawPath(shoulders.getPath())
}

private fun RcCanvasScope.drawProfileIcon(s: Float, color: Int) {
    stroke(color, s * 0.08f)
    drawCircle(s * 0.5f, s * 0.5f, s * 0.42f)
    drawCircle(s * 0.5f, s * 0.4f, s * 0.13f)
    val shoulders = remotePath(s * 0.24f, s * 0.8f)
    shoulders.quadTo(s * 0.3f, s * 0.6f, s * 0.5f, s * 0.6f)
    shoulders.quadTo(s * 0.7f, s * 0.6f, s * 0.76f, s * 0.8f)
    drawPath(shoulders.getPath())
}

private fun RcCanvasScope.drawCupIcon(s: Float, color: Int) {
    stroke(color, s * 0.08f)
    val cup = remotePath(s * 0.16f, s * 0.28f)
    cup.lineTo(s * 0.7f, s * 0.28f)
    cup.lineTo(s * 0.66f, s * 0.74f)
    cup.lineTo(s * 0.2f, s * 0.74f)
    cup.close()
    drawPath(cup.getPath())
    val handle = remotePath(s * 0.69f, s * 0.38f)
    handle.quadTo(s * 0.9f, s * 0.38f, s * 0.88f, s * 0.52f)
    handle.quadTo(s * 0.86f, s * 0.64f, s * 0.67f, s * 0.64f)
    drawPath(handle.getPath())
    drawLine(s * 0.1f, s * 0.88f, s * 0.78f, s * 0.88f)
}

private fun RcCanvasScope.drawUtensilsIcon(s: Float, color: Int) {
    stroke(color, s * 0.08f)
    // Knife
    drawLine(s * 0.22f, s * 0.88f, s * 0.82f, s * 0.2f)
    val blade = remotePath(s * 0.82f, s * 0.2f)
    blade.quadTo(s * 0.88f, s * 0.36f, s * 0.66f, s * 0.42f)
    drawPath(blade.getPath())
    // Fork
    drawLine(s * 0.78f, s * 0.88f, s * 0.3f, s * 0.34f)
    drawLine(s * 0.3f, s * 0.34f, s * 0.14f, s * 0.18f)
    drawLine(s * 0.3f, s * 0.34f, s * 0.24f, s * 0.12f)
    drawLine(s * 0.3f, s * 0.34f, s * 0.08f, s * 0.28f)
}

private fun RcCanvasScope.drawTagIcon(s: Float, color: Int) {
    stroke(color, s * 0.08f)
    val tag = remotePath(s * 0.14f, s * 0.14f)
    tag.lineTo(s * 0.5f, s * 0.14f)
    tag.lineTo(s * 0.88f, s * 0.52f)
    tag.lineTo(s * 0.52f, s * 0.88f)
    tag.lineTo(s * 0.14f, s * 0.5f)
    tag.close()
    drawPath(tag.getPath())
    fill(color)
    drawCircle(s * 0.33f, s * 0.33f, s * 0.07f)
}

private fun RcCanvasScope.drawFlameIcon(s: Float, color: Int) {
    stroke(color, s * 0.11f)
    val flame = remotePath(s * 0.5f, s * 0.08f)
    flame.quadTo(s * 0.88f, s * 0.42f, s * 0.8f, s * 0.68f)
    flame.quadTo(s * 0.72f, s * 0.92f, s * 0.5f, s * 0.92f)
    flame.quadTo(s * 0.28f, s * 0.92f, s * 0.2f, s * 0.68f)
    flame.quadTo(s * 0.14f, s * 0.42f, s * 0.5f, s * 0.08f)
    flame.close()
    drawPath(flame.getPath())
}

private fun RcCanvasScope.drawMinusIcon(s: Float, color: Int) {
    stroke(color, s * 0.13f)
    drawLine(s * 0.12f, s * 0.5f, s * 0.88f, s * 0.5f)
}

private fun RcCanvasScope.drawPlusIcon(s: Float, color: Int) {
    stroke(color, s * 0.13f)
    drawLine(s * 0.5f, s * 0.12f, s * 0.5f, s * 0.88f)
    drawLine(s * 0.12f, s * 0.5f, s * 0.88f, s * 0.5f)
}

private fun RcCanvasScope.drawPlayBadge(s: Float) {
    fill(0xF2FFFFFF.toInt())
    drawCircle(s * 0.5f, s * 0.5f, s * 0.5f)
    fill(BrewColors.ink)
    val triangle = remotePath(s * 0.4f, s * 0.3f)
    triangle.lineTo(s * 0.72f, s * 0.5f)
    triangle.lineTo(s * 0.4f, s * 0.7f)
    triangle.close()
    drawPath(triangle.getPath())
}

// endregion
