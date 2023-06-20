package io.j0a0m4.portsandadapters.domain.usecases.dependencies

import io.j0a0m4.portsandadapters.domain.usecases.OtpRecord

fun interface Sender {
	infix fun send(record: OtpRecord)
}
