package com.interminable.pipe.util

import com.interminable.pipe.entity.enums.PipeTaskType

interface TaskExecutor {

    /**
     * Executes the scrypt
     *
     * @return logs of the executed task
     */
    fun execute(
            type: PipeTaskType,
            path: String,
            properties: List<String>
    ): String
}