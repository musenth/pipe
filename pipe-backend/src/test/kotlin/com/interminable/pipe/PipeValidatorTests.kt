package com.interminable.pipe

import com.interminable.pipe.entity.PipeProject
import com.interminable.pipe.entity.enums.PipeValidationStatus
import com.interminable.pipe.util.PipeValidator
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PipeValidatorTests {

    private val underTest = PipeValidator()

    @Test
    @DisplayName("1. Если граф не содержит связей с несуществующими вершинами, возвращается true")
    fun t1_isGraphValid_withValidGraph_mustReturnTrue() {
        val links = mutableMapOf(
                0 to mutableListOf(1, 2),
                1 to mutableListOf(0),
                2 to mutableListOf(0, 1)
        )
        val project = PipeProject(
                name = "",
                links = links
        )
        assertTrue(underTest.isGraphValid(project))
    }

    @Test
    @DisplayName("2. Если граф содержит вершины, которые не существуют, возвращается false")
    fun t2_isGraphValid_withInvalidGraph_mustReturnFalse() {
        val links = mutableMapOf(
                0 to mutableListOf(1, 2),
                1 to mutableListOf(0, 6),
                2 to mutableListOf(0, 3)
        )
        val project = PipeProject(
                name = "",
                links = links
        )
        assertFalse(underTest.isGraphValid(project))
    }

    @Test
    @DisplayName("3. Если граф содержит циклы, проверка на циклы возвращает true")
    fun t3_isGraphCycled_withCycledGraph_mustReturnTrue() {
        val links = mutableMapOf(
                0 to mutableListOf(1, 2),
                1 to mutableListOf(0),
                2 to mutableListOf(0, 1)
        )
        val project = PipeProject(
                name = "",
                links = links
        )
        val result = underTest.isGraphCycled(project)
        assertEquals(PipeValidationStatus.ERROR, result.status)
        println(result.errors)
    }

    @Test
    @DisplayName("4. Если в графе есть вершины, которые связаны с собой, проверка на циклы возвращает true")
    fun t4_isGraphCycled_withCycledGraph_mustReturnTrue() {
        val links = mutableMapOf(
                0 to mutableListOf(0)
        )
        val project = PipeProject(
                name = "",
                links = links
        )
        val result = underTest.isGraphCycled(project)
        assertEquals(PipeValidationStatus.ERROR, result.status)
        println(result.errors)
    }

    @Test
    @DisplayName("5. Если граф не содержит циклы, проверка на циклы возвращает false")
    fun t5_isGraphCycled_withCycledGraph_mustReturnFalse() {
        val links = mutableMapOf(
                0 to mutableListOf(1),
                1 to mutableListOf(2),
                2 to mutableListOf()
        )
        val project = PipeProject(
                name = "",
                links = links
        )
        assertEquals(PipeValidationStatus.OK, underTest.isGraphCycled(project).status)
    }
}