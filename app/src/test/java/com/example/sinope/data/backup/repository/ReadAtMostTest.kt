package com.example.sinope.data.backup.repository

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Test
import java.io.ByteArrayInputStream
import java.io.InputStream

/**
 * `readAtMost` replaces `InputStream.readNBytes`, which is API 33+ while this app ships to API 30.
 * These pin the behaviour the caller relies on: fill up to the limit so an oversize file can be
 * detected, and never read past it.
 */
class ReadAtMostTest {

    /** Hands out at most [chunkSize] bytes per read, the way a real content-provider stream does. */
    private class DripStream(data: ByteArray, private val chunkSize: Int) : InputStream() {
        private val delegate = ByteArrayInputStream(data)
        override fun read(): Int = delegate.read()
        override fun read(b: ByteArray, off: Int, len: Int): Int =
            delegate.read(b, off, minOf(len, chunkSize))
    }

    private fun bytes(size: Int) = ByteArray(size) { (it % 251).toByte() }

    @Test
    fun `reads the whole stream when it is under the limit`() {
        val data = bytes(1_000)
        assertArrayEquals(data, ByteArrayInputStream(data).readAtMost(5_000))
    }

    @Test
    fun `stops at the limit and leaves the rest`() {
        val data = bytes(1_000)
        val read = ByteArrayInputStream(data).readAtMost(400)

        assertEquals(400, read.size)
        assertArrayEquals(data.copyOfRange(0, 400), read)
    }

    @Test
    fun `a file exactly at the limit is not mistaken for an oversize one`() {
        // The caller reads limit+1 and rejects anything longer than limit, so an exact-size file
        // must come back at exactly limit bytes.
        val limit = 1_000
        val data = bytes(limit)

        assertEquals(limit, ByteArrayInputStream(data).readAtMost(limit + 1).size)
    }

    @Test
    fun `an oversize file comes back one byte over, so the caller can reject it`() {
        val limit = 1_000
        val data = bytes(limit + 500)

        assertEquals(limit + 1, ByteArrayInputStream(data).readAtMost(limit + 1).size)
    }

    @Test
    fun `keeps reading across short reads instead of stopping at the first chunk`() {
        // The bug this guards: a single read() call returns far less than asked for.
        val data = bytes(10_000)
        assertArrayEquals(data, DripStream(data, chunkSize = 64).readAtMost(20_000))
    }

    @Test
    fun `an empty stream yields an empty array`() {
        assertEquals(0, ByteArrayInputStream(ByteArray(0)).readAtMost(1_000).size)
    }
}
