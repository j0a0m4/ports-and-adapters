package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.VerificationCode
import java.util.*

interface OtpUseCases {
	fun send(contactId: UUID, method: SendMethod)
	fun verify(contactId: UUID, method: SendMethod, otp: VerificationCode): Result<Unit>
}