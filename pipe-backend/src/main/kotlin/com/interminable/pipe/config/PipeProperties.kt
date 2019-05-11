package com.interminable.pipe.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "pipe")
data class PipeProperties(
        var taskFilesLocation: String = "~/.pipe"
)
