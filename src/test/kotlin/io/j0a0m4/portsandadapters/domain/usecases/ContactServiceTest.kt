package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.adapter.NoSuchUUID
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import io.j0a0m4.portsandadapters.domain.model.contact
import io.j0a0m4.portsandadapters.domain.usecases.dependencies.ContactRepository
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.mockk.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import

class ContactServiceTest : ContactServiceTestSpec() {
	init {
		val contact = contact {
			it.name = "Dustin Kensrue"
			it.email = "dustin@thrice.org"
			it.phone = "+1923543194"
		}

		feature("add contact") {
			scenario("persists to storage and returns contact id") {
				service.add(contact) shouldBeSuccess contact.id

				verify(exactly = 1) {
					repository persist contact
				}
			}
		}

		feature("update contact status by id") {
			every {
				repository.update(any())
			} answers {
				Result.success(contact)
			}
			scenario("persist new status to storage") {
				forAll(
					row(VerifiedStatus.Verified),
					row(VerifiedStatus.Unverified),
					row(VerifiedStatus.Pending),
				) { status ->
					service.updateStatus(contact.id, status)
				}

				verify(exactly = 3) {
					repository update (any())
				}
			}
		}

		feature("find contact by id") {
			scenario("contact id is found") {
				every {
					repository.findBy(contact.id)
				} answers {
					Result.success(contact)
				}

				service.findBy(contact.id) shouldBeSuccess contact

				verify(exactly = 1) {
					repository.findBy(contact.id)
				}
			}
			scenario("contact id is not found") {
				val exception = NoSuchUUID(contact.id)

				every {
					repository.findBy(contact.id)
				} answers {
					Result.failure(exception)
				}

				service.findBy(contact.id) shouldBeFailure exception

				verify(exactly = 1) {
					repository.findBy(contact.id)
				}
			}
		}
	}
}

@TestConfiguration
class ContactServiceTestConfig {
	@Bean
	fun repository() = mockk<ContactRepository>(relaxed = true)
}

@Import(ContactServiceTestConfig::class)
@SpringBootTest
class ContactServiceTestSpec() : FeatureSpec() {
	@Autowired
	lateinit var service: ContactService

	@Autowired
	lateinit var repository: ContactRepository

	init {
		afterTest {
			clearMocks(repository)
		}
	}
}
