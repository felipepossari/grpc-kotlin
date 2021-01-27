package com.felipepossari.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.runBlocking

fun main() {
    println("gRPC client")

    val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build()

    val client = UserServiceGrpcKt.UserServiceCoroutineStub(channel)

    val userCreateRequest = UserCreateRequest.newBuilder()
            .setCountry("BR")
            .setEmail("email@email.com")
            .setName("Name")
            .build()

    val response = runBlocking { client.create(userCreateRequest) }

    println("Response: ${response.id} - ${response.email} - ${response.country} - ${response.name}")
    channel.shutdown()
}