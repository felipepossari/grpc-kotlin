package com.felipepossari.grpc

import io.grpc.Server
import io.grpc.ServerBuilder

//fun main(args: Array<String>) {
//	build()
//	    .args(*args)
//		.packages("com.felipepossari.grpc")
//		.start()
//}


fun main() {
    println("Starting server")
    val server: Server = ServerBuilder.forPort(50051)
            .build()

    server.start()

    Runtime.getRuntime().addShutdownHook(
            Thread {
                println("Shutting server")
                server.shutdown()
            }
    )

    server.awaitTermination()
}
