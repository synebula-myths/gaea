package com.synebula.gaea.domain.event

import com.synebula.gaea.data.message.IEvent
import com.synebula.gaea.domain.model.IAggregateRoot

class AfterRemoveEvent<T : IAggregateRoot<I>, I>(var id: I? = null) : IEvent
