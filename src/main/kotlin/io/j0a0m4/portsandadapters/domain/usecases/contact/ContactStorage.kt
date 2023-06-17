package io.j0a0m4.portsandadapters.domain.usecases.contact

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import java.util.*

interface ContactStorage {
	infix fun persist(contact: Contact)
	infix fun update(pair: Pair<UUID, VerifiedStatus>): Result<Contact>
	infix fun findBy(contactId: UUID): Result<Contact>
}