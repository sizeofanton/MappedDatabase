package com.sizeofanton.mappeddatabaseandroid

import com.sizeofanton.mappeddatabaseandroid.util.ext.toInt
import org.junit.Assert.assertEquals
import org.junit.Test

class ExtensionsTest {
    @Test
    fun booleanToIntExtensionTest() {
        val boolean1 = true
        assertEquals(1, boolean1.toInt())

        val boolean2 = false
        assertEquals(0, boolean2.toInt())
    }
}
