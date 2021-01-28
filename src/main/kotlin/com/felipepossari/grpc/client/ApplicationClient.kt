package com.felipepossari.grpc

import io.grpc.ManagedChannel
import io.grpc.ManagedChannelBuilder
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.cancel
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking

fun main() {
    println("gRPC client")

    val channel: ManagedChannel = ManagedChannelBuilder
            .forAddress("localhost", 50051)
            .usePlaintext()
            .build()

    val client = UserServiceGrpcKt.UserServiceCoroutineStub(channel)

//    callCreateUser(client)
//    callSendPush(client)
    callBulkCreateUser(client)
    channel.shutdown()
}

fun callBulkCreateUser(client: UserServiceGrpcKt.UserServiceCoroutineStub) {

    val response = runBlocking {
        client.bulkCreate(flow {
            for (i in 1..10) {
                val userCreateRequest = buildBulkUserCreateRequest(i)
                println("Sending user create request $i")
                emit(userCreateRequest)
                delay(timeMillis = 1000)
            }
        })
    }
    println("Total of users created: ${response.count}")
}

fun callSendPush(client: UserServiceGrpcKt.UserServiceCoroutineStub) {

    val pushRequest = PushRequest.newBuilder().apply {
        userId = 1;
        message = "Push message"
    }.build()

    try {
        runBlocking {
            client.sendPush(pushRequest).collect {
                if (it.status == Status.APPROVED) {
                    println("Status found")
                    currentCoroutineContext().cancel()
                }
                println("Push status: ${it.status}")
            }
        }
    } catch (ex: CancellationException) {
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
    println("User created: ${response.id}")
    println("Response: ${response.id} - ${response.email} - ${response.country} - ${response.name}")

}

private fun buildBulkUserCreateRequest(i: Int): UserCreateRequest {
    val userCreateRequest = UserCreateRequest.newBuilder()
            .setCountry("BR")
            .setEmail("email$i@email.com")
            .setName("Name $i")
            .build()
    return userCreateRequest
}
