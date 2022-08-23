package com.synebula.gaea.app.component.bus

import com.synebula.gaea.bus.Bus
import org.springframework.stereotype.Component

@Component
class EventBus : Bus<Any>()

