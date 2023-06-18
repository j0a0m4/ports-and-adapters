package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.adapter.NoSuchOtpKey
import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.model.VerificationCode
import io.j0a0m4.portsandadapters.domain.usecases.OtpRecord
import io.j0a0m4.portsandadapters.domain.usecases.OtpStorage
import org.springframework.stereotype.Repository
import java.security.InvalidKeyException
import java.util.*

@Repository
class InMemoryOtpStorage : OtpStorage, MutableMap<Pair<UUID, SendMethod>, VerificationCode> by HashMap() {
	override fun persist(record: OtpRecord) {
		this += record.toMap()
	}

	override fun retrieveOtp(key: Pair<UUID, SendMethod>) =
		when (key in this) {
			false -> Result.failure(NoSuchOtpKey(key))
			true  -> Result.success(this[key])
		}

	override fun invalidate(key: Pair<UUID, SendMethod>) =
		when (val value = remove(key)) {
			null -> Result.failure(InvalidKeyException("Key $key not found"))
			else -> Result.success(value)
		}
}
