package com.felipepossari.grpc.server

import com.felipepossari.grpc.PushRequest
import com.felipepossari.grpc.PushResponse
import com.felipepossari.grpc.Status
import com.felipepossari.grpc.UserBulkCreateResponse
import com.felipepossari.grpc.UserCreateRequest
import com.felipepossari.grpc.UserCreateResponse
import com.felipepossari.grpc.UserServiceGrpcKt
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*

class UserEndpoint : UserServiceGrpcKt.UserServiceCoroutineImplBase() {

    override suspend fun create(request: UserCreateRequest): UserCreateResponse {

        return UserCreateResponse.newBuilder().apply {
            email = request.email;
            name = request.name;
            country = request.country
        }.build()
    }

    override fun sendPush(request: PushRequest): Flow<PushResponse> = flow {
        val transaction = UUID.randomUUID().toString()
        for (i in 1..5) {
            kotlinx.coroutines.delay(1000)
            if(i == 3){
                println("Push response: APPROVED")
                emit(buildApprovedPushResponse(transaction))
            }else{
                println("Push response: PENDING")
                emit(buildPendingPushResponse(transaction))
            }
        }
        println("Push response: APPROVED")
        emit(buildApprovedPushResponse(transaction))
    }

    override suspend fun bulkCreate(requests: Flow<UserCreateRequest>): UserBulkCreateResponse {
        println("User bulk create request received")
        var usersCreated = 0L

        requests.collect {
            println("User created: ${it.name}")
            if(it.name.isNotEmpty()){
                usersCreated++
            }
        }
        println("Users created: $usersCreated")
        return UserBulkCreateResponse.newBuilder().apply { count = usersCreated }.build()
    }

    private fun buildApprovedPushResponse(transaction: String) =
            buildPushResponse(transaction, Status.APPROVED)

    private fun buildPendingPushResponse(transaction: String) =
            buildPushResponse(transaction, Status.PENDING)

    private fun buildPushResponse(transaction: String, statusPush: Status) =
            PushResponse.newBuilder().apply {
                transactionId = transaction;
                status = statusPush
            }.build()
}