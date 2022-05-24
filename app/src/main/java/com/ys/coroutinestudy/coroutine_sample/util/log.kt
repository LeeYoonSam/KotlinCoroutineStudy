package com.ys.coroutinestudy.coroutine_sample.util

import android.annotation.SuppressLint
import java.time.Instant

@SuppressLint("NewApi")
fun log(msg: String) = println("${Instant.now()} [${Thread.currentThread().name}] $msg")