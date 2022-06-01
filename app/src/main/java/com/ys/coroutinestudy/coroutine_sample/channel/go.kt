package com.ys.coroutinestudy.coroutine_sample.channel

import com.ys.coroutinestudy.coroutine_sample.context.CommonPool
import com.ys.coroutinestudy.coroutine_sample.run.runBlocking

fun mainBlocking(block: suspend () -> Unit) = runBlocking(CommonPool, block)

fun go(block: suspend () -> Unit) = CommonPool.runParallel(block)