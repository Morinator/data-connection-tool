package com.digitalfrontiers.olddataconnectiontool.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.io.File
import java.io.FileNotFoundException

class FileStorageServiceTest {

    private var fileStorageService = FileStorageService()
    private var defaultKey = "simpleKey1"

    @BeforeEach
    fun `reset directory so that previous tests do not interfere`() {

        val tmpDir = File("tmp")

        // Delete the directory and its contents if it exists
        if (tmpDir.exists()) {
            tmpDir.walkBottomUp().forEach { file ->
                file.delete()
            }
        }

        tmpDir.mkdirs() // Create a fresh tmp directory
    }

    @Test
    fun `store and load simple value`() {
        fileStorageService.store(defaultKey, data="123abc")
        val data = fileStorageService.load(key = "simpleKey1")
        assertEquals("123abc", data)
    }

    @Test
    fun `load non existing value`() {
        assertThrows<FileNotFoundException> {
            fileStorageService.load(key = "simpleKey1")
        }
    }

    @Test
    fun `verify encoding with special characters`() {
        val complexData = """
            {
                "emoji": "🌟🎉🌈👨‍👩‍👧‍👦",
                "japanese": "こんにちは世界",
                "arabic": "مرحبا بالعالم",
                "russian": "Привет, мир",
                "greek": "Γεια σας κόσμε",
                "specialChars": "™©®‰€£¥\n\t\"\\",
                "mathSymbols": "∑∏∆∇√∞≠≈",
                "mixedContent": "Hello مرحبا 你好 🌟 π®"
            }
        """.trimIndent()

        val key = "encoding-test"
        fileStorageService.store(key, complexData)
        val loadedData = fileStorageService.load(key)

        assertEquals(complexData, loadedData, "Loaded data should match stored data exactly, including all special characters")

        // Additional verification for specific character sequences
        loadedData?.let {
            assertTrue(it.contains("🌟"), "Should contain emoji")
            assertTrue(it.contains("こんにちは"), "Should contain Japanese text")
            assertTrue(it.contains("مرحبا"), "Should contain Arabic text")
            assertTrue(it.contains("\\"), "Should contain escaped backslash")
            assertTrue(it.contains("\n"), "Should contain newline")
            assertTrue(it.contains("≠"), "Should contain mathematical symbols")
        }
    }

    @Test
    fun `test evil key value`() {
        val evilKey = "../..////..//."
        fileStorageService.store(key = evilKey, data = "123abc")
        val data = fileStorageService.load(key = evilKey)
        assertEquals("123abc", data)
    }
}