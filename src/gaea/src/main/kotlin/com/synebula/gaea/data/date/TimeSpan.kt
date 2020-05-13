/**
 *
 * @author whj
 * @version 0.0.1
 * @since 2017年11月14日 下午4:51:20
 */
package com.synebula.gaea.data.date

import java.math.BigDecimal
import java.util.*

/**
 *
 * @author whj
 * @version 0.0.1
 * @since 2017年11月14日 下午4:51:20
 */
class TimeSpan {
    private val MSEC_PER_SECOND: Double = 1000.0
    private val MSEC_PER_MINUTE: Double = MSEC_PER_SECOND * 60
    private val MSEC_PER_HOUR: Double = MSEC_PER_MINUTE * 60
    private val MSEC_PER_DAY: Double = MSEC_PER_HOUR * 24

    /**
     * 是否为正值，默认正值
     */
    var isPositive: Boolean = false
    var day: Int = 0
    var hour: Int = 0
    var minute: Int = 0
    var second: Int = 0
    var millisecond: Int = 0

    val totalDays: Double
        get() {
            return this.totalMilliseconds / this.MSEC_PER_DAY
        }

    val totalHours: Double
        get() {
            return this.totalMilliseconds / this.MSEC_PER_HOUR
        }

    val totalMinutes: Double
        get() {
            return this.totalMilliseconds / this.MSEC_PER_MINUTE
        }

    val totalSeconds: Double
        get() {
            return this.totalMilliseconds / this.MSEC_PER_SECOND
        }

    var totalMilliseconds: Long = 0
        set(value) {
            this.day = (value / this.MSEC_PER_DAY).toInt()
            this.hour = ((value % this.MSEC_PER_DAY) / this.MSEC_PER_HOUR).toInt()
            this.minute = ((value % this.MSEC_PER_HOUR) / this.MSEC_PER_MINUTE).toInt()
            this.second = ((value % this.MSEC_PER_MINUTE) / this.MSEC_PER_SECOND).toInt()
            this.millisecond = (value % this.MSEC_PER_SECOND).toInt()
            field = value
        }

    constructor(
            day: Int = 0,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            millisecond: Int = 0
    ) : this(true, day, hour, minute, second, millisecond)

    constructor(
            positive: Boolean,
            day: Int = 0,
            hour: Int = 0,
            minute: Int = 0,
            second: Int = 0,
            millisecond: Int = 0
    ) {
        this.isPositive = positive
        this.day = day
        this.hour = hour
        this.minute = minute
        this.second = second
        this.millisecond = millisecond
        this.totalMilliseconds = (day * this.MSEC_PER_DAY + hour * this.MSEC_PER_HOUR
                + minute * this.MSEC_PER_MINUTE + second * this.MSEC_PER_SECOND + millisecond).toLong()
    }

    constructor(totalMilliseconds: Long) {
        this.isPositive = totalMilliseconds > 0
        this.totalMilliseconds = if (totalMilliseconds > 0) totalMilliseconds else -totalMilliseconds
    }

    override fun toString(): String {
        return if (isPositive)
            "$day $hour:$minute:$second.$millisecond"
        else
            "-$day $hour:$minute:$second.$millisecond"
    }
}
