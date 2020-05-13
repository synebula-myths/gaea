package com.synebula.gaea.domain.repository.context

/**
 * 表示所有继承于该接口的类型都是Unit Of Work的一种实现。
 *
 * @author alex
 */
interface IUnitOfWork {

    /**
     * 获得一个boolean值，该值表述了当前的Unit Of Work事务是否已被提交。
     *
     * @return 返回Unit Of Work事务是否已被提交。
     */
    val isCommitted: Boolean

    /**
     * 提交当前的Unit Of Work事务。
     */
    fun commit()

    /**
     * 回滚当前的Unit Of Work事务。
     */
    fun rollback()
}
