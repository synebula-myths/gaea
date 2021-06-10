package com.synebula.gaea.data.date

import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

/**
 * 时间格式，方便用于获取Date格式的多种形式
 */
class DateTime() : Comparable<DateTime> {

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
    constructor(date: Date) : this() {
        this.calendar.time = date
    }

    /**
     * 从Calendar格式转化
     */
    constructor(calendar: Calendar) : this() {
        this.calendar = calendar
    }

    /**
     * 从Ｄate格式转化
     */
    constructor(date: String, format: String = "yyyy-MM-dd HH:mm:ss") : this() {
        val formatter = SimpleDateFormat(format)
        try {
            val value = formatter.parse(date)
            this.calendar.time = value
        } catch (e: ParseException) {
            throw RuntimeException("date string can't format to date", e)
        }
    }

    /**
     * 从Ｄate格式转化。需要注意的是月0代表是一月，以此类推。
     */
    constructor(year: Int, month: Int, day: Int = 0, hour: Int = 0, minute: Int = 0, second: Int = 0) : this() {
        this.calendar.set(year, month, day, hour, minute, second)
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
     * @return true or false
     */
    fun isBetween(start: DateTime, end: DateTime): Boolean {
        //return this in start..end
        return start.dateNoTime.compareTo(this.dateNoTime) * this.dateNoTime.compareTo(end.dateNoTime) >= 0
    }

    /**
     * 比较当前时间是否在目标时间范围内。
     * @param start 目标开始时间。
     * @param end 目标结束时间。
     * @param level 比较时间的最小级别。
     * @return true or false
     */
    fun isBetween(start: DateTime, end: DateTime, level: TimeUnit): Boolean {
        return this.compareTo(start, level) >= 0 && this.compareTo(end, level) <= 0
    }


    /**
     * 判断当前时间是否在某时间后
     *
     * @param other 另一时间
     * @return true or false
     */
    fun isAfter(other: DateTime): Boolean {
        return this.date.after(other.date)
    }

    /**
     * 判断当前时间是否在某时间前
     *
     * @param other 另一时间
     * @return true or false
     */
    fun isBefore(other: DateTime): Boolean {
        return this.date.before(other.date)
    }

    /**
     * 判断当前时间是否同一天
     *
     * @param other 另一时间
     * @return true or false
     */
    fun isSameDay(other: DateTime): Boolean {
        return this.calendar.get(Calendar.ERA) == other.calendar.get(Calendar.ERA)
                && this.calendar.get(Calendar.YEAR) == other.calendar.get(Calendar.YEAR)
                && this.calendar.get(Calendar.DAY_OF_YEAR) == other.calendar.get(Calendar.DAY_OF_YEAR)
    }


    /**
     * 加减年。
     *
     * @param year 月份。可以为负数，即为减。
     * @return this
     */
    fun addYear(year: Int): DateTime {
        this.calendar.add(Calendar.YEAR, year)
        return this
    }

    /**
     * 加减月份。
     *
     * @param month 月份。可以为负数，即为减。
     * @return this
     */
    fun addMonth(month: Int): DateTime {
        this.calendar.add(Calendar.MONTH, month)
        return this
    }


    /**
     * 加减日期。
     *
     * @param day 天数。可以为负数，即为减。
     * @return this
     */
    fun addDay(day: Int): DateTime {
        this.calendar.add(Calendar.DAY_OF_MONTH, day)
        return this
    }


    /**
     * 加减小时。
     *
     * @param hour 小时。可以为负数，即为减。
     * @return this
     */
    fun addHour(hour: Int): DateTime {
        this.calendar.add(Calendar.HOUR_OF_DAY, hour)
        return this
    }

    /**
     * 加减分钟。
     *
     * @param minute 分钟。可以为负数，即为减。
     * @return this
     */
    fun addMinute(minute: Int): DateTime {
        this.calendar.add(Calendar.MINUTE, minute)
        return this
    }

    /**
     * 加减秒
     * @param second 秒数。可以为负数，即为减。
     * @return this
     */
    fun addSecond(second: Int): DateTime {
        this.calendar.add(Calendar.SECOND, second)
        return this
    }

    /**
     * 加减毫秒
     * @param millisecond 毫秒数。可以为负数，即为减。
     * @return this
     */
    fun addMillisecond(millisecond: Int): DateTime {
        this.calendar.add(Calendar.MILLISECOND, millisecond)
        return this
    }

    /**
     * 设置年。
     *
     * @param year 月份。
     * @return this
     */
    fun setYear(year: Int): DateTime {
        this.calendar.set(Calendar.YEAR, year)
        return this
    }

    /**
     * 设置月份。
     *
     * @param month 月份。
     * @return this
     */
    fun setMonth(month: Int): DateTime {
        this.calendar.set(Calendar.MONTH, month)
        return this
    }


    /**
     * 设置日期。
     *
     * @param day 天数。
     * @return this
     */
    fun setDay(day: Int): DateTime {
        this.calendar.set(Calendar.DAY_OF_MONTH, day)
        return this
    }


    /**
     * 设置小时。
     *
     * @param hour 小时。
     * @return this
     */
    fun setHour(hour: Int): DateTime {
        this.calendar.set(Calendar.HOUR_OF_DAY, hour)
        return this
    }

    /**
     * 设置分钟。
     *
     * @param minute 分钟。
     * @return this
     */
    fun setMinute(minute: Int): DateTime {
        this.calendar.set(Calendar.MINUTE, minute)
        return this
    }

    /**
     * 设置秒
     * @param second 总秒数。
     * @return this
     */
    fun setSecond(second: Int): DateTime {
        this.calendar.set(Calendar.SECOND, second)
        return this
    }

    /**
     * 设置毫秒。
     * @param millisecond 毫秒数。
     * @return this
     */
    fun setMillisecond(millisecond: Int): DateTime {
        this.calendar.set(Calendar.MILLISECOND, millisecond)
        return this
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
        val dayOfWeek = this.calendar.get(Calendar.DAY_OF_WEEK)
        return (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY)
    }

    /**
     * 清空毫秒，避免误差
     */
    fun clearMillisecond() {
        this.calendar.set(Calendar.MILLISECOND, 0)
    }

    /**
     * 克隆当前对象
     *
     * @return 返回新的DateTime对象
     */
    fun clone(): DateTime {
        return DateTime(this.calendar)
    }

    /**
     * 时间相见
     */
    operator fun minus(other: DateTime): TimeSpan {
        return TimeSpan(this.milliseconds - other.milliseconds)
    }
}
