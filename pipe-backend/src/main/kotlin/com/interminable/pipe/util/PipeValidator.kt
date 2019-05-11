package com.interminable.pipe.util

import com.interminable.pipe.entity.PipeProject

/**
 * Provides methods for workflow validation
 */
class PipeValidator {

    /**
     * Checks if graph has
     */
    fun isGraphValid(project: PipeProject): Boolean {
        val links = project.links
        links.forEach { values ->
            values.forEach {
                if (it >= links.size) return false
            }
        }
        return true
    }

    /**
     * Checks if graph has cycles or not
     */
    fun isGraphCycled(project: PipeProject): Boolean {
        val links = project.links
        val visited = BooleanArray(links.size)
        val recStack = BooleanArray(links.size)

        repeat(links.size) {
            if (isCyclicUtil(links, it, visited, recStack)) return true
        }

        return false
    }

    private fun isCyclicUtil(
            links: List<List<Int>>,
            i: Int,
            visited: BooleanArray,
            recStack: BooleanArray
    ): Boolean {
        if (recStack[i])
            return true

        if (visited[i])
            return false

        visited[i] = true

        recStack[i] = true
        val children = links[i]

        for (c in children)
            if (isCyclicUtil(links, c, visited, recStack))
                return true

        recStack[i] = false

        return false
    }
}