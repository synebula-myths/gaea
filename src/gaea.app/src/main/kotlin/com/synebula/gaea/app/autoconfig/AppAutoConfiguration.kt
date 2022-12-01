package com.synebula.gaea.app.autoconfig

import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan(basePackages = ["com.synebula.gaea.app.bus", "com.synebula.gaea.app.component", "com.synebula.gaea.app.security"])
class AppAutoConfiguration