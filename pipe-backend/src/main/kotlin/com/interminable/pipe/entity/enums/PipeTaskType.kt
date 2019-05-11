package com.interminable.pipe.entity.enums

/**
 * Types of tasks that are supported by the project
 */
enum class PipeTaskType(
        var command: String
) {
    JAVA("java -jar "),
    BASH("./"),
    UNKNOWN("cat Cant't run this scrypt: ")
}