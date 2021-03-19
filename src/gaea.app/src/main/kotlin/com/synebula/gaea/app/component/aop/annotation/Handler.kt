package com.synebula.gaea.app.component.aop.annotation

import kotlin.reflect.KClass

annotation class Handler(val value: KClass<Any>)