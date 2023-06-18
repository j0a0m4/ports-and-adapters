package io.j0a0m4.portsandadapters.adapter.driven

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import io.j0a0m4.portsandadapters.domain.usecases.otp.*
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class InMemoryOtpStorage : OtpStorage, MutableMap<Pair<UUID, SendMethod>, VerificationCode> by HashMap() {
	override fun persist(record: OtpRecord) {
		this += record.toMap()
	}

	override fun verify(key: Pair<UUID, SendMethod>, value: VerificationCode) =
		when (key in this) {
			true -> Result.success(this[key] == value)
			else -> Result.failure(NoSuchElementException("Key [ $key ] not found"))
		}

	override fun invalidate(key: Pair<UUID, SendMethod>) =
		when (val value = remove(key)) {
			null -> Result.failure(NoSuchElementException("Key [ $key ] not found"))
			else -> Result.success(value)
		}
}
