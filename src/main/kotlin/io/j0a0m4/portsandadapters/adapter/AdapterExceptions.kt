package io.j0a0m4.portsandadapters.adapter

import io.j0a0m4.portsandadapters.domain.model.SendMethod
import java.util.*

sealed class NoSuchKeyException(override val message: String) : Exception()

class NoSuchUUID(contactId: UUID) : NoSuchKeyException("UUID ($contactId) not found")

class NoSuchOtpKey(key: Pair<UUID, SendMethod>) : NoSuchKeyException("Key $key not found")
