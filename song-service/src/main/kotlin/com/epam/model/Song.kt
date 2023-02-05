package com.epam.model

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id

@Entity
class Song {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var id: Int? = null

    var name: String? = null

    var artist: String? = null

    var album: String? = null

    var length: String? = null

    @Column(unique = true)
    var resourceId: Int? = null

    var year: String? = null
}