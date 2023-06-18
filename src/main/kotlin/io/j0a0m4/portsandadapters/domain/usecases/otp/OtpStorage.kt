package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import java.util.*

interface OtpStorage {
	infix fun persist(record: OtpRecord)
	infix fun retrieve(key: Pair<UUID,SendMethod>): Result<VerificationCode?>
	infix fun invalidate(key: Pair<UUID,SendMethod>): Result<VerificationCode>

	fun retrieve(contactId: UUID, sendMethod: SendMethod) =
		retrieve(contactId to sendMethod)

	fun invalidate(contactId: UUID, method: SendMethod) {
		invalidate(contactId to method)
	}
}
