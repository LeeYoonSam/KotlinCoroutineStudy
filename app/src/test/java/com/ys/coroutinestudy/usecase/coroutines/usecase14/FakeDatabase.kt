package com.ys.coroutinestudy.usecase.coroutines.usecase14

import com.ys.coroutinestudy.mock.mockAndroidVersions

class FakeDatabase : AndroidVersionDao {

	var insertedIntoDb = false

	override suspend fun getAndroidVersions(): List<AndroidVersionEntity> {
		return mockAndroidVersions.mapToEntityList()
	}

	override suspend fun insert(androidVersionEntity: AndroidVersionEntity) {
		insertedIntoDb = true
	}

	override suspend fun clear() {}
}