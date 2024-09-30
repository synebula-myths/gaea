package com.synebula.gaea.jpa

import com.synebula.gaea.db.query.IQuery
import com.synebula.gaea.db.query.Page
import com.synebula.gaea.db.query.Params
import com.synebula.gaea.jpa.proxy.method.resolver.PageMethodResolver
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.support.SimpleJpaRepository

@Suppress("UNCHECKED_CAST")
class JpaQuery(protected var entityManager: EntityManager) : IQuery {
    protected var repos = mutableMapOf<Class<*>, SimpleJpaRepository<*, *>>()


    override operator fun <TView, ID> get(id: ID, clazz: Class<TView>): TView? {
        val repo = this.getJpaRepository(clazz)
        val view = repo.findById(id!!)
        return if (view.isPresent) view.get() else null
    }


    /**
     * 根据实体类条件查询所有符合条件记录
     *`
     * @param params 查询条件。
     * @return 视图列表
     */
    override fun <TView> list(params: Map<String, String>?, clazz: Class<TView>): List<TView> {
        val repo = this.getJpaRepository(clazz)
        val spec = params?.toSpecification(clazz) as Specification<TView>
        return repo.findAll(spec)
    }

    /**
     * 根据条件查询符合条件记录的数量
     *
     * @param params 查询条件。
     * @return 数量
     */
    override fun <TView> count(params: Map<String, String>?, clazz: Class<TView>): Long {
        val repo = this.getJpaRepository(clazz)
        val spec = params?.toSpecification(clazz) as Specification<TView>
        return repo.count(spec)
    }

    /**
     * 根据实体类条件查询所有符合条件记录（分页查询）
     *
     * @param params 分页条件
     * @return 分页数据
     */
    override fun <TView> paging(params: Params, clazz: Class<TView>): Page<TView> {
        val repo = this.getJpaRepository(clazz)
        val p = PageMethodResolver("findAll", clazz).mappingArguments(arrayOf(params))
        val page = repo.findAll(p[0] as Specification<TView>, p[1] as Pageable)
        return Page(page.number + 1, page.size, page.totalElements, page.content)
    }

    /**
     * 查询条件范围内数据。
     * @param field 查询字段
     * @param params 查询条件
     *
     * @return 视图列表
     */
    override fun <TView> range(field: String, params: List<Any>, clazz: Class<TView>): List<TView> {
        // method proxy in JpaRepositoryProxy [SimpleJpaRepository]
        return emptyList()
    }

    private fun <TView> getJpaRepository(clazz: Class<TView>): SimpleJpaRepository<TView, in Any> {
        if (this.repos.isNotEmpty() && this.repos.containsKey(clazz)) {
            return this.repos[clazz] as SimpleJpaRepository<TView, Any>
        } else {
            val r = SimpleJpaRepository<TView, Any>(clazz, entityManager)
            this.repos[clazz] = r
            return r
        }
    }
}