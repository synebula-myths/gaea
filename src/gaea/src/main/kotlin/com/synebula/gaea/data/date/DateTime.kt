package com.synebula.gaea.data.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间格式，方便用于获取Date格式的多种形式
 */
class DateTime : Comparable<DateTime> {

    // 内部存储日历格式方便操作
    /**
     * 返回内部日历格式类型。
     * @return 日历格式类型。
     */
    private var calendar = Calendar.getInstance()!!

    /**
     * 当前时间的总毫秒数。
     */
    private val milliseconds: Long
        get() = this.calendar.timeInMillis

    /**
     * 列出时间的级别数组
     */
    private val calendarLevel = intArrayOf(
        Calendar.MILLISECOND,
        Calendar.SECOND,
        Calendar.MINUTE,
        Calendar.HOUR_OF_DAY,
        Calendar.DAY_OF_MONTH,
        Calendar.MONTH,
        Calendar.YEAR
    )

    val date: Date
        get() = calendar.time

    /**
     * 获取去除了时间部分的日期类型。
     */
    val dateNoTime: Date
        get() {
            val instance = calendar.clone() as Calendar
            instance.set(Calendar.HOUR_OF_DAY, 0)
            instance.set(Calendar.MINUTE, 0)
            instance.set(Calendar.SECOND, 0)
            instance.set(Calendar.MILLISECOND, 0)
            return instance.time
        }

    /**
     * 当前时间年
     */
    var year: Int
        get() = calendar.get(Calendar.YEAR)
        set(value) = calendar.set(Calendar.YEAR, value)

    /**
     * 当前时间月
     */
    var month: Int
        get() = calendar.get(Calendar.MONTH)
        set(value) = calendar.set(Calendar.MONTH, value)

    /**
     * 当前时间天
     */
    var day: Int
        get() = calendar.get(Calendar.DAY_OF_MONTH)
        set(value) = calendar.set(Calendar.DAY_OF_MONTH, value)

    /**
     * 获取时间
     */
    val time: Time
        get() = Time(calendar.time)

    /**
     * 获取当月天数
     */
    val days: Int
        get() {
            return this.calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        }

    /**
     * 当前月第一天
     */
    val firstDayOfMonth: DateTime
        get() {
            val instance = calendar.clone() as Calendar
            instance.set(Calendar.DAY_OF_MONTH, 1)
            return DateTime(instance)
        }

    /**
     * 当前月最后一天
     */
    val lastDayOfMonth: DateTime
        get() {
            val instance = calendar.clone() as Calendar
            instance.set(Calendar.DAY_OF_MONTH, instance.getActualMaximum(Calendar.DAY_OF_MONTH))
            return DateTime(instance)
        }

