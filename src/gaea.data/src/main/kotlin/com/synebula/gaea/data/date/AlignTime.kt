package com.synebula.gaea.data.date

import java.util.*

/**
 * 校准时间。
 * 根据标准时间和间隔时间，获取就近的校准时间。
 * 如：2019-1-1 10:10:25, 根据标准时间2019-1-1 10:10:00按40s的间隔会校准为2019-1-1 10:10:40.
 */
class AlignTime {
    /**
     * 间隔时间，默认为1s
     */
    var intervalSeconds = 1

    /**
     * 标准时间。默认为2018-1-1 0:0:0
     */
    var baseTime = DateTime(2018, 0, 1, 0, 0, 0)

    constructor(intervalSeconds: Int) {
        this.intervalSeconds = intervalSeconds
    }

    constructor(baseTime: DateTime) {
        this.baseTime = baseTime
    }

    constructor(baseTime: DateTime, intervalSeconds: Int) {
        this.baseTime = baseTime
        this.intervalSeconds = intervalSeconds
    }

    /**
     * 获取就近的校准时间。
     * 如果时间刚好是正点时间，则输出该时间。如果不是正点时间则输出大于该时间的校准正点时间。
     */
    fun ceilingTime(): DateTime {
        return ceilingTime(DateTime(Date()))
    }

    /**
     * 获取就近的校准时间。
     * 如果时间刚好是正点时间，则输出该时间。如果不是正点时间则输出大于该时间的校准正点时间。
     */
    fun ceilingTime(lastTime: DateTime): DateTime {
        return this.ceilingTime(lastTime, this.intervalSeconds)
    }

    /**
     * 获取就近的校准时间。
     * 如果时间刚好是正点时间，则输出该时间。如果不是正点时间则输出大于该时间的校准正点时间。
     */
    fun ceilingTime(intervalSeconds: Int): DateTime {
        return this.ceilingTime(this.baseTime, intervalSeconds)
    }

    /**
     * 获取就近的校准时间。
     * 如果时间刚好是正点时间，则输出该时间。如果不是正点时间则输出大于该时间的校准正点时间。
     */
    fun ceilingTime(lastTime: DateTime, intervalSeconds: Int): DateTime {
        val span = lastTime - this.baseTime
        val count = Math.ceil(span.totalSeconds / intervalSeconds).toInt()
        val newTime = DateTime(this.baseTime.date)
        newTime.addSeconds(count * intervalSeconds * 1L)
        return newTime
    }

    /**
     * 获取就近的校准时间。
     * 如果时间刚好是正点时间，则输出该时间。如果不是正点时间则输出小于该时间的校准正点时间。
     */
    fun floorTime(): DateTime {
        return floorTime(DateTime(Date()))
    }

    /**
     * 获取就近的校准时间。
     * 如果时间刚好是正点时间，则输出该时间。如果不是正点时间则输出小于该时间的校准正点时间。
     */
    fun floorTime(lastTime: DateTime): DateTime {
        return this.floorTime(lastTime, this.intervalSeconds)
    }

    /**
     * 获取就近的校准时间。
     * 如果时间刚好是正点时间，则输出该时间。如果不是正点时间则输出小于该时间的校准正点时间。
     */
    fun floorTime(intervalSeconds: Int): DateTime {
        return this.floorTime(this.baseTime, intervalSeconds)
    }

    /**
     * 获取就近的校准时间。
     * 如果时间刚好是正点时间，则输出该时间。如果不是正点时间则输出小于该时间的校准正点时间。
     */
    fun floorTime(lastTime: DateTime, intervalSeconds: Int): DateTime {
        val span = lastTime - this.baseTime
        val count = Math.floor(span.totalSeconds / intervalSeconds).toInt()
        val newTime = DateTime(this.baseTime.date)
        newTime.addSeconds(count * intervalSeconds * 1L)
        return newTime
    }
}