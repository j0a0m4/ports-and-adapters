package io.j0a0m4.portsandadapters.domain.model

import io.j0a0m4.portsandadapters.domain.model.Entity
import java.util.*

class ID(value: UUID?) : Entity {
	override val id: UUID = value ?: UUID.randomUUID()
	override fun toString() = id.toString()
	override fun equals(other: Any?) = id == other
	override fun hashCode() = id.hashCode()
}
