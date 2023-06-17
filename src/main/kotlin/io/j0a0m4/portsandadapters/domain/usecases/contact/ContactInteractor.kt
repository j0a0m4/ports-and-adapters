package io.j0a0m4.portsandadapters.domain.usecases.contact

import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import org.springframework.stereotype.Service
import java.util.*

@Service
class ContactInteractor(val storage: ContactStorage) : ContactUseCases {
	override fun invoke(command: AddContactCommand) = with(command) {
		contact.apply { storage persist this }
			.run { Result.success(id) }
	}

	override fun invoke(command: UpdateStatusCommand) = with(command) {
		storage update (contactId to newStatus)
	}

	override fun invoke(command: FindByIdCommand) = with(command) {
		storage findBy contactId
	}
}


