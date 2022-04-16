package com.ys.coroutinestudy.usecase.coroutines.usecase14

import androidx.lifecycle.viewModelScope
import com.ys.coroutinestudy.base.BaseViewModel
import kotlinx.coroutines.launch

class ContinueCoroutineWhenUserLeavesScreenViewModel(
	private var repository: AndroidVersionRepository
): BaseViewModel<UiState>() {

	// 취소해서는 안 되는 작업에 대한 코루틴 및 패턴에 대한 이 블로그 게시물의 추가 정보 =>
	// https://medium.com/androiddevelopers/coroutines-patterns-for-work-that-shouldnt-be-cancelled-e26c40f142ad

	fun loadData() {
		uiState.value = UiState.Loading.LoadFromDb

		viewModelScope.launch {
			val localVersions = repository.getLocalAndroidVersions()
			if (localVersions.isNotEmpty()) {
				uiState.value =
					UiState.Success(DataSource.Database, localVersions)
			} else {
				uiState.value =
					UiState.Error(DataSource.Database, "Database empty!")
			}

			uiState.value = UiState.Loading.LoadFromNetwork

			try {
				uiState.value = UiState.Success(
					DataSource.Network,
					repository.loadAndStoreRemoteAndroidVersions()
				)
			} catch (exception: Exception) {
				uiState.value = UiState.Error(DataSource.Network, "Network Request failed")
			}
		}
	}

	fun clearDatabase() {
		repository.clearDatabase()
	}
}

sealed class DataSource(val name: String) {
	object Database : DataSource("Database")
	object Network : DataSource("Network")
}