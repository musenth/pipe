package com.interminable.pipe.entity

import com.interminable.pipe.entity.enums.PipeTaskType
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class PipeTask(

        /**
         * Task ID
         */
        @Id
        var id: Int = 0,

        /**
         * Location of the scrypt file
         */
        var filePath: String,

        /**
         * Horizontal position of the task in GUI
         */
        var x_location: Int,

        /**
         * Vertical position of the task in GUI
         */
        var y_location: Int,

        /**
         * Type of executed project
         */
        var type: PipeTaskType = PipeTaskType.BASH,

        /**
         * Properties of the task for execution
         */
        var properties: MutableList<String> = mutableListOf()
)