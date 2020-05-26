package com.synebula.gaea.io.file

import java.io.InputStream
import java.io.OutputStream

interface IFileManager {

    /**
     * 写入文件
     * @param name 需要写入文件的短名称
     * @param stream 二进制文件流
     * @return 文件的全路径
     */
    fun write(name: String, stream: InputStream): String

    /**
     * 读取文件
     * @param path 需要读取文件的路径
     */
    fun read(path: String): OutputStream

    /**
     * 删除文件
     * @path 需要删除文件的路径
     */
    fun rm(path: String): Boolean

    /**
     * 创建目录
     * @param path 需要创建的目录
     * @param parents 如果没有父目录是否自动创建, 默认创建
     */
    fun mkdir(path: String, parents: Boolean = true)
}