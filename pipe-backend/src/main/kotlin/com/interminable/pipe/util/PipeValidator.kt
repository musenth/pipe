package com.interminable.pipe.util

import com.interminable.pipe.entity.PipeProject
import com.interminable.pipe.entity.PipeResult
import com.interminable.pipe.entity.enums.PipeValidationStatus

/**
 * Provides methods for workflow validation
 */
class PipeValidator {

    /**
     * Checks if graph has
     */
    fun isGraphValid(project: PipeProject): Boolean {
        val links = project.links
        links.values.forEach { values ->
            values.forEach {
                if (!links.keys.contains(it)) return false
            }
        }
        return true
    }

    /**
     * Checks if graph has cycles or not
     */
    fun isGraphCycled(project: PipeProject): PipeResult {
        val links = project.links
        val visited = mutableMapOf<Int, Boolean>()
        val recStack = mutableMapOf<Int, Boolean>()

        links.forEach {
            visited[it.key] = false
            recStack[it.key] = false
        }

        val way = "${project.startTaskId}"

        return (isCyclicUtil(links, project.startTaskId, visited, recStack, way))
    }

    private fun isCyclicUtil(
            links: Map<Int, List<Int>>,
            i: Int,
            visited: MutableMap<Int, Boolean>,
            recStack: MutableMap<Int, Boolean>,
            way: String
    ): PipeResult {
        if (recStack[i]!!)
            return PipeResult(PipeValidationStatus.ERROR)

        if (visited[i]!!)
            return PipeResult(PipeValidationStatus.OK)

        visited[i] = true

        recStack[i] = true
        val children = links[i]

        for (c in children!!) {
            val newWay = "$way -> $c"
            if (isCyclicUtil(links, c, visited, recStack, newWay).status == PipeValidationStatus.ERROR)
                return PipeResult(
                        PipeValidationStatus.ERROR,
                        mutableListOf("Graph has a cycle: $newWay")
                )
        }

        recStack[i] = false

        return PipeResult()
    }
}