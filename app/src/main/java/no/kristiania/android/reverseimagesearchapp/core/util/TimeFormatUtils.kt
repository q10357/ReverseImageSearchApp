package no.kristiania.android.reverseimagesearchapp.core.util

import java.text.SimpleDateFormat
import java.util.*

private const val simpleDatePattern: String = "yyyy-MM-dd"
private const val simpleTimePattern: String = "HH:mm"

object TimeFormat{

    fun formatSimpleDate(date: Date): String{
       return SimpleDateFormat(simpleDatePattern).format(date)
    }

    fun formatSimpleTime(time: Date): String{
        return SimpleDateFormat(simpleTimePattern).format(time)
    }

    fun getFullDate(date: Date, hour: Int, minutes: Int): Date{
        val c = Calendar.getInstance()
        c.set(Calendar.MINUTE, minutes)
        c.set(Calendar.HOUR_OF_DAY, hour)
        return c.time
    }

}