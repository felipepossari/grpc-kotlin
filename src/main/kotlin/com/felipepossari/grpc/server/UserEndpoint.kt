package com.felipepossari.grpc.server

import com.felipepossari.grpc.PushRequest
import com.felipepossari.grpc.PushResponse
import com.felipepossari.grpc.Status
import com.felipepossari.grpc.UserActionRequest
import com.felipepossari.grpc.UserActionResponse
import com.felipepossari.grpc.UserBulkCreateResponse
import com.felipepossari.grpc.UserCreateRequest
import com.felipepossari.grpc.UserCreateResponse
import com.felipepossari.grpc.UserServiceGrpcKt
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

class UserEndpoint : UserServiceGrpcKt.UserServiceCoroutineImplBase() {

    private val random = Random(123456)

    override suspend fun create(request: UserCreateRequest): UserCreateResponse = kotlin.runCatching {

        if(request.email.isEmpty()){
            throw Exception("Email null or empty")
        }

        return UserCreateResponse.newBuilder().apply {
            email = request.email;
            name = request.name;
            country = request.country
        }.build()
    }.onFailure { e ->
        throw io.grpc.Status.INVALID_ARGUMENT
                .withCause(e.cause)
                .withDescription(e.message)
                .asRuntimeException()
    }.getOrThrow()

    override fun sendPush(request: PushRequest): Flow<PushResponse> = flow {
        val transaction = UUID.randomUUID().toString()
        val randomPushApproval = random.nextInt(1..10)
        for (i in 1..10) {
            kotlinx.coroutines.delay(500)
            if (i == randomPushApproval) {
                println("Push response: APPROVED")
                emit(buildApprovedPushResponse(transaction))
                break;
            } else {
                println("Push response: PENDING")
                emit(buildPendingPushResponse(transaction))
            }
        }
    }

    override suspend fun bulkCreate(requests: Flow<UserCreateRequest>): UserBulkCreateResponse {
        println("User bulk create request received")
        var usersCreated = 0L

        requests.collect {
            println("User created: ${it.name}")
            if (it.name.isNotEmpty()) {
                usersCreated++
            }
        }
        println("Users created: $usersCreated")
        return UserBulkCreateResponse.newBuilder().apply { count = usersCreated }.build()
    }

    override fun sendAction(requests: Flow<UserActionRequest>): Flow<UserActionResponse> = flow {
        requests.collect {
            println("Action received. Id: ${it.actionId}")
            for (i in 1..it.repeatTimes) {
                emit(buildUserActionResponse(it.actionId, it.repeatTimes))
                delay(250)
            }
        }
    }

    private fun buildUserActionResponse(requestActionId: Long, repeatTimes: Int) =
            UserActionResponse.newBuilder().apply {
                actionId = requestActionId;
                actionResponseId = requestActionId * random.nextInt(100..1000)
            }.build()

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