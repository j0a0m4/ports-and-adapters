package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.*
import java.util.*

interface OtpUseCases {
	fun send(contactId: UUID, method: SendMethod)
	fun verify(contactId: UUID, method: SendMethod, otp: VerificationCode): Result<Unit>
}
