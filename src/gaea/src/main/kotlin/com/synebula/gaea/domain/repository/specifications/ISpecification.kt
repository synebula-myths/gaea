package com.synebula.gaea.domain.repository.specifications

/**
 * 声明规约对象的公共接口。
 *
 * @author alex
 *
 * @param T 规约对象的类型。
 */
interface ISpecification<T> {
    /**
     * 判断是否符合条件。
     *
     * @param obj 规约类型的对象。
     * @return 符合条件的返回True。
     */
    fun isSatisfiedBy(obj: T): Boolean

    /**
     * 结合当前规约对象和另一规约对象进行与判断。
     *
     * @param other 需要进行与结合的另一个规约对象。
     * @return 结合后的规约对象。
     */
    fun and(other: ISpecification<T>): ISpecification<T>

    /**
     * 结合当前规约对象和另一规约对象进行或判断。
     *
     * @param other 需要进行或结合的另一个规约对象。
     * @return 结合后的规约对象。
     */
    fun or(other: ISpecification<T>): ISpecification<T>

    /**
     * 结合当前规约对象和另一规约对象进行与且非判断
     *
     * @param other 需要进行非结合的另一个规约对象。
     * @return 结合后的规约对象。
     */
    fun andNot(other: ISpecification<T>): ISpecification<T>

    /**
     * 结合当前规约对象和另一规约对象进行或非判断。
     *
     * @param other 需要进行或非结合的另一个规约对象。
     * @return 结合后的规约对象。
     */
    fun orNot(other: ISpecification<T>): ISpecification<T>

    /**
     * 反转当前规约的判断结果。
     *
     * @return 反转后的规约对象。
     */
    operator fun not(): ISpecification<T>
}
