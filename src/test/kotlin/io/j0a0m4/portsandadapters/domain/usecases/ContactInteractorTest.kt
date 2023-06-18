package io.j0a0m4.portsandadapters.domain.usecases

import io.j0a0m4.portsandadapters.adapter.NoSuchUUID
import io.j0a0m4.portsandadapters.domain.model.VerifiedStatus
import io.j0a0m4.portsandadapters.domain.model.contact
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.data.forAll
import io.kotest.data.row
import io.kotest.matchers.result.shouldBeFailure
import io.kotest.matchers.result.shouldBeSuccess
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.assertDoesNotThrow
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean

@SpringBootTest
class ContactInteractorTest : FeatureSpec() {
	@TestConfiguration
	class ApiTestConfig {
		@Bean
		fun storage() = mockk<ContactStorage>()
	}

	@Autowired
	private lateinit var interactor: ContactInteractor

	@Autowired
	private lateinit var storage: ContactStorage

	init {
		val contact = contact {
			it.name = "Dustin Kensrue"
			it.email = "dustin@thrice.org"
			it.phone = "+1923543194"
		}

		feature("add contact") {
			scenario("should persist to storage") {
				every { storage.persist(contact) } answers {}
			}
			scenario("and should return new UUID") {
				assertDoesNotThrow {
					interactor.add(contact) shouldBeSuccess contact.id
				}
			}
		}

		feature("update contact status by id") {
			every { storage.update(any()) } answers { Result.success(contact) }
			scenario("should persist to storage") {
				forAll(
					row(VerifiedStatus.Verified),
					row(VerifiedStatus.Unverified),
					row(VerifiedStatus.Pending),
				) { status ->
					assertDoesNotThrow {
						interactor.updateStatus(contact.id, status)
					}
				}
			}
		}

		feature("find contact by id") {
			scenario("should find contact") {
				every { storage.findBy(contact.id) } answers { Result.success(contact) }
				interactor.findBy(contact.id) shouldBeSuccess contact
			}
			scenario("id is not found") {
				val exception = NoSuchUUID(contact.id)
				every { storage.findBy(contact.id) } answers { Result.failure(exception) }
				interactor.findBy(contact.id) shouldBeFailure exception
			}
		}
	}
}
