package com.synebula.gaea.data.date

object TimeExchanger {
    /**
     * 转换率。分别对应：毫秒、秒、分、时、日
     */
    private val exchangeRate = arrayOf(1, 1000, 60, 60, 24)

    fun dayToHour(day: Int): Double {
        return this.exchange(TimeUnit.Day, TimeUnit.Hour, day.toLong())
    }

    fun dayToMinute(day: Int): Double {
        return this.exchange(TimeUnit.Day, TimeUnit.Minute, day.toLong())
    }

    fun dayToSecond(day: Int): Double {
        return this.exchange(TimeUnit.Day, TimeUnit.Second, day.toLong())
    }

    fun dayToMillisecond(day: Int): Double {
        return this.exchange(TimeUnit.Day, TimeUnit.Millisecond, day.toLong())
    }

    fun hourToDay(hour: Int): Double {
        return this.exchange(TimeUnit.Hour, TimeUnit.Day, hour.toLong())
    }

    fun hourToMinute(hour: Int): Double {
        return this.exchange(TimeUnit.Hour, TimeUnit.Minute, hour.toLong())
    }

    fun hourToSecond(hour: Int): Double {
        return this.exchange(TimeUnit.Hour, TimeUnit.Second, hour.toLong())
    }

    fun hourToMillisecond(hour: Int): Double {
        return this.exchange(TimeUnit.Hour, TimeUnit.Millisecond, hour.toLong())
    }

    fun minuteToDay(minute: Int): Double {
        return this.exchange(TimeUnit.Minute, TimeUnit.Day, minute.toLong())
    }

    fun minuteToHour(minute: Int): Double {
        return this.exchange(TimeUnit.Minute, TimeUnit.Hour, minute.toLong())
    }

    fun minuteToSecond(minute: Int): Double {
        return this.exchange(TimeUnit.Minute, TimeUnit.Second, minute.toLong())
    }

    fun minuteToMillisecond(minute: Int): Double {
        return this.exchange(TimeUnit.Minute, TimeUnit.Millisecond, minute.toLong())
    }

    fun secondToDay(second: Int): Double {
        return this.exchange(TimeUnit.Second, TimeUnit.Day, second.toLong())
    }

    fun secondToHour(second: Int): Double {
        return this.exchange(TimeUnit.Second, TimeUnit.Hour, second.toLong())
    }

    fun secondToMinute(second: Int): Double {
        return this.exchange(TimeUnit.Second, TimeUnit.Minute, second.toLong())
    }

    fun secondToMillisecond(second: Int): Double {
        return this.exchange(TimeUnit.Second, TimeUnit.Millisecond, second.toLong())
    }

    fun millisecondToDay(millisecond: Long): Double {
        return this.exchange(TimeUnit.Millisecond, TimeUnit.Day, millisecond)
    }

    fun millisecondToHour(millisecond: Long): Double {
        return this.exchange(TimeUnit.Millisecond, TimeUnit.Hour, millisecond)
    }

    fun millisecondToMinute(millisecond: Long): Double {
        return this.exchange(TimeUnit.Millisecond, TimeUnit.Minute, millisecond)
    }

    fun millisecondToSecond(millisecond: Long): Double {
        return this.exchange(TimeUnit.Millisecond, TimeUnit.Second, millisecond)
    }


    /**
     * 转换时间的单位
     */
    fun exchange(source: TimeUnit, target: TimeUnit, value: Long): Double {
        var result = value.toDouble()
        if (source.ordinal > TimeUnit.Day.ordinal || target.ordinal > TimeUnit.Day.ordinal)
            throw UnsupportedOperationException("can't exchange from or exchange to month or day!")
        if (source.ordinal < target.ordinal) {
            for (i in (source.ordinal + 1)..target.ordinal) { //由小单位向上转换，转换率需要向前上提一位
                result /= exchangeRate[i]
            }
        } else {
            for (i in source.ordinal downTo (target.ordinal + 1)) {
                result *= exchangeRate[i]
            }
        }
        return result
    }
}