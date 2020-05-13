package com.synebula.gaea.domain.model.complex

import com.synebula.gaea.domain.model.IEntity

interface IComplexEntity<TKey, TSecond> : IEntity<TKey> {
    var secondary: TSecond
}
