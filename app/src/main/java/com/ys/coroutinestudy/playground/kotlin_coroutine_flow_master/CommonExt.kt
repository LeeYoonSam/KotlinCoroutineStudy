package com.ys.coroutinestudy.playground.kotlin_coroutine_flow_master

fun printCurrentThreadName() = println(getCurrentThreadName())

fun getCurrentThreadName() = Thread.currentThread().name

fun log(msg: String) = println("[${getCurrentThreadName()}] $msg")

fun printTitle(title: String) = println("---------- $title ----------")