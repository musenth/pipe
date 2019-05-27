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
