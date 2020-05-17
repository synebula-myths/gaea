package com.synebula.gaea.domain.model

abstract class AggregateRoot<TKey> : Entity<TKey>(), IAggregateRoot<TKey> {
    override var alive: Boolean = true
}
