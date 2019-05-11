package com.interminable.pipe

import com.interminable.pipe.entity.PipeProject
import com.interminable.pipe.util.PipeValidator
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class PipeValidatorTests {

    private val underTest = PipeValidator()

    @Test
    @DisplayName("1. Если граф не содержит связей с несуществующими вершинами, возвращается true")
    fun t1_isGraphValid_withValidGraph_mustReturnTrue() {
        val links = mutableListOf(
                mutableListOf(1, 2),
                mutableListOf(0),
                mutableListOf(0, 1)
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
        val links = mutableListOf(
                mutableListOf(1, 2),
                mutableListOf(0, 6),
                mutableListOf(0, 3)
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
        val links = mutableListOf(
                mutableListOf(1, 2),
                mutableListOf(0),
                mutableListOf(0, 1)
        )
        val project = PipeProject(
                name = "",
                links = links
        )
        assertTrue(underTest.isGraphCycled(project))
    }

    @Test
    @DisplayName("4. Если в графе есть вершины, которые связаны с собой, проверка на циклы возвращает true")
    fun t4_isGraphCycled_withCycledGraph_mustReturnTrue() {
        val links = mutableListOf(
                mutableListOf(0)
        )
        val project = PipeProject(
                name = "",
                links = links
        )
        assertTrue(underTest.isGraphCycled(project))
    }

    @Test
    @DisplayName("5. Если граф не содержит циклы, проверка на циклы возвращает false")
    fun t5_isGraphCycled_withCycledGraph_mustReturnFalse() {
        val links = mutableListOf(
                mutableListOf(1),
                mutableListOf(2),
                mutableListOf()
        )
        val project = PipeProject(
                name = "",
                links = links
        )
        assertFalse(underTest.isGraphCycled(project))
    }
}