package com.epam.repository

import com.epam.model.Storage
import org.springframework.data.jpa.repository.JpaRepository

interface StorageRepository: JpaRepository<Storage, Long>