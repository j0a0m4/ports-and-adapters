package io.j0a0m4.portsandadapters.domain.usecases.contact

import io.j0a0m4.portsandadapters.domain.model.Contact

class AddContactCommand(val contact: Contact) {
	companion object
}