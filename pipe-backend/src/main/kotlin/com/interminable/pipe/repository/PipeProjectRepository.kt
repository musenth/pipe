package com.interminable.pipe.repository

import com.interminable.pipe.entity.PipeProject
import org.springframework.data.mongodb.repository.MongoRepository

interface PipeProjectRepository : MongoRepository<PipeProject, String> {

    /**
     * Returns the project with given name
     */
    fun findByName(name: String): PipeProject?

    fun deleteByName(name: String)
}