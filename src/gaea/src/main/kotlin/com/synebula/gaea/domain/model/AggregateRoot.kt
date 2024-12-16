package com.synebula.gaea.domain.model

abstract class AggregateRoot<ID> : Entity<ID>(), IAggregateRoot<ID> {
    override var available: Boolean = true
}
