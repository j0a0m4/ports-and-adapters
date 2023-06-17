package io.j0a0m4.portsandadapters.domain.model

import java.util.*

sealed interface Entity {
	val id: UUID
}
