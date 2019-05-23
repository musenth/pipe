package com.interminable.pipe.service

import com.interminable.pipe.config.PipeProperties
import com.interminable.pipe.entity.PipeProject
import com.interminable.pipe.entity.PipeResult
import com.interminable.pipe.entity.PipeTask
import com.interminable.pipe.entity.enums.PipeProjectStatus
import com.interminable.pipe.entity.enums.PipeValidationStatus
import com.interminable.pipe.repository.PipeProjectRepository
import com.interminable.pipe.util.PipeTaskExecutor
import com.interminable.pipe.util.PipeValidator
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File
import java.time.OffsetDateTime
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.timer

@Service
class PipeProjectService(
        private val props: PipeProperties
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PipeProjectService::class.java)
    }

    @Autowired
    private lateinit var repository: PipeProjectRepository

    private val taskExecutor = PipeTaskExecutor()

    private val validator = PipeValidator()

    private val timers: MutableMap<String, Timer> = mutableMapOf()

    /**
     * Returns the list of projects
     */
    fun getAllProjects() = repository.findAll()

    /**
     * Chooses the project to edit
     */
    fun chooseProject(name: String) = repository.findByName(name)

    /**
     * Creates a project with given name
     */
    fun createProject(name: String): Boolean {
        val used = repository.findByName(name)
        if (used != null) return false
        repository.save(PipeProject(name = name))
        return true
    }

    /**
     * Saves the state of the project
     */
    fun saveProject(project: PipeProject) = repository.save(project)

    /**
     * Deletes the project
     */
    fun deleteProject(name: String) = repository.deleteByName(name)

    /**
     * Returns all available tasks loaded to the specified directory
     */
    fun showAllTasks(): List<String> = File(props.taskFilesLocation).listFiles().map { it.name }

    /**
     * Adds a task to a project
     */
    fun addTask(projectName: String, task: PipeTask) {
        val project = repository.findByName(projectName) ?: return
        project.tasks.add(task)
        repository.save(project)
    }

    /**
     * Deletes a task with specified ID from a project
     */
    fun deleteTask(projectName: String, taskId: Int) {
        val project = repository.findByName(projectName) ?: return
        project.tasks.removeAt(taskId)
        repository.save(project)
    }

    /**
     * Sets a properties collection to a task of a project
     */
    fun setProperties(projectName: String, taskId: Int, properties: MutableList<String>) {
        val project = repository.findByName(projectName) ?: return
        project.tasks[taskId].properties = properties
        repository.save(project)
    }

    /**
     * Adds all properties from collection to a task in project
     */
    fun addProperties(projectName: String, taskId: Int, properties: List<String>) {
        val project = repository.findByName(projectName) ?: return
        project.tasks[taskId].properties.addAll(properties)
        repository.save(project)
    }

    /**
     * Changes coordinates of a task location in GUI
     */
    fun changeTaskLocation(projectName: String, taskId: Int, x: Int, y: Int) {
        val project = repository.findByName(projectName) ?: return
        project.tasks[taskId].x_location = x
        project.tasks[taskId].y_location = y
        repository.save(project)
    }

    /**
     * Adds a link between two tasks
     */
    fun addLink(projectName: String, inputTaskId: Int, outputTaskId: Int) {
        val project = repository.findByName(projectName) ?: return
        if (project.links[inputTaskId]!!.isEmpty()) project.links[inputTaskId] = mutableListOf(outputTaskId)
        else project.links[inputTaskId]!!.add(outputTaskId)
    }

    /**
     * Deletes a link between two tasks
     */
    fun deleteLink(projectName: String, inputTaskId: Int, outputTaskId: Int) {
        val project = repository.findByName(projectName) ?: return
        if (project.links[inputTaskId]!!.isEmpty()) project.links[inputTaskId]!!.remove(outputTaskId)
    }

    /**
     * Runs a workflow inside the project
     */
    fun run(projectName: String): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("There is no project with name $projectName")
        )
        if (project.status == PipeProjectStatus.INVALID) return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project is invalid! Fix all of the problems and try again")
        )
        if (project.status == PipeProjectStatus.RUNNING) return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project is already running")
        )
        if (validator.isGraphCycled(project)) return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project's graph is cycled")
        )
        if (!validator.isGraphValid(project)) return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project's graph is invalid")
        )
        timers[projectName] = timer(
                projectName,
                false,
                Date.from(project.startDate.toInstant(OffsetDateTime.now().offset)),
                project.period
        ) {
            runDAG(project)
        }
        return PipeResult(PipeValidationStatus.OK)
    }

    fun stop(projectName: String): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("There is no project with name $projectName")
        )
        timers[projectName]?.cancel() ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("There is no running project with name $projectName")
        )
        project.status = PipeProjectStatus.READY
        return PipeResult(PipeValidationStatus.OK)
    }

    private fun runDAG(project: PipeProject) {
        val links = ConcurrentHashMap<Int, MutableList<Int>>()
        project.links.forEach {
            links[it.key] = it.value
        }
        recursiveProcessing(
                project.startTaskId,
                project,
                links
        )
    }

    private fun recursiveProcessing(
            id: Int,
            project: PipeProject,
            links: ConcurrentHashMap<Int, MutableList<Int>>
    ) {
        links.values.forEach {
            if (it.contains(id)) return
        }
        val task = project.tasks.first { x -> x.id == id }
        taskExecutor.execute(
                task.type,
                task.filePath,
                task.properties
        )
        if (links[id].isNullOrEmpty()) return
        runBlocking {
            val nextIds = mutableListOf<Int>()
            nextIds.addAll(links[id]!!)
            nextIds.forEach { index ->
                launch {
                    links[id]!!.removeAll {
                        it == index
                    }
                    recursiveProcessing(
                            index,
                            project,
                            links
                    )
                }
            }
        }
    }
}