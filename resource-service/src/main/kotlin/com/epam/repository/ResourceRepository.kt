package com.epam.repository

import com.epam.model.Resource
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ResourceRepository: CrudRepository<Resource, Long>