package com.epam.model

import javax.persistence.*

@Entity
@Table(name = "OUTBOX")
class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    var id: Int? = null

    var eventType: String? = null

    var payload: String? = null

    var isProceeded: Boolean? = null
}