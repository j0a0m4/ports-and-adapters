package io.j0a0m4.portsandadapters.domain.usecases.contact

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import java.util.*

interface ContactStorage {
	infix fun persist(contact: Contact)
	fun update(contactId: UUID, status: VerifiedStatus): Result<Contact>
	infix fun findBy(contactId: UUID): Result<Contact>
	infix fun update(pair: Pair<UUID, VerifiedStatus>) {
		update(pair.first, pair.second)
	}
}