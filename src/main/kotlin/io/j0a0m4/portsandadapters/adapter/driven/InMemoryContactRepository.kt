package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.adapter.NoSuchUUID
import io.j0a0m4.portsandadapters.domain.model.*
import io.j0a0m4.portsandadapters.domain.usecases.dependencies.ContactRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class InMemoryContactRepository() : ContactRepository, MutableMap<UUID, Contact> by HashMap() {
	override infix fun persist(contact: Contact) {
		this[contact.id] = contact
	}

	override fun update(contactId: UUID, status: VerifiedStatus) =
		updateStatus(contactId, status)

	override fun findBy(contactId: UUID): Result<Contact> =
		when (val contact = this[contactId]) {
			null -> Result.failure(NoSuchUUID(contactId))
			else -> Result.success(contact)
		}

	private fun updateStatus(contactId: UUID, newStatus: VerifiedStatus) =
		findBy(contactId)
			.map { it.update { verifiedStatus = newStatus } }
			.onSuccess { this[contactId] = it }
}
