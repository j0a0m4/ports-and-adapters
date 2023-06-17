package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.*
import java.util.UUID

data class SendOtpCommand(val id: UUID, val sendMethod: SendMethod) {
	companion object
}
