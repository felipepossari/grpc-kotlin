package com.felipepossari.grpc

import com.felipepossari.grpc.server.UserEndpoint
import io.grpc.Server
import io.grpc.ServerBuilder
import java.io.File

//fun main(args: Array<String>) {
//	build()
//	    .args(*args)
//		.packages("com.felipepossari.grpc")
//		.start()
//}


fun main() {
    println("Starting server")

    // Plaintext server
//    val server: Server = ServerBuilder.forPort(50051)
//            .addService(UserEndpoint())
//            .build()

    val server: Server = ServerBuilder.forPort(50051)
            .addService(UserEndpoint())
            .useTransportSecurity(File("ssl/server.crt"), File("ssl/server.pem"))
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
