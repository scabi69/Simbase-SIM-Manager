package com.xabi.simbase.api

import retrofit2.http.*

interface SimbaseApi {

    @GET("simcards")
    suspend fun getAllSims(
        @Header("Authorization") token: String
    ): SimListResponse

    @POST("simcards/{iccid}/state")
    suspend fun setSimState(
        @Path("iccid") iccid: String,
        @Header("Authorization") token: String,
        @Body body: SimStateRequest
    ): SimStateResponse
}