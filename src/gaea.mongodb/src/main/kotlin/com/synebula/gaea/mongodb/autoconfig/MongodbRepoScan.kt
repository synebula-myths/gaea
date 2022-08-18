package com.synebula.gaea.mongodb.autoconfig

import org.springframework.context.annotation.Import
import java.lang.annotation.Inherited


@Target(AnnotationTarget.ANNOTATION_CLASS, AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@Inherited
@Import(MongodbRepoRegister::class)
annotation class MongodbRepoScan(val basePackages: Array<String> = [])