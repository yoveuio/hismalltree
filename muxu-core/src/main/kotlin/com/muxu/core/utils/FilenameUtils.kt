package com.muxu.core.utils

import com.muxu.core.annotation.Slf4j
import kotlin.math.max

@Suppress("unused")
@Slf4j
class FilenameUtils private constructor() {

    companion object {

        fun getExtension(filename: String?): String? {
            return filename?.let {
                val index: Int = indexOfExtension(filename)
                if (index == -1) "" else filename.substring(index + 1)
            }
        }

        fun indexOfExtension(filename: String?): Int {
            return filename?.let {
                val extensionPos = filename.lastIndexOf(46.toChar())
                val lastSeparator: Int = indexOfLastSeparator(filename)
                if (lastSeparator > extensionPos) -1 else extensionPos
            } ?: -1
        }

        fun indexOfLastSeparator(filename: String?): Int {
            return filename?.let {
                val lastUnixPos = filename.lastIndexOf(47.toChar())
                val lastWindowsPos = filename.lastIndexOf(92.toChar())
                max(lastUnixPos.toDouble(), lastWindowsPos.toDouble()).toInt()
            } ?: -1
        }

    }

}