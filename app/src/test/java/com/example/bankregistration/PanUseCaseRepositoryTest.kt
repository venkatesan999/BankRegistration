package com.example.bankregistration

import com.example.bankregistration.model.PanDetails
import com.example.bankregistration.model.PanUseCaseRepository
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Test
import java.time.LocalDate

class PanUseCaseRepositoryTest {

    private val panUseCaseRepository = PanUseCaseRepository()

    @Test
    fun `valid PAN details`() {
        val panDetails = PanDetails(
            panNumber = "ABCDE1234F",
            date = "15",
            month = "08",
            year = "1990"
        )
        val result = panUseCaseRepository.verifyPanDetails(panDetails)
        assertTrue(result.first)
        assertEquals(null, result.second)
    }

    @Test
    fun `invalid PAN number`() {
        val panDetails = PanDetails(
            panNumber = "ABCDE12345",
            date = "15",
            month = "08",
            year = "1990"
        )
        val result = panUseCaseRepository.verifyPanDetails(panDetails)
        assertFalse(result.first)
        assertEquals("Invalid PAN number", result.second)
    }

    @Test
    fun `invalid date`() {
        val panDetails = PanDetails(
            panNumber = "ABCDE1234F",
            date = "32",
            month = "08",
            year = "1990"
        )
        val result = panUseCaseRepository.verifyPanDetails(panDetails)
        assertFalse(result.first)
        assertEquals("Invalid date", result.second)
    }

    @Test
    fun `invalid month`() {
        val panDetails = PanDetails(
            panNumber = "ABCDE1234F",
            date = "15",
            month = "13",
            year = "1990"
        )
        val result = panUseCaseRepository.verifyPanDetails(panDetails)
        assertFalse(result.first)
        assertEquals("Invalid month", result.second)
    }

    @Test
    fun `age below 18`() {
        val panDetails = PanDetails(
            panNumber = "ABCDE1234F",
            date = "15",
            month = "08",
            year = (LocalDate.now().year - 17).toString()
        )
        val result = panUseCaseRepository.verifyPanDetails(panDetails)
        assertFalse(result.first)
        assertEquals("Age should be 18 or above", result.second)
    }

    @Test
    fun `year in future`() {
        val panDetails = PanDetails(
            panNumber = "ABCDE1234F",
            date = "15",
            month = "08",
            year = (LocalDate.now().year - 1).toString()
        )
        val result = panUseCaseRepository.verifyPanDetails(panDetails)
        assertFalse(result.first)
        assertEquals("Year should not be in the future", result.second)
    }

    @Test
    fun `invalid date and month length`() {
        val panDetails = PanDetails(
            panNumber = "ABCDE1234F",
            date = "5",
            month = "8",
            year = "1990"
        )
        val result = panUseCaseRepository.verifyPanDetails(panDetails)
        assertFalse(result.first)
        assertEquals("Date and month must be two digits each", result.second)
    }
}
