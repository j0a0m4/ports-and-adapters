package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class ContactInteractor(val storage: ContactStorage) : ContactUseCases {
	override fun add(contact: Contact) =
		contact.apply { storage persist this }
			.run { Result.success(id) }

	override fun updateStatus(contactId: UUID, newStatus: VerifiedStatus) {
		storage update (contactId to newStatus)
	}

	override fun findBy(contactId: UUID) = storage findBy contactId
}


