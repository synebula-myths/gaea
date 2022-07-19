package com.synebula.gaea.io.scan

import java.io.IOException
import java.net.URL
import java.util.*

/**
 *
 * @author alex
 * @version 0.0.1
 * @since 2016年9月20日 下午2:59:47
 */
object ClassPath {

    /**
     * 获得Java ClassPath路径
     * @return 获得Java ClassPath路径，不包括 jre
     */
    fun get(): Array<String> {
        val paths = System.getProperty("java.class.path")
        val separator = System.getProperty("path.separator")
        return paths.split(separator.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    /**
     * 获得ClassPath
     *
     * @param packageName 包名称
     * @return ClassPath路径字符串集合
     */
    fun get(packageName: String): Array<String> {
        val paths = HashSet<String>()
        var path = packageName.replace(".", "/")
        // 判断路径最后一个字符是不是"/"，如果不是加上
        path = if (path.lastIndexOf("/") != path.length - 1) "$path/" else path

        val resources: Enumeration<URL>?
        try {
            resources = ClassLoaderContext.get().getResources(path)
        } catch (ex: IOException) {
            throw RuntimeException(ex)
        }

        while (resources!!.hasMoreElements()) {
            paths.add(resources.nextElement().path)
        }
        return paths.toTypedArray()
    }
}
