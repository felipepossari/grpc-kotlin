package com.felipepossari.grpc.server

import com.felipepossari.grpc.UserCreateRequest
import com.felipepossari.grpc.UserCreateResponse
import com.felipepossari.grpc.UserServiceGrpcKt

class UserEndpoint : UserServiceGrpcKt.UserServiceCoroutineImplBase() {

    override suspend fun create(request: UserCreateRequest): UserCreateResponse {

        return UserCreateResponse.newBuilder()
                .setCountry(request.country)
                .setEmail(request.email)
                .setName(request.name)
                .setId(1L)
                .build()
    }
}