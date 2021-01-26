package com.felipepossari.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder

fun main() {
    println("gRPC client")

    val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build()

    val client = GrpcKotlinServiceGrpcKt.GrpcKotlinServiceCoroutineStub(channel)

    channel.shutdown()
}