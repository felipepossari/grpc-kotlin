package com.felipepossari.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import javax.xml.catalog.Catalog

fun main() {
    println("gRPC client")

    val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build()

    val client = UserServiceGrpcKt.UserServiceCoroutineStub(channel)

//    callCreateUser(client)
    callSendPush(client)
    channel.shutdown()
}

fun callSendPush(client: UserServiceGrpcKt.UserServiceCoroutineStub) {

    val pushRequest = PushRequest.newBuilder().apply {
        userId = 1;
        message = "Push message"
    }.build()

    try {
        val response = runBlocking {
            client.sendPush(pushRequest).collect {
                if (it.status == Status.APPROVED) {
                    println("Status found")
                    currentCoroutineContext().cancel()
                }
                println("Push status: ${it.status}")
            }
        }
    }catch (ex: CancellationException){
        println("Stream stopped")
    }
}

fun callCreateUser(client: UserServiceGrpcKt.UserServiceCoroutineStub) {
    val userCreateRequest = UserCreateRequest.newBuilder()
            .setCountry("BR")
            .setEmail("email@email.com")
            .setName("Name")
            .build()

    val response = runBlocking { client.create(userCreateRequest) }

    println("Response: ${response.id} - ${response.email} - ${response.country} - ${response.name}")

}
