package com.synebula.gaea.domain.repository.specifications

abstract class Specification<T> : ISpecification<T> {

    abstract override fun isSatisfiedBy(obj: T): Boolean

    override fun and(other: ISpecification<T>): ISpecification<T> {
        return AndSpecification(this, other)
    }

    override fun or(other: ISpecification<T>): ISpecification<T> {
        return OrSpecification(this, other)
    }

    override fun andNot(other: ISpecification<T>): ISpecification<T> {
        return AndNotSpecification(this, other)
    }

    override fun orNot(other: ISpecification<T>): ISpecification<T> {
        return OrNotSpecification(this, other)
    }

    override fun not(): ISpecification<T> {
        return NotSpecification(this)
    }

}
