package com.interminable.pipe.service

import com.interminable.pipe.config.PipeProperties
import com.interminable.pipe.entity.PipeProject
import com.interminable.pipe.entity.PipeResult
import com.interminable.pipe.entity.PipeTask
import com.interminable.pipe.repository.PipeProjectRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.io.File

@Service
class PipeProjectService(
        private val props: PipeProperties
) {

    @Autowired
    private lateinit var repository: PipeProjectRepository

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
    fun addTask(project: PipeProject, task: PipeTask) {
        project.tasks.add(task)
        repository.save(project)
    }

    /**
     * Deletes a task from a project
     */
    fun deleteTask(project: PipeProject, task: PipeTask) {
        project.tasks.remove(task)
        repository.save(project)
    }

    /**
     * Deletes a task with specified ID from a project
     */
    fun deleteTask(project: PipeProject, taskId: Int) {
        project.tasks.removeAt(taskId)
        repository.save(project)
    }

    /**
     * Sets a properties collection to a task of a project
     */
    fun setProperties(project: PipeProject, task: PipeTask, properties: MutableList<String>) {
        project.tasks[task.id].properties = properties
        repository.save(project)
    }

    /**
     * Sets a properties collection to a task of a project
     */
    fun setProperties(project: PipeProject, taskId: Int, properties: MutableList<String>) {
        project.tasks[taskId].properties = properties
        repository.save(project)
    }

    /**
     * Adds all properties from collection to a task in project
     */
    fun addProperties(project: PipeProject, task: PipeTask, properties: List<String>) {
        project.tasks[task.id].properties.addAll(properties)
        repository.save(project)
    }

    /**
     * Adds all properties from collection to a task in project
     */
    fun addProperties(project: PipeProject, taskId: Int, properties: List<String>) {
        project.tasks[taskId].properties.addAll(properties)
        repository.save(project)
    }

    /**
     * Changes coordinates of a task location in GUI
     */
    fun changeTaskLocation(project: PipeProject, task: PipeTask, x: Int, y: Int) {
        project.tasks[task.id].x_location = x
        project.tasks[task.id].y_location = y
        repository.save(project)
    }

    /**
     * Changes coordinates of a task location in GUI
     */
    fun changeTaskLocation(project: PipeProject, taskId: Int, x: Int, y: Int) {
        project.tasks[taskId].x_location = x
        project.tasks[taskId].y_location = y
        repository.save(project)
    }

    /**
     * Adds a link between two tasks
     */
    fun addLink(project: PipeProject, inputTask: PipeTask, outputTask: PipeTask) {
        if (project.links[inputTask.id] == null) project.links[inputTask.id] = mutableListOf(outputTask.id)
        else project.links[inputTask.id]!!.add(outputTask.id)
    }

    /**
     * Adds a link between two tasks
     */
    fun addLink(project: PipeProject, inputTaskId: Int, outputTaskId: Int) {
        if (project.links[inputTaskId] == null) project.links[inputTaskId] = mutableListOf(outputTaskId)
        else project.links[inputTaskId]!!.add(outputTaskId)
    }

    /**
     * Deletes a link between two tasks
     */
    fun deleteLink(project: PipeProject, inputTask: PipeTask, outputTask: PipeTask) {
        if (project.links[inputTask.id] != null) project.links[inputTask.id]!!.remove(outputTask.id)
    }

    /**
     * Deletes a link between two tasks
     */
    fun deleteLink(project: PipeProject, inputTaskId: Int, outputTaskId: Int) {
        if (project.links[inputTaskId] != null) project.links[inputTaskId]!!.remove(outputTaskId)
    }

    /**
     * Runs a workflow inside the project
     */
    fun run(projectName: String): PipeResult {
        TODO()
    }

    fun stop(projectName: String): PipeResult {
        TODO()
    }
}