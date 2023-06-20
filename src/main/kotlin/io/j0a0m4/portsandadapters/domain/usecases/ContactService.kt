package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import io.j0a0m4.portsandadapters.domain.usecases.dependencies.ContactRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class ContactService(val repository: ContactRepository) : ContactUseCases {
	override fun add(contact: Contact) =
		contact.apply { repository persist this }
			.run { Result.success(id) }

	override fun updateStatus(contactId: UUID, newStatus: VerifiedStatus) {
		repository update (contactId to newStatus)
	}

	override fun findBy(contactId: UUID) = repository findBy contactId
}
