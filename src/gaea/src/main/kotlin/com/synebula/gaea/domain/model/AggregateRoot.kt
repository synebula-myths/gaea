package com.synebula.gaea.domain.model

abstract class AggregateRoot<ID> : Entity<ID>(), IAggregateRoot<ID> {
    override var alive: Boolean = true
}
