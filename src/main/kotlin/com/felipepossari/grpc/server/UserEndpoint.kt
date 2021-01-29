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
import io.grpc.StatusException
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import java.util.*
import kotlin.random.Random
import kotlin.random.nextInt

class UserEndpoint : UserServiceGrpcKt.UserServiceCoroutineImplBase() {

    private val random = Random(123456)

    override suspend fun create(request: UserCreateRequest): UserCreateResponse = runCatching {

        if (request.email.isEmpty()) {
            throw IllegalArgumentException("Email null or empty")
        }

        return UserCreateResponse.newBuilder().apply {
            email = request.email;
            name = request.name;
            country = request.country
        }.build()
    }.onFailure { e -> throw e.toStatusException() }.getOrThrow()

    override fun sendPush(request: PushRequest): Flow<PushResponse> = runCatching {
        flow {
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
    }.onFailure { e -> throw e.toStatusException() }.getOrThrow()

    override suspend fun bulkCreate(requests: Flow<UserCreateRequest>): UserBulkCreateResponse = runCatching {
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
    }.onFailure { e -> throw e.toStatusException() }.getOrThrow()

    override fun sendAction(requests: Flow<UserActionRequest>): Flow<UserActionResponse> = runCatching {
        flow {
            requests.collect {
                println("Action received. Id: ${it.actionId}")
                for (i in 1..it.repeatTimes) {
                    emit(buildUserActionResponse(it.actionId, it.repeatTimes))
                    delay(250)
                }
            }
        }
    }.onFailure { e -> throw e.toStatusException() }.getOrThrow()

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

    private fun Throwable.toStatusException(): StatusException =
            when (this) {
                is IllegalArgumentException -> io.grpc.Status.INVALID_ARGUMENT
                        .withCause(this.cause)
                        .withDescription(this.message)
                        .asException()
                else -> io.grpc.Status.INTERNAL
                        .withCause(this.cause)
                        .withDescription(this.message)
                        .asException()
            }
}