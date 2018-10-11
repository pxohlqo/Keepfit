package com.cracky_axe.pxohlqo.keyi.util

import java.text.SimpleDateFormat
import java.util.*

class FitDateUtils {

    companion object {

        const val DAY_TIME_RANGE = 86400000

        fun getThisWeekDayList(): List<String> {

            val calendar = Calendar.getInstance()

            var mon2Today = calendar.get(Calendar.DAY_OF_WEEK) - 2
            if (mon2Today == -1) {
                mon2Today = 6
            }
            val monInMillis = calendar.apply { set(this.get(Calendar.YEAR),
                    this.get(Calendar.MONTH),
                    this.get(Calendar.DATE), 0, 0, 0) }.timeInMillis - mon2Today * DAY_TIME_RANGE
            val week = mutableListOf<String>()
            val dateFormat = SimpleDateFormat("dd")
            repeat(7) {
                week.add(it, dateFormat.format(Date(monInMillis + it * DAY_TIME_RANGE)))
            }
            return week
        }

        /**
         * @return day#of the week, from 0 ~ 6
         */
        fun getTodayOfWeek(): Int {
            val now = System.currentTimeMillis()
            val date = Date(now)
            val calendar = Calendar.getInstance()
            calendar.time = date

            var today = calendar.get(Calendar.DAY_OF_WEEK) - 2
            if (today == -1) {
                today = 6
            }
            return today
        }

        fun utcTime2ReadableTime(time: Long): String {
            val formatter = SimpleDateFormat("HH:mm:ss", Locale.CHINA)
            return formatter.format(time)
        }

        fun getDateTimeRange(dayOfMonth: Int): List<Long> {

            val beginAt = Calendar.getInstance().apply { set(this.get(Calendar.YEAR), this.get(Calendar.MONTH), dayOfMonth, 0, 0, 0) }

            return listOf(beginAt.timeInMillis, beginAt.timeInMillis + DAY_TIME_RANGE)
        }

        fun getWeekTimeRange(): List<Long> {
            val calendar = Calendar.getInstance()

            var mon2Today = calendar.get(Calendar.DAY_OF_WEEK) - 2
            if (mon2Today == -1) {
                mon2Today = 7
            }
            val monInMillis = calendar.apply { set(this.get(Calendar.YEAR),
                    this.get(Calendar.MONTH),
                    this.get(Calendar.DATE), 0, 0, 0) }.timeInMillis - mon2Today * DAY_TIME_RANGE

            val weekRangeInMillis = mutableListOf<Long>()
            weekRangeInMillis.apply {
                add(0, monInMillis)
                add(1, monInMillis + 7 * DAY_TIME_RANGE)
            }

            return weekRangeInMillis

        }

        fun easyTimeMillis2String(timeMillis: Long): String {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = timeMillis
            return "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH ) + 1}-${calendar.get(Calendar.DATE)} ${calendar.get(Calendar.HOUR_OF_DAY)}:${calendar.get(Calendar.MINUTE)}"
        }
    }
}