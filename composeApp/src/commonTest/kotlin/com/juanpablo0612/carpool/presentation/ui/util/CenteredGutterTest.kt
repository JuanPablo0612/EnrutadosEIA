package com.juanpablo0612.carpool.presentation.ui.util

import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.test.Test
import kotlin.test.assertEquals

class CenteredGutterTest {

    @Test
    fun aNarrowWindowKeepsThePlainGutter() {
        assertEquals(16.dp, centeredGutter(available = 360.dp, maxWidth = 840.dp, gutter = 16.dp))
    }

    @Test
    fun aWideWindowCentresTheContentAtItsMaxWidth() {
        assertEquals(220.dp, centeredGutter(available = 1280.dp, maxWidth = 840.dp, gutter = 16.dp))
    }

    @Test
    fun justPastTheMaxWidthTheGutterIsStillTheFloor() {
        assertEquals(16.dp, centeredGutter(available = 850.dp, maxWidth = 840.dp, gutter = 16.dp))
    }

    @Test
    fun unboundedSpaceGetsThePlainGutter() {
        assertEquals(24.dp, centeredGutter(available = Dp.Infinity, maxWidth = 600.dp, gutter = 24.dp))
    }
}
