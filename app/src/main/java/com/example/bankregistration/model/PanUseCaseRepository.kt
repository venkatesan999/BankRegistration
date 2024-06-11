package com.example.bankregistration.model

import java.time.LocalDate

class PanUseCaseRepository {

    fun verifyPanDetails(panDetails: PanDetails): Pair<Boolean, String?> {
        val isPanValid = isPanValid(panDetails.panNumber)
        val isDobValid =
            isDobValid(panDetails.date.toInt(), panDetails.month.toInt(), panDetails.year.toInt())
        val isValidInputFields = isValidInputFields(
            panDetails.panNumber,
            panDetails.year
        )
        val isDateAndMonthFields = isDateAndMonthFields(panDetails.date, panDetails.month)

        return if (isPanValid.first && isDobValid.first && isValidInputFields && isDateAndMonthFields.first) {
            Pair(true, null)
        } else {
            val errorMessage = isDateAndMonthFields.second ?: isDobValid.second ?: isPanValid.second
            Pair(false, errorMessage)
        }
    }

    private fun isDateAndMonthFields(date: String, month: String): Pair<Boolean, String?> {
        return if (date.length == 2 && month.length == 2) {
            Pair(true, null)
        } else {
            Pair(false, "Date and month must be two digits each")
        }
    }

    private fun isValidInputFields(panNumber: String, year: String): Boolean =
        panNumber.length > 9 && year.length > 3

    private fun isPanValid(panNumber: String): Pair<Boolean, String?> {
        val panRegex = "[A-Z]{5}[0-9]{4}[A-Z]{1}".toRegex()
        return if (panNumber.matches(panRegex)) Pair(true, null) else Pair(
            false,
            "Invalid PAN number"
        )
    }

    private fun isDobValid(date: Int, month: Int, year: Int): Pair<Boolean, String?> {
        val currentDate = LocalDate.now()

        if (month !in 1..12) {
            return Pair(false, "Invalid month")
        }

        val daysInMonth = when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> return Pair(false, "Invalid month")
        }

        if (date !in 1..daysInMonth) {
            return Pair(false, "Invalid date")
        }

        val dob = LocalDate.of(year, month, date)

        if (dob.plusYears(18).isAfter(currentDate)) {
            return Pair(false, "Age should be 18 or above")
        }

        if (year > currentDate.year) {
            return Pair(false, "Year should not be in the future")
        }

        return Pair(true, null)
    }

    private fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }
}