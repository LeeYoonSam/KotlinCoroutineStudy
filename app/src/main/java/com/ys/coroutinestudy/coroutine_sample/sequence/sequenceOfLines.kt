package com.ys.coroutinestudy.coroutine_sample.sequence

import java.io.BufferedReader
import java.io.FileReader

fun sequenceOfLines(fileName: String) = sequence {
	BufferedReader(FileReader(fileName)).use {
		while (true) {
			yield(it.readLine() ?: break)
		}
	}
}

fun main() {
	sequenceOfLines("/Users/ys/Documents/Github/KotlinCoroutineStudy/app/src/main/java/com/ys/coroutinestudy/coroutine_sample/sequence/sequenceOfLines.kt")
		.forEach(::println)
}