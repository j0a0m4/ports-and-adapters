package io.j0a0m4.portsandadapters.domain.usecases.dependencies

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import java.util.*

interface ContactRepository {
	infix fun persist(contact: Contact)

	infix fun findBy(contactId: UUID): Result<Contact>

	fun update(contactId: UUID, status: VerifiedStatus): Result<Contact>
	infix fun update(pair: Pair<UUID, VerifiedStatus>) {
		update(pair.first, pair.second)
	}
}
