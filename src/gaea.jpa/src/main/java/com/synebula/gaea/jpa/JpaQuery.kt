package com.synebula.gaea.jpa

import com.synebula.gaea.query.IQuery
import com.synebula.gaea.query.Page
import com.synebula.gaea.query.Params
import jakarta.persistence.EntityManager
import org.springframework.data.jpa.repository.support.SimpleJpaRepository

class JpaQuery<TView, ID>(override var clazz: Class<TView>, entityManager: EntityManager) : IQuery<TView, ID> {
    protected var repo: SimpleJpaRepository<TView, ID>

    init {
        repo = SimpleJpaRepository<TView, ID>(clazz, entityManager)
    }

    override operator fun get(id: ID): TView? {
        val view = this.repo.findById(id)
        return if (view.isPresent) view.get() else null
    }


    /**
     * 根据实体类条件查询所有符合条件记录
     *`
     * @param params 查询条件。
     * @return 视图列表
     */
    override fun list(params: Map<String, String>?): List<TView> {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
        return emptyList()
    }

    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return 数量
     */
    override fun count(params: Map<String, String>?): Int {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
        return -1
    }

    /**
     * 根据实体类条件查询所有符合条件记录（分页查询）
     *
     * @param params 分页条件
     * @return 分页数据
     */
    override fun paging(params: Params): Page<TView> {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
        return Page()
    }

    /**
     * 查询条件范围内数据。
     * @param field 查询字段
     * @param params 查询条件
     *
     * @return 视图列表
     */
    override fun range(field: String, params: List<Any>): List<TView> {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
        return emptyList()
    }

}