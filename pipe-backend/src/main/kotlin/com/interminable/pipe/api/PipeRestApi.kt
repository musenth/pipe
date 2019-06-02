package com.interminable.pipe.api

import com.interminable.pipe.entity.PipeProject
import com.interminable.pipe.entity.PipeTask
import com.interminable.pipe.service.PipeProjectService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
class PipeRestApi {

    @Autowired
    private lateinit var service: PipeProjectService

    @GetMapping("/getAllProjects")
    fun getAllProjects() = service.getAllProjects()

    @GetMapping("/getProject")
    fun getProject(@RequestParam name: String) = service.getProject(name)

    @PutMapping("/changeStartDate/{name}/{startDate}")
    fun changeStartDate(
            @PathVariable name: String,
            @PathVariable startDate: String
    ) = service.changeStartDate(name, startDate)

    @PutMapping("/changePeriod/{name}/{period}")
    fun changePeriod(
            @PathVariable name: String,
            @PathVariable period: Long
    ) = service.changePeriod(name, period)

    @PutMapping("/changeStartId/{name}/{startId}")
    fun changeStartId(
            @PathVariable name: String,
            @PathVariable startId: Int
    ) = service.changeStartId(name, startId)

    @PostMapping("/createProject/{name}")
    fun createProject(@PathVariable name: String) = service.createProject(name)

    @PutMapping("/saveProject")
    fun saveProject(@RequestBody project: PipeProject) = service.saveProject(project)

    @DeleteMapping("/deleteProject/{name}")
    fun deleteProject(@PathVariable name: String) = service.deleteProject(name)

    @GetMapping("/getAllTasks")
    fun getAllTasks() = service.getAllTasks()

    // NOTE methods are called as "addSomething" but actually updates the project - that's why the PUT HTTP method is used

    @PutMapping("/addTask/{projectName}")
    fun addTask(
            @PathVariable projectName: String,
            @RequestBody task: PipeTask
    ) = service.addTask(projectName, task)

    @PutMapping("/deleteTask/{projectName}/{taskId}")
    fun deleteTask(
            @PathVariable projectName: String,
            @PathVariable taskId: Int
    ) = service.deleteTask(projectName, taskId)

    @PutMapping("/setProperties/{projectName}/{taskId}")
    fun setProperties(
            @PathVariable projectName: String,
            @PathVariable taskId: Int,
            @RequestBody properties: MutableList<String>
    ) = service.setProperties(projectName, taskId, properties)

    @PutMapping("/addProperties/{projectName}/{taskId}")
    fun addProperties(
            @PathVariable projectName: String,
            @PathVariable taskId: Int,
            @RequestBody properties: MutableList<String>
    ) = service.addProperties(projectName, taskId, properties)

    @PutMapping("/changeTaskLocation/{projectName}/{taskId}/{x}/{y}")
    fun changeTaskLocation(
            @PathVariable projectName: String,
            @PathVariable taskId: Int,
            @PathVariable x: Int,
            @PathVariable y: Int
    ) = service.changeTaskLocation(projectName, taskId, x, y)

    @PutMapping("/addLink/{projectName}/{inputId}/{outputId}")
    fun addLink(
            @PathVariable projectName: String,
            @PathVariable inputId: Int,
            @PathVariable outputId: Int
    ) = service.addLink(projectName, inputId, outputId)

    @PutMapping("/deleteLink/{projectName}/{inputId}/{outputId}")
    fun deleteLink(
            @PathVariable projectName: String,
            @PathVariable inputId: Int,
            @PathVariable outputId: Int
    ) = service.deleteLink(projectName, inputId, outputId)

    @PostMapping("/runProject/{projectName}")
    fun runProject(@PathVariable projectName: String) = service.run(projectName)

    @PostMapping("/stopProject/{projectName}")
    fun stopProject(@PathVariable projectName: String) = service.stop(projectName)
}