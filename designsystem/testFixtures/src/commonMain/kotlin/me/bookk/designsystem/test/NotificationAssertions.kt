package me.bookk.designsystem.test

import me.bookk.core.presentation.error.ActionType
import me.bookk.core.presentation.error.PresentationNotification
import me.bookk.designsystem.uistate.PresentationNotificationState
import kotlin.reflect.KClass
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

fun FakeErrorMapper.assertMappedSingle(expected: KClass<out Throwable>) {
    assertEquals(1, mappedErrors.size, "expected exactly one mapped error but was $mappedErrors")
    assertTrue(expected.isInstance(mappedErrors.single()), "expected ${expected.simpleName} but was ${mappedErrors.single()}")
}

fun FakeErrorMapper.assertNothingMapped() {
    assertTrue(mappedErrors.isEmpty(), "expected no mapped errors but was $mappedErrors")
}

inline fun <reified T : PresentationNotification> PresentationNotificationState.assertSingle(): T {
    assertEquals(1, presentationNotification.size, "expected one notification but was $presentationNotification")
    return assertIs<T>(presentationNotification.single())
}

fun PresentationNotificationState.assertEmpty() {
    assertTrue(presentationNotification.isEmpty(), "expected no notifications but was $presentationNotification")
}

fun PresentationNotification.Message.tap(actionType: ActionType) {
    buttons.single { it.actionType == actionType }.onClick()
}
