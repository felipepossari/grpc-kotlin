package com.felipepossari.grpc

import com.felipepossari.grpc.server.UserEndpoint
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
            .addService(UserEndpoint())
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
