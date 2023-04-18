package com.synebula.gaea.jpa

import com.synebula.gaea.data.date.DateTime
import com.synebula.gaea.query.Operator
import com.synebula.gaea.query.Where
import jakarta.persistence.criteria.*
import org.springframework.data.jpa.domain.Specification
import java.lang.reflect.Field
import java.util.*


/**
 * 类型转换
 *
 * @param field 字段对象
 * @return object
 */
fun String.toFieldType(field: Field): Any? {
    var result: Any? = this
    val fieldType = field.type
    if (fieldType != this.javaClass) {
        if (Int::class.java == fieldType || Int::class.javaPrimitiveType == fieldType) {
            result = this.toInt()
        }
        if (Double::class.java == fieldType || Double::class.javaPrimitiveType == fieldType) {
            result = this.toDouble()
        }
        if (Float::class.java == fieldType || Float::class.javaPrimitiveType == fieldType) {
            result = this.toFloat()
        }
        if (Date::class.java == fieldType) {
            result = DateTime(this, "yyyy-MM-dd HH:mm:ss").date
        }
    }
    return result
}

/**
 * 格式化数值类型
 *
 * @param field 字段对象
 * @return double
 */
fun String.tryToDigital(field: Field): Double {
    val result: Double
    val fieldType = field.type
    result =
        if (Int::class.java == fieldType || Int::class.javaPrimitiveType == fieldType
            || Double::class.java == fieldType || Double::class.javaPrimitiveType == fieldType
            || Float::class.java == fieldType || Float::class.javaPrimitiveType == fieldType
        ) {
            this.toDouble()
        } else throw RuntimeException(
            String.format(
                "class [%s] field [%s] is not digital",
                field.declaringClass.name,
                field.name
            )
        )
    return result
}

/**
 * 参数 Map 转换查询 Specification
 *
 * @param clazz 类
 * @return Specification
 */
fun Map<String, String>.toSpecification(clazz: Class<*>): Specification<*> {
    val rangeStartSuffix = "[0]" //范围查询开始后缀
    val rangeEndSuffix = "[1]" //范围查询结束后缀
    return Specification<Any?> { root: Root<Any?>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
        val predicates = mutableListOf<Predicate>()
        for (argumentName in this.keys) {
            try {
                var fieldName = argumentName
                val fieldValue = this[argumentName]!!
                var operator: Operator = Operator.Default

                // 判断是否为range类型(范围内查询)
                var start = true
                if (fieldName.endsWith(rangeStartSuffix) || fieldName.endsWith(rangeEndSuffix)) {
                    fieldName = fieldName.substring(fieldName.length - 3)
                    if (fieldName.endsWith(rangeEndSuffix)) start = false
                }
                val fieldTree = fieldName.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                //查找是否是嵌入字段， 找到最深的类型
                var field: Field
                if (fieldTree.isNotEmpty()) {
                    var hostClass = clazz //需要查找字段所在的class
                    var i = 0
                    do {
                        field = hostClass.getDeclaredField(fieldTree[i])
                        hostClass = field.type
                        i++
                    } while (i < fieldTree.size)
                } else {
                    field = clazz.getDeclaredField(fieldName)
                }
                val where = field.getDeclaredAnnotation(Where::class.java)
                if (where != null) operator = where.operator

                // 如果是范围内容, 判断是数值类型还是时间类型
                if (operator === Operator.Range) {
                    if (field.type != Date::class.java) {
                        operator = if (start) Operator.Gte else Operator.Lte
                    }
                }
                var predicate: Predicate
                var digitalValue: Double
                when (operator) {
                    Operator.Ne -> predicate = criteriaBuilder.notEqual(
                        getFieldPath<Any>(root, fieldName),
                        typeConvert(field, fieldValue)
                    )

                    Operator.Lt -> try {
                        digitalValue = parseDigital(field, fieldValue)
                        predicate = criteriaBuilder.lessThan(getFieldPath(root, fieldName), digitalValue)
                    } catch (e: Exception) {
                        throw RuntimeException(
                            String.format(
                                "class [%s] field [%s] can not use annotation Where(Operator.lt)",
                                field.declaringClass.name,
                                field.name
                            ), e
                        )
                    }

                    Operator.Gt -> try {
                        digitalValue = parseDigital(field, fieldValue)
                        predicate = criteriaBuilder.greaterThan(
                            getFieldPath(
                                root,
                                fieldName
                            ), digitalValue
                        )
                    } catch (e: Exception) {
                        throw RuntimeException(
                            String.format(
                                "class [%s] field [%s] can not use annotation Where(Operator.gt)",
                                field.declaringClass.name,
                                field.name
                            ), e
                        )
                    }

                    Operator.Lte -> try {
                        digitalValue = parseDigital(field, fieldValue)
                        predicate = criteriaBuilder.lessThanOrEqualTo(
                            getFieldPath(
                                root,
                                fieldName
                            ), digitalValue
                        )
                    } catch (e: Exception) {
                        throw RuntimeException(
                            String.format(
                                "class [%s] field [%s] can not use annotation Where(Operator.lte)",
                                field.declaringClass.name,
                                field.name
                            ), e
                        )
                    }

                    Operator.Gte -> try {
                        digitalValue = parseDigital(field, fieldValue)
                        predicate = criteriaBuilder.greaterThanOrEqualTo(
                            getFieldPath(
                                root,
                                fieldName
                            ), digitalValue
                        )
                    } catch (e: Exception) {
                        throw RuntimeException(
                            String.format(
                                "class [%s] field [%s] can not use annotation Where(Operator.gte)",
                                field.declaringClass.name,
                                field.name
                            ), e
                        )
                    }

                    Operator.Like -> predicate = criteriaBuilder.like(
                        getFieldPath(root, fieldName),
                        String.format("%%%s%%", fieldValue)
                    )

                    Operator.Range -> predicate = if (start) criteriaBuilder.greaterThanOrEqualTo(
                        getFieldPath(root, fieldName), this[argumentName]!!
                    ) else criteriaBuilder.lessThanOrEqualTo(
                        getFieldPath(root, fieldName), this[argumentName]!!
                    )

                    else -> predicate = criteriaBuilder.equal(
                        getFieldPath<Any>(root, fieldName),
                        typeConvert(field, fieldValue)
                    )
                }
                predicates.add(predicate)
            } catch (e: NoSuchFieldException) {
                throw RuntimeException(e)
            }
        }
        criteriaBuilder.and(*predicates.toTypedArray())
    }
}

