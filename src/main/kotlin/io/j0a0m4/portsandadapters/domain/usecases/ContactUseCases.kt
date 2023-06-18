package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import java.util.*

interface ContactUseCases {
	fun add(contact: Contact): Result<UUID>
	fun updateStatus(contactId: UUID, newStatus: VerifiedStatus)
	fun findBy(contactId: UUID): Result<Contact>

	fun pending(contactId: UUID) {
		updateStatus(contactId, VerifiedStatus.Pending)
	}
	fun verified(contactId: UUID) {
		updateStatus(contactId, VerifiedStatus.Verified)
	}
}