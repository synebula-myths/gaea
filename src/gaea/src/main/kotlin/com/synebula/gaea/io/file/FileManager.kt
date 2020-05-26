package com.synebula.gaea.io.file

import java.io.InputStream
import java.io.OutputStream

class FileManager:IFileManager {
    override fun write(name: String, stream: InputStream): String {
        TODO("Not yet implemented")
    }

    override fun read(path: String): OutputStream {
        TODO("Not yet implemented")
    }

    override fun rm(path: String): Boolean {
        TODO("Not yet implemented")
    }

    override fun mkdir(path: String, parents: Boolean) {
        TODO("Not yet implemented")
    }
}