    /**
     * 当前周第一天
     */
    val firstDayOfWeek: DateTime
        get() {
            val instance = calendar.clone() as Calendar
            instance.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY)
            instance.set(Calendar.HOUR_OF_DAY, 0)
            instance.set(Calendar.MINUTE, 0)
            instance.set(Calendar.SECOND, 0)
            instance.set(Calendar.MILLISECOND, 0)
            return DateTime(instance)
        }

    /**
     * 当前周最后一天
     */
    val lastDayOfWeek: DateTime
        get() {
            val instance = this.firstDayOfWeek.calendar.clone() as Calendar
            instance.add(Calendar.DAY_OF_WEEK, 6)//当前周第一天加6天
            instance.set(Calendar.HOUR_OF_DAY, 23)
            instance.set(Calendar.MINUTE, 59)
            instance.set(Calendar.SECOND, 59)
            instance.set(Calendar.MILLISECOND, 0)
            return DateTime(instance)
        }

    /**
     * 当前天最早时间
     */
    val earliestTime: DateTime
        get() {
            val instance = this.calendar.clone() as Calendar
            instance.set(Calendar.HOUR_OF_DAY, 0)
            instance.set(Calendar.MINUTE, 0)
            instance.set(Calendar.SECOND, 0)
            instance.set(Calendar.MILLISECOND, 0)
            return DateTime(instance)
        }

    /**
     * 当前天最晚时间
     */
    val latestTime: DateTime
        get() {
            val instance = this.calendar.clone() as Calendar
            instance.set(Calendar.HOUR_OF_DAY, 23)
            instance.set(Calendar.MINUTE, 59)
            instance.set(Calendar.SECOND, 59)
            instance.set(Calendar.MILLISECOND, 0)
            return DateTime(instance)
        }

    /**
     * 当前时间最上一月
     */
    val previousMonth: DateTime
        get() {
            return this.addMonth(-1)
        }

    /**
     * 当前时间下一月
     */
    val nextMonth: DateTime
        get() {
            return this.addMonth(1)
        }

    /**
     * 当前时间最上一天
     */
    val previousDay: DateTime
        get() {
            return this.addDay(-1)
        }

    /**
     * 当前时间下一天
     */
    val nextDay: DateTime
        get() {
            return this.addDay(1)
        }

    /**
     * 从Ｄate格式转化
     */
    constructor(date: Date) {
        this.calendar.time = date
        this.calendar.set(Calendar.MILLISECOND, 0)
    }

    /**
     * 从Calendar格式转化
     */
    constructor(calendar: Calendar) {
        this.calendar = calendar
        this.calendar.set(Calendar.MILLISECOND, 0)
    }

    /**
     * 从Ｄate格式转化
     */
    constructor(date: String, format: String) {
        val formatter = SimpleDateFormat(format)
        try {
            val value = formatter.parse(date)
            this.calendar.time = value
            this.calendar.set(Calendar.MILLISECOND, 0)
        } catch (e: ParseException) {
            throw RuntimeException("date string can't format to date", e)
        }
    }

    /**
     * 从Ｄate格式转化。需要注意的是月0代表是一月，以此类推。
     */
    constructor(year: Int, month: Int, day: Int = 0, hour: Int = 0, minute: Int = 0, second: Int = 0) {
        this.calendar.set(year, month, day, hour, minute, second)
        this.calendar.set(Calendar.MILLISECOND, 0)
    }

    /**
     * 输入字符串。
     */
    override fun toString(): String {
        return calendar.time.toString()
    }

    /**
     * 格式化输出字符串。
     */
    fun toString(format: String): String {
        val formatter = SimpleDateFormat(format)
        return formatter.format(calendar.time)
    }

    /**
     * 比较两个时间的大小。
     * -1当前时间早于目标时间，0两个时间相等，1当前时间晚于目标时间。
     * @param other 目标时间。
     * @return -1当前时间早于目标时间，0两个时间相等，1当前时间晚于目标时间。
     */
    override fun compareTo(other: DateTime): Int {
        return this.calendar.compareTo(other.calendar)
    }

    /**
     * 比较两个时间的大小, 考虑时间级别的敏感程度。如 了level选择秒，则忽略毫秒值。
     * -1当前时间早于目标时间，0两个时间相等，1当前时间晚于目标时间。
     * @param o 目标时间。
     * @param level 比较时间的最小级别。
     * @return -1当前时间早于目标时间，0两个时间相等，1当前时间晚于目标时间。
     */
    fun compareTo(o: DateTime, level: TimeUnit): Int {
        val first = this.calendar.clone() as Calendar
        val second = o.calendar.clone() as Calendar
        for (i in 0 until level.ordinal) {
            first.set(calendarLevel[i], 0)
            second.set(calendarLevel[i], 0)
        }
        return first.compareTo(second)
    }

    /**
     * 比较当前时间是否在目标时间范围内。
     * @param start 目标开始时间。
     * @param end 目标结束时间。
     * @return 是否。
     */
    fun between(start: DateTime, end: DateTime): Boolean {
        //return this in start..end
        return start.dateNoTime.compareTo(this.dateNoTime) * this.dateNoTime.compareTo(end.dateNoTime) >= 0
    }

    /**
     * 比较当前时间是否在目标时间范围内。
     * @param start 目标开始时间。
     * @param end 目标结束时间。
     * @param level 比较时间的最小级别。
     * @return 是否。
     */
    fun between(start: DateTime, end: DateTime, level: TimeUnit): Boolean {
        return this.compareTo(start, level) >= 0 && this.compareTo(end, level) <= 0
    }

    /**
     * 增加秒
     */
    fun addSeconds(seconds: Long) {
        if (seconds <= Int.MAX_VALUE)
            this.calendar.add(Calendar.SECOND, seconds.toInt())
        else {
            val span = TimeSpan(seconds * 1000)
            this.calendar.add(Calendar.DAY_OF_MONTH, span.day)
            this.calendar.add(Calendar.HOUR_OF_DAY, span.hour)
            this.calendar.add(Calendar.MINUTE, span.minute)
            this.calendar.add(Calendar.SECOND, span.second)
        }
    }

    /**
     * 判断当前时间是否在某时间后
     *
     * @param other 另一时间
     */
    fun after(other: DateTime): Boolean {
        return this.date.after(other.date)
    }

    /**
     * 判断当前时间是否在某时间前
     *
     * @param other 另一时间
     */
    fun before(other: DateTime): Boolean {
        return this.date.before(other.date)
    }

    /**
     * 加减月份。
     *
     * @param month 月份。可以为负数，即为减。
     */
    fun addMonth(month: Int): DateTime {
        val instance = calendar.clone() as Calendar
        instance.add(Calendar.MONTH, month)
        return DateTime(instance)
    }

    /**
     * 加减日期。
     *
     * @param day 天数。可以为负数，即为减。
     */
    fun addDay(day: Int): DateTime {
        val instance = calendar.clone() as Calendar
        instance.add(Calendar.DAY_OF_MONTH, day)
        return DateTime(instance)
    }

    /**
     * 当前天是否工作日
     */
    fun isWorkday(): Boolean {
        return !this.isWeekend()
    }

    /**
     * 当前天是否周末
     */
    fun isWeekend(): Boolean {
        val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
        return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
    }

    operator fun minus(other: DateTime): TimeSpan {
        return TimeSpan(this.milliseconds - other.milliseconds)
    }
}
