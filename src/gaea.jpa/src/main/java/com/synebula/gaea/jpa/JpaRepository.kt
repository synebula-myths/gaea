package com.synebula.gaea.jpa

import com.synebula.gaea.domain.model.IAggregateRoot
import com.synebula.gaea.domain.repository.IRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import javax.persistence.EntityManager


class JpaRepository<TAggregateRoot : IAggregateRoot<ID>, ID>(
    override var clazz: Class<TAggregateRoot>,
    entityManager: EntityManager
) : IRepository<TAggregateRoot, ID> {
    protected var repo: JpaRepository<TAggregateRoot, ID>? = null

    init {
        repo = SimpleJpaRepository(clazz, entityManager)
    }

    /**
     * 插入单个对象。
     *
     * @param obj 需要插入的对象。
     * @return 返回原对象，如果对象ID为自增，则补充自增ID。
     */
    override fun add(obj: TAggregateRoot) {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
    }

    /**
     * 插入多个个对象。
     *
     * @param list 需要插入的对象。
     * @return 返回原对象，如果对象ID为自增，则补充自增ID。
     */
    override fun add(list: List<TAggregateRoot>) {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
    }

    /**
     * 更新对象。
     *
     * @param obj 需要更新的对象。
     * @return
     */
    override fun update(obj: TAggregateRoot) {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
    }

    /**
     * 更新多个个对象。
     *
     * @param list 需要新的对象。
     */
    override fun update(list: List<TAggregateRoot>) {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
    }

    /**
     * 通过id删除该条数据
     *
     * @param id 对象ID。
     * @return
     */
    override fun remove(id: ID) {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
    }

    /**
     * 根据ID获取对象。
     *
     * @param id 对象ID。
     * @return
     */
    override fun get(id: ID): TAggregateRoot? {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
        return null
    }

    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return int
     */
    override fun count(params: Map<String, String>?): Int {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
        return -1
    }

}