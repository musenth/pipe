package com.interminable.pipe.entity

import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonIgnore
import com.interminable.pipe.entity.enums.PipeProjectStatus
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDateTime
import javax.validation.constraints.NotBlank

@Document(collection = "projects")
data class PipeProject(
        /**
         * Project ID
         */
        @Id
        @JsonIgnore
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
        var links: MutableMap<Int, MutableList<Int>> = mutableMapOf(),

        /**
         * The ready status of this project
         */
        var status: PipeProjectStatus = PipeProjectStatus.INVALID,

        /**
         * The time of the first tasks execution of this project
         */
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        var startDate: LocalDateTime = LocalDateTime.now(),

        /**
         * Period of the tasks execution in millis
         */
        var period: Long = 60000,

        /**
         * Id of the start task
         */
        var startTaskId: Int = 0
)