package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import java.util.*

interface ContactUseCases {
	infix fun add(contact: Contact): Result<UUID>

	infix fun findBy(contactId: UUID): Result<Contact>

	fun updateStatus(contactId: UUID, newStatus: VerifiedStatus)
	infix fun setToPending(contactId: UUID) {
		updateStatus(contactId, VerifiedStatus.Pending)
	}
	infix fun setToVerified(contactId: UUID) {
		updateStatus(contactId, VerifiedStatus.Verified)
	}
}
