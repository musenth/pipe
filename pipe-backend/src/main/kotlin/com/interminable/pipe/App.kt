package com.interminable.pipe

import com.interminable.pipe.config.PipeProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(PipeProperties::class)
class App

fun main(args: Array<String>) {
    runApplication<App>(*args)
}

// TASKS:
// TODO (1) Add validation and PipeResult to task-interactive methods
// TODO (2) Complete run and stop methods
// TODO (3) Code tests for Service-layer
// TODO (4) Add REST API