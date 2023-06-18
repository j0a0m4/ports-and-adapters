package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.VerificationCode
import java.util.*

interface OtpStorage {
	infix fun persist(record: OtpRecord)
	infix fun retrieveOtp(key: Pair<UUID,SendMethod>): Result<VerificationCode?>
	infix fun invalidate(key: Pair<UUID,SendMethod>): Result<VerificationCode>

	fun retrieveOtp(contactId: UUID, sendMethod: SendMethod) =
		retrieveOtp(contactId to sendMethod)

	fun invalidate(contactId: UUID, method: SendMethod) {
		invalidate(contactId to method)
	}
}
