package com.synebula.gaea.query.annotation

import com.synebula.gaea.query.type.Operator

@Target(AnnotationTarget.FIELD)
annotation class Where(val operator: Operator)