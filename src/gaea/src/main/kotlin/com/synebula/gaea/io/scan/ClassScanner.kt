package com.synebula.gaea.io.scan

import java.io.File
import java.io.FileFilter
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.nio.charset.Charset
import java.util.*
import java.util.jar.JarFile

/**
 * 包扫描组件
 * @author alex
 * @version 0.0.1
 * @since 2016年9月20日 下午2:56:50
 *
 * @param packageName 扫描的包名
 */
class ClassScanner(private var packageName: String) {
    /**
     * 类过滤器列表
     */
    private var classFilters: Array<out IClassFilter>? = null

    /**
     * 扫描的类
     */
    private val classes: MutableSet<Class<*>> = HashSet()

    /**
     * 文件过滤器
     */
    private val fileFilter =
        FileFilter { file -> file.isDirectory || file.name.endsWith(".class") || file.name.endsWith(".jar") }


    /**
     * 构造方法。
     *
     * @param packageName 需要扫描的包名。
     * @param classFilter
     */
    constructor(packageName: String, vararg classFilter: IClassFilter) : this(packageName) {
        this.classFilters = classFilter
    }

    /**
     * 开始扫描。
     *
     * @return
     */
    fun scan(): Collection<Class<*>> {
        // 如果类集合不为空，先清空。
        if (classes.isNotEmpty())
            classes.clear()

        var paths = ClassPath.get(packageName)// 先扫描当前类路径下文件
        this.scanPath(paths)
        if (classes.isEmpty()) {// 若当前类路径下文件没有,扫描java路径
            paths = ClassPath.get()
            this.scanPath(paths)
        }
        return classes
    }

    /*
	 * Private
	 *
	 */

    /**
     * 填充满足条件的class 填充到 classes<br></br>
     * 同时会判断给定的路径是否为Jar包内的路径，如果是，则扫描此Jar包
     *
     * @param paths Class文件路径或者所在目录Jar包路径
     */
    private fun scanPath(paths: Array<String>) {
        for (path in paths) {
            try {
                // 路径编码，防止由于路径中空格和中文导致的Jar找不到
                val realPath = URLDecoder.decode(path, Charset.defaultCharset().name())
                val files = scanDirectory(realPath)
                for (file in files) {
                    val fileName = file.toString()
                    if (fileName.contains(".jar") && !fileName.endsWith(".class")) {
                        scanJar(file)
                    }
//                    else if (fileName.endsWith(".class")) {
//                    }
                    scanClass(realPath, file)
                }
            } catch (ex: UnsupportedEncodingException) {
                throw ex
            }
        }
    }

    /**
     * 扫描文件，过滤添加类文件。
     *
     * @param path 扫描的磁盘路径
     */
    private fun scanDirectory(path: String): Collection<File> {
        var realPath = path
        val files = LinkedList<File>()
        val index = realPath.lastIndexOf(".jar")
        if (index != -1) {// 首先判path是不是jar包下，如果是说明当前path是文件
            // jar文件
            realPath = realPath.substring(0, index + 4)// 截取jar路径 [xxx/xxx.jar]
            if (realPath.startsWith("file:"))
                realPath = realPath.substring(5) // 去掉文件前缀[file:]
            files.add(File(realPath))
        } else {// 否则扫描文件夹下文件
            val directory = LinkedList<File>()
            directory.add(File(realPath))
            var list: Array<File>?
            var file: File
            while (directory.size != 0) {
                file = directory.poll()
                list = file.listFiles(fileFilter)
                if (list!!.isNotEmpty()) {
                    for (item in list) {
                        if (item.isDirectory)
                            directory.add(item)
                        else
                            files.add(item)
                    }
                }
            }
        }
        return files
    }

    /**
     * 扫描jar文件，过滤添加类文件。
     *
     * @param file 扫描的jar文件
     */
    private fun scanJar(file: File) {
        try {
            val jar = JarFile(file)
            val entries = jar.entries()
            val list = Collections.list(entries)
            for (entry in list) {
                if (entry.name.endsWith(".class")) {
                    val className = entry.name.replace("/", ".").replace(".class", "")
                    filterClass(className)
                }
            }
            jar.close()
        } catch (ex: Throwable) {
            throw ex
        }

    }

    /**
     * 扫描.class类文件，过滤添加类文件。
     *
     * @param path 路径
     * @param file 文件
     */
    private fun scanClass(path: String, file: File) {
        var realPath = path
        if (!realPath.endsWith(File.separator)) {
            realPath += File.separator
        }
        var absolutePath = file.absolutePath
        if (packageName.isEmpty())
            absolutePath = absolutePath.substring(realPath.length)

        val filePathWithDot = absolutePath.replace(File.separator, ".")
        val subIndex = filePathWithDot.indexOf(packageName)
        if (subIndex != -1) {
            val endIndex = filePathWithDot.lastIndexOf(".class")
            val className = filePathWithDot.substring(subIndex, endIndex)
            filterClass(className)
        }
    }

    /**
     * 过滤符合要求的类，添加到结果列表中。
     *
     * @param className 类名
     */
    private fun filterClass(className: String) {
        if (className.startsWith(packageName)) {
            try {
                val clazz = Class.forName(className, false, ClassLoaderContext.get())
                if (classFilters == null)
                    classes.add(clazz)
                else {
                    for (iClassFilter in classFilters!!) {
                        if (iClassFilter.accept(clazz))
                            classes.add(clazz)
                    }
                }
            } catch (ex: Throwable) {
                throw ex
            }

        }
    }

}
