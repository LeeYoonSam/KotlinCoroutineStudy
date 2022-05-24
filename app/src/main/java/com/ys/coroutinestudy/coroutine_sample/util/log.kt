package com.ys.coroutinestudy.coroutine_sample.util

import android.os.Build.VERSION_CODES
import androidx.annotation.RequiresApi
import java.time.*

@RequiresApi(VERSION_CODES.O)
fun log(msg: String) = println("${Instant.now()} [${Thread.currentThread().name}] $msg")