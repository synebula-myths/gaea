package com.synebula.gaea.jpa

import com.synebula.gaea.data.date.DateTime
import com.synebula.gaea.query.Operator
import com.synebula.gaea.query.Where
import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.CriteriaQuery
import jakarta.persistence.criteria.Predicate
import jakarta.persistence.criteria.Root
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
fun Map<String, String>?.toSpecification(clazz: Class<*>): Specification<*> {
    val rangeStartSuffix = "[0]" //范围查询开始后缀
    val rangeEndSuffix = "[1]" //范围查询结束后缀
    return Specification<Any?> { root: Root<Any?>, _: CriteriaQuery<*>?, criteriaBuilder: CriteriaBuilder ->
        val predicates: MutableList<Predicate> = ArrayList()
        for (argumentName in this!!.keys) {
            if (this[argumentName] == null) continue
            var fieldName = argumentName
            var operator: Operator

            // 判断是否为range类型(范围内查询)
            var start = true
            if (fieldName.endsWith(rangeStartSuffix) || fieldName.endsWith(rangeEndSuffix)) {
                fieldName = fieldName.substring(fieldName.length - 3)
                if (fieldName.endsWith(rangeEndSuffix)) start = false
            }
            val field = clazz.getDeclaredField(fieldName)
            val where: Where = field.getDeclaredAnnotation(Where::class.java)
            operator = where.operator

            // 如果是范围内容, 判断是数值类型还是时间类型
            if (operator === Operator.Range) {
                if (clazz.getDeclaredField(fieldName).type != Date::class.java) {
                    operator = if (start) Operator.Gte else Operator.Lte
                }
            }
            var predicate: Predicate
            var digitalValue: Double
            try {
                when (operator) {
                    Operator.Ne -> predicate =
                        criteriaBuilder.notEqual(root.get<Any>(fieldName), this[fieldName]!!.toFieldType(field))

                    Operator.Lt -> {
                        digitalValue = this[fieldName]!!.tryToDigital(field)
                        predicate = criteriaBuilder.lessThan(root.get(fieldName), digitalValue)
                    }

                    Operator.Gt -> {
                        digitalValue = this[fieldName]!!.tryToDigital(field)
                        predicate = criteriaBuilder.greaterThan(root.get(fieldName), digitalValue)
                    }

                    Operator.Lte -> {
                        digitalValue = this[fieldName]!!.tryToDigital(field)
                        predicate = criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), digitalValue)
                    }

                    Operator.Gte -> {
                        digitalValue = this[fieldName]!!.tryToDigital(field)
                        predicate = criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), digitalValue)
                    }

                    Operator.Like -> predicate = criteriaBuilder.like(root.get(fieldName), "%${this[fieldName]}%")
                    Operator.Range -> {
                        predicate = if (start) {
                            criteriaBuilder.greaterThanOrEqualTo(root.get(fieldName), this[argumentName]!!)
                        } else {
                            criteriaBuilder.lessThanOrEqualTo(root.get(fieldName), this[argumentName]!!)
                        }
                    }

                    else -> predicate =
                        criteriaBuilder.equal(root.get<Any>(fieldName), this[fieldName]!!.toFieldType(field))
                }
                predicates.add(predicate)
            } catch (e: NoSuchFieldException) {
                throw Error(
                    "class [${field.declaringClass.name}] field [${field.name}] can't annotation [@Where(${operator.declaringJavaClass.simpleName}.${operator.name})]",
                    e
                )
            }
        }
        criteriaBuilder.and(*predicates.toTypedArray())
    }
}