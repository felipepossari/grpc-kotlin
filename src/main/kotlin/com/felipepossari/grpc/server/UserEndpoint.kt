package com.felipepossari.grpc.server

import com.felipepossari.grpc.PushRequest
import com.felipepossari.grpc.PushResponse
import com.felipepossari.grpc.Status
import com.felipepossari.grpc.UserCreateRequest
import com.felipepossari.grpc.UserCreateResponse
import com.felipepossari.grpc.UserServiceGrpcKt
import kotlinx.coroutines.flow.Flow
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
            emit(buildPendingPushResponse(transaction))
        }
        emit(buildApprovedPushResponse(transaction))
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