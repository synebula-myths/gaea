package com.synebula.gaea.domain.model.complex

abstract class ComplexAggregateRoot<TKey, TSecond> : ComplexEntity<TKey, TSecond>(), IComplexAggregateRoot<TKey, TSecond>
