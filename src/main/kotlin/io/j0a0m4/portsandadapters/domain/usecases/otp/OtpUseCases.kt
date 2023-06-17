package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.Contact
import io.j0a0m4.portsandadapters.domain.model.SendMethod
import java.util.*

interface OtpUseCases {
	fun send(contactId: UUID, sendMethod: SendMethod): Result<Contact>
}
