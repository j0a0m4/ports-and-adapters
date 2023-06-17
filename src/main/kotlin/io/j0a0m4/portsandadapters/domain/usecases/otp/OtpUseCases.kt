package io.j0a0m4.portsandadapters.domain.usecases.otp

import io.j0a0m4.portsandadapters.domain.model.Contact

interface OtpUseCases {
	fun execute(command: SendOtpCommand): Result<Contact>
}
