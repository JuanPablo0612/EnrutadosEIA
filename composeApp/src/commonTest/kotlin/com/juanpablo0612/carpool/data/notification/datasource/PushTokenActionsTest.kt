package com.juanpablo0612.carpool.data.notification.datasource

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class PushTokenActionsTest {

    @Test
    fun registersEachUserAndInvalidatesOnEverySignOut() = runTest {
        val actions = flowOf(null, "a", "a", null, null, "b", null).pushTokenActions().toList()

        assertEquals(
            listOf(
                PushTokenAction.Register("a"),
                PushTokenAction.Invalidate,
                PushTokenAction.Register("b"),
                PushTokenAction.Invalidate,
            ),
            actions,
        )
    }

    @Test
    fun aSignedOutLaunchInvalidatesNothing() = runTest {
        assertEquals(emptyList(), flowOf(null, null).pushTokenActions().toList())
    }

    @Test
    fun switchingAccountsRegistersTheNewUserWithoutInvalidating() = runTest {
        assertEquals(
            listOf(PushTokenAction.Register("a"), PushTokenAction.Register("b")),
            flowOf("a", "b").pushTokenActions().toList(),
        )
    }
}
