package io.j0a0m4.portsandadapters.domain.usecases.contact

import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import java.util.*

data class UpdateStatusCommand(val contactId: UUID, val newStatus: VerifiedStatus) {
	companion object
}
