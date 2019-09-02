package com.synebula.gaea.data.date

import java.math.BigDecimal
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


/**
 * 一天的时间类型。
 *
 * @author whj
 * @version 0.0.1
 * @since 2017年11月14日 下午3:45:26
 */
class Time(var hour: Int = 0, var minute: Int = 0, var second: Int = 0, var millisecond: Int = 0) {

    private var milliseconds = 0L

    /**
     * 根据默认格式字符串"HH:mm:ss"转化为时间类型。
     *
     * @param time 时间格式字符串。
     */
    constructor(time: String) : this(time, "HH:mm:ss")

    /**
     * 根据时间格式字符串转化为时间类型。
     *
     * @param time 时间格式字符串。
     */
    constructor(time: String, format: String) : this() {
        val formatter = SimpleDateFormat(format)
        try {
            val value = formatter.parse(time)
            this.loadTime(value)
        } catch (e: ParseException) {
            throw RuntimeException("date string can't format to date", e)
        }
    }

    /**
     * 获取日期的时间。
     */
    constructor(time: Date) : this() {
        this.loadTime(time)
    }

    private fun loadTime(time: Date) {
        val instance = Calendar.getInstance()
        instance.time = time
        this.hour = instance.get(Calendar.HOUR_OF_DAY)
        this.minute = instance.get(Calendar.MINUTE)
        this.second = instance.get(Calendar.SECOND)
        this.millisecond = instance.get(Calendar.MILLISECOND)

        this.milliseconds = (TimeExchanger.hourToMillisecond(this.hour) + TimeExchanger.minuteToMillisecond(this.minute)
                + TimeExchanger.secondToMillisecond(this.second) + this.millisecond).toLong()
    }

    /**
     * 當前时间對象减去參數中时间，得出间隔的时间
     *
     * @param other 另一个时间
     * @return
     */
    operator fun minus(other: Time): TimeSpan {
        return TimeSpan(this.milliseconds - other.milliseconds)
    }

    /**
     * 转换当前时间间隔为分钟。
     *
     * @return
     */
    fun toMinute(): Int {
        return this.hour * 60 + this.minute
    }

    /**
     * 转换当前时间间隔为小时。
     *
     * @return
     */
    fun toHour(): Double {
        return TimeExchanger.minuteToHour(this.minute) + this.hour
    }

    override fun toString(): String {
        return "$hour:$minute:$second.$millisecond"
    }
}

