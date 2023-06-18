package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import java.util.*

interface OtpStorage {
	fun persist(record: OtpRecord)
	fun verify(key: Pair<UUID,SendMethod>, value: VerificationCode): Boolean

	fun verify(contactId: UUID, sendMethod: SendMethod, otp: VerificationCode) =
		verify(contactId to sendMethod, otp)
}
