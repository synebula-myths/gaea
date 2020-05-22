package com.synebula.gaea.query

@Target(AnnotationTarget.FIELD)
annotation class Where(val operator: Operator)