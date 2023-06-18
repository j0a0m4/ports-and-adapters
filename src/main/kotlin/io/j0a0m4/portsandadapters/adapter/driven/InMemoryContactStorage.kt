package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.domain.model.*
import io.j0a0m4.portsandadapters.domain.usecases.contact.ContactStorage
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class InMemoryContactStorage() : ContactStorage, MutableMap<UUID, Contact> by HashMap() {
	override infix fun persist(contact: Contact) {
		this[contact.id] = contact
	}

	override fun update(contactId: UUID, status: VerifiedStatus) =
		updateStatus(contactId, status)

	override fun findBy(contactId: UUID): Result<Contact> = get(contactId).let {
		if (it != null) {
			Result.success(it)
		} else {
			Result.failure(NoSuchElementException("UUID ($contactId) not found"))
		}
	}

	private fun updateStatus(contactId: UUID, newStatus: VerifiedStatus) =
		findBy(contactId)
			.map { it.patch { verifiedStatus = newStatus } }
			.onSuccess { this[contactId] = it }
}
