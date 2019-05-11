package com.interminable.pipe.entity

import com.interminable.pipe.entity.enums.PipeValidationStatus

/**
 * Result of validation before execution of "run" command.
 * It is also a response body
 */
data class PipeResult(
        /**
         * Status of an action
         */
        var status: PipeValidationStatus = PipeValidationStatus.OK,

        /**
         * Error messages
         */
        val errors: MutableList<String> = mutableListOf()
)