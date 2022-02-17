package com.c3ai.sourcingoptimization.common

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

fun getCurrentDate():Date = Calendar.getInstance().time

@SuppressLint("SimpleDateFormat")
fun formatDate(format: String = "yyyy-MM-dd'T'HH:mm:ss", date: Date): String =
    SimpleDateFormat(format).format(date)

fun getMonthBackDate(months: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.MONTH, -months)
    return calendar.time
}

fun getYearBackDate(years: Int): Date {
    val calendar = Calendar.getInstance()
    calendar.add(Calendar.YEAR, -years)
    return calendar.time
}

@SuppressLint("SimpleDateFormat")
fun getMonth(date: String, format: String = "yyyy-MM-dd'T'HH:mm:ss"): String {
    val convertedDate = SimpleDateFormat(format).parse(date)
    val calendar = Calendar.getInstance()
    calendar.time = convertedDate
    val months = listOf( "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec")
    return months[calendar.get(Calendar.MONTH)]
}