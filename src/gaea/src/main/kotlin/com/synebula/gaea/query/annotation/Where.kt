package com.synebula.gaea.query.annotation

import com.synebula.gaea.query.type.Operator

/**
 * 字段注解，规定字段的查询方式
 *
 * @param operator 查询方式
 * @param sensitiveCase 是否大小写敏感，默认为true敏感(只在Operator.like下有效)。
 * @param children 是否查询子元素。如果为空("")查询本字段，否则查询本字段下的子元素。
 */
@Target(AnnotationTarget.FIELD)
annotation class Where(val operator: Operator, val sensitiveCase: Boolean = true, val children: String = "")