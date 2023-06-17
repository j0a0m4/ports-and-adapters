package io.j0a0m4.portsandadapters.domain.usecases.contact

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import java.util.*

interface ContactUseCases {
	operator fun invoke(command: AddContactCommand): Result<UUID>
	operator fun invoke(command: UpdateStatusCommand): Result<Contact>
	operator fun invoke(command: FindByIdCommand): Result<Contact>
	fun updateStatus(contactId: UUID, status: VerifiedStatus): Result<Contact>
}