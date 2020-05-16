package com.synebula.gaea.io.scan

/**
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年9月20日 上午10:50:06
 */
object ClassLoaderContext {
    /**
     * 获得class loader<br></br>
     * 若当前线程class loader不存在，取当前类的class loader
     *
     * @return 类加载器
     */
    fun get(): ClassLoader {
        var classLoader: ClassLoader? = Thread.currentThread().contextClassLoader
        if (classLoader == null) {
            classLoader = ClassLoaderContext::class.java.classLoader
        }
        return classLoader!!
    }
}
