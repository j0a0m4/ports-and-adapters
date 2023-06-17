package io.j0a0m4.portsandadapters.domain.usecases.contact

import java.util.UUID

data class FindByIdCommand(val contactId: UUID) {
	companion object
}
