package com.synebula.gaea.domain.model

abstract class AggregateRoot<ID> : Entity<ID>(), IAggregateRoot<ID> {
    override var avalible: Boolean = true
}
