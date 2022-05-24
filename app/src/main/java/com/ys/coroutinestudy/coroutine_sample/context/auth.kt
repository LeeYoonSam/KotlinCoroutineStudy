package com.ys.coroutinestudy.coroutine_sample.context

import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

class AuthUser(val name: String): AbstractCoroutineContextElement(AuthUser) {
	companion object Key: CoroutineContext.Key<AuthUser>
}