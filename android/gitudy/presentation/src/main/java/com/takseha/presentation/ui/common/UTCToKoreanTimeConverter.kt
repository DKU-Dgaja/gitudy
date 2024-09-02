package com.takseha.presentation.ui.common

import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class UTCToKoreanTimeConverter {
    fun convertToKoreaTime(localDateTimeUTC: LocalDateTime): String {
        val utcZonedDateTime = localDateTimeUTC.atZone(ZoneId.of("UTC"))
        val koreaZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")

        return koreaZonedDateTime.format(formatter)
    }

    fun convertToKoreaDate(localDateTimeUTC: LocalDateTime): String {
        val utcZonedDateTime = localDateTimeUTC.atZone(ZoneId.of("UTC"))
        val koreaZonedDateTime = utcZonedDateTime.withZoneSameInstant(ZoneId.of("Asia/Seoul"))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        return koreaZonedDateTime.format(formatter)
    }
}