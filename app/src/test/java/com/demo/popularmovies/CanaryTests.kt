package com.demo.popularmovies

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CanaryTests {
    @Test
    fun `environment setup test`() {
        assertThat(true).isTrue()
    }
}