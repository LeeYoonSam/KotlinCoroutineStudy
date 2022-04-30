package com.ys.coroutinestudy.usecase.coroutines.usecase14

import com.ys.coroutinestudy.mock.mockAndroidVersions
import com.ys.coroutinestudy.util.log
import com.ys.coroutinestudy.utils.MainCoroutineScopeRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestCoroutineScope
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AndroidVersionRepositoryTest {

	@get:Rule
	val mainCoroutineScopeRule: MainCoroutineScopeRule = MainCoroutineScopeRule()

	@Test
	fun `getLocalAndroidVersions() should return android versions from database`() =
		mainCoroutineScopeRule.runBlockingTest {
			val fakeDatabase = FakeDatabase()

			val repository = AndroidVersionRepository(fakeDatabase, mainCoroutineScopeRule)
			assertEquals(mockAndroidVersions, repository.getLocalAndroidVersions())
		}

	@Test
	fun `loadRecentAndroidVersions() should return android versions from network`() =
		mainCoroutineScopeRule.runBlockingTest {
			val fakeDatabase = FakeDatabase()
			val fakeApi = FakeApi()

			val repository = AndroidVersionRepository(
				database = fakeDatabase,
				scope = mainCoroutineScopeRule,
				api = fakeApi
			)

			assertEquals(mockAndroidVersions, repository.loadAndStoreRemoteAndroidVersions())
		}

	@Test
	fun `loadRecentAndroidVersions() should continue to load and store android versions when calling scope gets cancelled`() =
		mainCoroutineScopeRule.runBlockingTest {
			val fakeDatabase = FakeDatabase()
			val fakeApi = FakeApi()
			val repository = AndroidVersionRepository(
				fakeDatabase,
				mainCoroutineScopeRule,
				api = fakeApi
			)

			// 이 코루틴은 즉시 실행되지만, fakeApi의 delay(1)에서 실행을 중지합니다.
			val viewModelScope = TestCoroutineScope(SupervisorJob())
			viewModelScope.launch {
				log("running coroutine!")
				repository.loadAndStoreRemoteAndroidVersions()
				fail("Scope should be cancelled before versions are loaded!")
			}

			viewModelScope.cancel()

			// fakeApi에서 delay(1) 후 코루틴 실행
			advanceUntilIdle()

			assertEquals(true, fakeDatabase.insertedIntoDb)
		}
}
