package com.interminable.pipe.util

import com.interminable.pipe.entity.enums.PipeTaskType
import org.apache.commons.io.IOUtils
import java.nio.charset.Charset


class PipeTaskExecutor : TaskExecutor {

    override fun execute(
            type: PipeTaskType,
            path: String,
            properties: List<String>
    ): String {
        var command = "${type.command}$path"
        properties.forEach { command += " $it" }
        val result = Runtime.getRuntime().exec(command)
        return IOUtils.toString(result.inputStream, Charset.defaultCharset())
    }
}