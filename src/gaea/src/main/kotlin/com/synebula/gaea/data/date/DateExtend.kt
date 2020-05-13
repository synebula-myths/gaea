package com.synebula.gaea.data.date

import java.util.*


/**
 * 日期相减
 */
operator fun Date.minus(other: Date): TimeSpan {
    return TimeSpan(this.time - other.time)
}