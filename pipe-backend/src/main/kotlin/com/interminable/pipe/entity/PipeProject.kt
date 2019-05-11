package com.interminable.pipe.entity

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import javax.validation.constraints.NotBlank

@Document(collection = "users")
data class PipeProject(
        /**
         * Project ID
         */
        @Id
        var id: String = "",

        /**
         * Project name
         */
        @NotBlank
        @Indexed
        var name: String,

        /**
         * Short description for this project
         */
        var description: String = "",

        /**
         * Tasks inside the project
         */
        var tasks: MutableList<PipeTask> = mutableListOf(),

        /**
         * Task links.
         *
         * Key and value - IDes of tasks
         */
        var links: MutableList<MutableList<Int>?> = mutableListOf()
)