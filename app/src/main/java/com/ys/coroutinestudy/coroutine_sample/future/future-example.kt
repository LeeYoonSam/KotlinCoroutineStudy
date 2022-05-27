package com.ys.coroutinestudy.coroutine_sample.future

import android.annotation.SuppressLint
import java.util.concurrent.CompletableFuture

@SuppressLint("NewApi")
fun foo(): CompletableFuture<String> = CompletableFuture.supplyAsync { "foo" }

@SuppressLint("NewApi")
fun bar(v: String): CompletableFuture<String> = CompletableFuture.supplyAsync { "bar with $v" }

@SuppressLint("NewApi")
fun main() {
	val future = future {
		println("start")
		val x = foo().await()
		println("got '$x'")
		val y = bar(x).await()
		println("got '$y' after '$x'")
		y
	}

	future.join()
}