/**
 * 获取字段在
 */
fun <Y> getFieldPath(root: Root<Any?>, field: String): Path<Y>? {
    val fieldTree = field.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    var path: Path<Y>
    if (fieldTree.isNotEmpty()) {
        path = root.get(fieldTree[0])
        for (i in 1 until fieldTree.size) {
            path = path.get(fieldTree[i])
        }
        return path
    }
    return root.get(field)
}


/**
 * 类型转换
 *
 * @param field 字段对象
 * @param value 值
 * @return object
 */
fun typeConvert(field: Field, value: String): Any {
    var result: Any = value
    val fieldType = field.type
    if (fieldType != value.javaClass) {
        if (Int::class.java == fieldType || Int::class.javaPrimitiveType == fieldType) {
            result = value.toInt()
        }
        if (Double::class.java == fieldType || Double::class.javaPrimitiveType == fieldType) {
            result = value.toDouble()
        }
        if (Float::class.java == fieldType || Float::class.javaPrimitiveType == fieldType) {
            result = value.toFloat()
        }
        if (Date::class.java == fieldType) {
            result = DateTime(value, "yyyy-MM-dd HH:mm:ss").date
        }
    }
    return result
}

/**
 * 格式化数值类型
 *
 * @param field 字段对象
 * @param value 值
 * @return double
 */
fun parseDigital(field: Field, value: String): Double {
    val result: Double
    val fieldType = field.type
    result =
        if (Int::class.java == fieldType || Int::class.javaPrimitiveType == fieldType || Double::class.java == fieldType || Double::class.javaPrimitiveType == fieldType || Float::class.java == fieldType || Float::class.javaPrimitiveType == fieldType) {
            value.toDouble()
        } else throw java.lang.RuntimeException(
            String.format(
                "class [%s] field [%s] is not digital",
                field.declaringClass.name,
                field.name
            )
        )
    return result
}
