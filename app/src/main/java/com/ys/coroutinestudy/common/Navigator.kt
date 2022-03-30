package com.ys.coroutinestudy.common

import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import com.ys.coroutinestudy.base.Demo
import com.ys.coroutinestudy.base.UseCase
import com.ys.coroutinestudy.base.UseCaseCategory

class Navigator private constructor(
    private val backDispatcher: OnBackPressedDispatcher,
    private val launchActivityDemo: (UseCase) -> Unit,
    private val rootUseCase: Demo,
    private val backStack: MutableList<Demo>,
    private val finish: () -> Unit
) {
    constructor(
        rootUseCase: Demo,
        backDispatcher: OnBackPressedDispatcher,
        launchActivityDemo: (UseCase) -> Unit,
        finish: () -> Unit,
    ) : this(backDispatcher, launchActivityDemo, rootUseCase, mutableListOf<Demo>(), finish)

    private val onBackPressed = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            if (!isRoot) {
                popBackStack()
                return
            }

            finish()
        }
    }.apply {
        isEnabled = true
        backDispatcher.addCallback(this)
    }

    private var _useCase by mutableStateOf(rootUseCase)
    var currentDemo: Demo
        get() = _useCase
        private set(value) {
            _useCase = value
            onBackPressed.isEnabled = true
        }

    val isRoot: Boolean get() = backStack.isEmpty()

    val backStackTitle: String
        get() = (backStack.drop(1) + currentDemo).joinToString(separator = " > ") { it.description }

    fun navigateTo(demo: Demo) {
        if (demo is UseCase) {
            launchActivityDemo(demo)
        } else {
            backStack.add(currentDemo)
            currentDemo = demo
        }
    }

    fun popAll() {
        if (!isRoot) {
            backStack.clear()
            currentDemo = rootUseCase
        }
    }

    private fun popBackStack() {
        currentDemo = backStack.removeAt(backStack.lastIndex)
    }

    companion object {
        fun Saver(
            rootUseCase: UseCaseCategory,
            backDispatcher: OnBackPressedDispatcher,
            launchActivityDemo: (UseCase) -> Unit,
            finish: () -> Unit
        ): Saver<Navigator, *> = listSaver(
            save = { navigator ->
                (navigator.backStack + navigator.currentDemo).map { it.description }
            },
            restore = { restored ->
                require(restored.isNotEmpty())
                val backStack = restored.mapTo(mutableListOf()) {
                    requireNotNull(findDemo(rootUseCase, it))
                }
                Navigator(backDispatcher, launchActivityDemo, rootUseCase, backStack, finish)
            }
        )

        private fun findDemo(useCase: Demo, title: String): Demo? {
            if (useCase.description == title) {
                return useCase
            }
            if (useCase is UseCaseCategory) {
                useCase.useCases.forEach { child ->
                    findDemo(child, title)?.let { return it }
                }
            }
            return null
        }
    }
}