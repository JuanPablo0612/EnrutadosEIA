package com.juanpablo0612.carpool.presentation.ui.util

import kotlin.test.Test
import kotlin.test.assertEquals

class MoneyFormatTest {

    @Test
    fun groupsThousandsWithDots() {
        assertEquals("$4.000", formatPesos(4_000))
        assertEquals("$50.000", formatPesos(50_000))
        assertEquals("$1.250.000", formatPesos(1_250_000))
    }

    @Test
    fun groupingAloneHasNoCurrencySign() {
        assertEquals("12.500", groupThousands(12_500))
    }

    @Test
    fun smallAmountsHaveNoSeparator() {
        assertEquals("$0", formatPesos(0))
        assertEquals("$500", formatPesos(500))
    }
}
