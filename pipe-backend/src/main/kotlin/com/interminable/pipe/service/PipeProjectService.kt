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
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Service
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.concurrent.timer

@Service
class PipeProjectService(
        private val props: PipeProperties
) : CommandLineRunner {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(PipeProjectService::class.java)
    }

    @Autowired
    private lateinit var repository: PipeProjectRepository

    private val taskExecutor = PipeTaskExecutor()

    private val validator = PipeValidator()

    private val timers: MutableMap<String, Timer> = mutableMapOf()

    /**
     * Repairing the state
     */
    override fun run(vararg args: String?) {
        repository.findAll()
                .filter {
                    it.status == PipeProjectStatus.RUNNING
                }
                .forEach {
                    it.status = PipeProjectStatus.READY
                    run(it)
                }
    }

    /**
     * Returns the list of projects
     */
    fun getAllProjects(): List<PipeProject> {
        LOGGER.info("Returned all projects")
        return repository.findAll()
    }

    /**
     * Changes the startDate field of the project
     */
    fun changeStartDate(projectName: String, startDate: String): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        project.startDate = formatter.parse(startDate, LocalDateTime::from)
        repository.save(project)
        LOGGER.info("Changed the startDate field of the project with the name '$projectName'")
        return PipeResult()
    }

    /**
     * Changes the period field of the project
     */
    fun changePeriod(projectName: String, period: Long): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        project.period = period
        repository.save(project)
        LOGGER.info("Changed the period field of the project with the name '$projectName'")
        return PipeResult()
    }

    /**
     * Changes the startTaskId field of the project
     */
    fun changeStartId(projectName: String, startId: Int): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        project.startTaskId = startId
        repository.save(project)
        LOGGER.info("Changed the startId field of the project with the name '$projectName'")
        return PipeResult()
    }

    /**
     * Returns the project to edit
     */
    fun getProject(name: String): PipeProject? {
        LOGGER.info("Returned the project with the name '$name'")
        return repository.findByName(name)
    }

    /**
     * Creates a project with given name
     */
    fun createProject(name: String): PipeResult {
        val used = repository.findByName(name)
        if (used != null) {
            return PipeResult(
                    PipeValidationStatus.ERROR,
                    mutableListOf("The project with the name '$name' already exists")
            )
        }
        repository.save(PipeProject(name = name))
        LOGGER.info("Created the project with the name $name")
        return PipeResult()
    }

    /**
     * Saves the state of the project
     */
    fun saveProject(project: PipeProject): PipeResult {
        repository.save(project)
        LOGGER.info("Saved the project with the name '${project.name}'")
        return PipeResult()
    }

    /**
     * Deletes the project
     */
    fun deleteProject(name: String): PipeResult {
        repository.deleteByName(name)
        LOGGER.info("Removed the project with the name $name")
        return PipeResult()
    }

    /**
     * Returns all available tasks loaded to the specified directory
     */
    fun getAllTasks(): List<String> {
        LOGGER.info("Returned all tasks from the directory ${props.taskFilesLocation}")
        return File(props.taskFilesLocation).listFiles().map { it.absolutePath }
    }

    /**
     * Adds a task to a project
     */
    fun addTask(projectName: String, task: PipeTask): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        project.tasks.add(task)
        repository.save(project)
        LOGGER.info("Added the task with id '${task.id}' to the project with the name '$projectName'")
        return PipeResult()
    }

    /**
     * Deletes a task with specified ID from a project
     */
    fun deleteTask(projectName: String, taskId: Int): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        project.tasks.removeAt(taskId)
        repository.save(project)
        LOGGER.info("Deleted the task with id '$taskId' to the project with the name '$projectName'")
        return PipeResult()
    }

    /**
     * Sets a properties collection to a task of a project
     */
    fun setProperties(projectName: String, taskId: Int, properties: MutableList<String>): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        project.tasks[taskId].properties = properties
        repository.save(project)
        LOGGER.info("Set properties: $properties to the task with id '$taskId' in project with the name '$projectName'")
        return PipeResult()
    }

    /**
     * Adds all properties from collection to a task in project
     */
    fun addProperties(projectName: String, taskId: Int, properties: List<String>): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        project.tasks[taskId].properties.addAll(properties)
        repository.save(project)
        LOGGER.info("Added properties: $properties to the task with id '$taskId' in project with the name '$projectName'")
        return PipeResult()
    }

    /**
     * Changes coordinates of a task location in GUI
     */
    fun changeTaskLocation(projectName: String, taskId: Int, x: Int, y: Int): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        val oldX = project.tasks[taskId].x_location
        val oldY = project.tasks[taskId].y_location
        project.tasks[taskId].x_location = x
        project.tasks[taskId].y_location = y
        repository.save(project)
        LOGGER.info("Changed the task (id '$taskId') location from {$oldX, $oldY} to {$x, $y}")
        return PipeResult()
    }

    /**
     * Adds a link between two tasks
     */
    fun addLink(projectName: String, inputTaskId: Int, outputTaskId: Int): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        if (project.links[inputTaskId].isNullOrEmpty()) project.links[inputTaskId] = mutableListOf(outputTaskId)
        else project.links[inputTaskId]!!.add(outputTaskId)
        repository.save(project)
        LOGGER.info("Added a link between tasks with IDs $inputTaskId and $outputTaskId in the project with the name '$projectName'")
        return PipeResult()
    }

    /**
     * Deletes a link between two tasks
     */
    fun deleteLink(projectName: String, inputTaskId: Int, outputTaskId: Int): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project with the name '$projectName' not found")
        )
        if (project.links[inputTaskId]!!.isNotEmpty()) project.links[inputTaskId]!!.remove(outputTaskId)
        repository.save(project)
        LOGGER.info("Deleted a link between tasks with IDs $inputTaskId and $outputTaskId in the project with the name '$projectName'")
        return PipeResult()
    }

    fun run(project: PipeProject): PipeResult {
        if (project.status == PipeProjectStatus.RUNNING) return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project is already running")
        )
        val result = validator.isGraphCycled(project)
        if (result.status == PipeValidationStatus.ERROR) return result
        if (!validator.isGraphValid(project)) return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("Project's graph is invalid")
        )
        timers[project.name] = timer(
                project.name,
                false,
                Date.from(project.startDate.toInstant(OffsetDateTime.now().offset)),
                project.period
        ) {
            runDAG(project)
        }
        project.status = PipeProjectStatus.RUNNING
        repository.save(project)
        return PipeResult(PipeValidationStatus.OK)
    }

    /**
     * Runs a workflow inside the project
     */
    fun run(projectName: String): PipeResult {
        val project = repository.findByName(projectName) ?: return PipeResult(
                PipeValidationStatus.ERROR,
                mutableListOf("There is no project with name $projectName")
        )
        return run(project)
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
        repository.save(project)
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