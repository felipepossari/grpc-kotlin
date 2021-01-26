package com.felipepossari.grpc

import io.micronaut.runtime.Micronaut.*
fun main(args: Array<String>) {
	build()
	    .args(*args)
		.packages("com.felipepossari.grpc")
		.start()
}

