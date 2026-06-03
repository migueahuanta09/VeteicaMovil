package com.example.veteica.network

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body body: Map<String, String>): Response<Map<String, Any>>

    @POST("api/auth/register")
    suspend fun register(@Body body: Map<String, String>): Response<Map<String, Any>>

    @GET("api/pets")
    suspend fun getPets(): Response<Map<String, Any>>

    @GET("api/pets/{id}")
    suspend fun getPet(@Path("id") id: String): Response<Map<String, Any>>

    @POST("api/pets")
    suspend fun createPet(@Body body: Map<String, String>): Response<Map<String, Any>>

    @PUT("api/pets/{id}")
    suspend fun updatePet(@Path("id") id: String, @Body body: Map<String, String>): Response<Map<String, Any>>

    @DELETE("api/pets/{id}")
    suspend fun deletePet(@Path("id") id: String): Response<Map<String, Any>>

    @GET("api/pets/{id}/medical-history")
    suspend fun getMedicalHistory(@Path("id") id: String): Response<Map<String, Any>>

    @GET("api/pets/{id}/vaccines")
    suspend fun getVaccines(@Path("id") id: String): Response<Map<String, Any>>

    @GET("api/owners")
    suspend fun getOwners(): Response<Map<String, Any>>

    @POST("api/owners")
    suspend fun createOwner(@Body body: Map<String, String>): Response<Map<String, Any>>

    @PUT("api/owners/{id}")
    suspend fun updateOwner(@Path("id") id: String, @Body body: Map<String, String>): Response<Map<String, Any>>

    @DELETE("api/owners/{id}")
    suspend fun deleteOwner(@Path("id") id: String): Response<Map<String, Any>>

    @GET("api/appointments")
    suspend fun getAppointments(): Response<Map<String, Any>>

    @POST("api/appointments")
    suspend fun createAppointment(@Body body: Map<String, Any>): Response<Map<String, Any>>

    @PUT("api/appointments/{id}/complete")
    suspend fun completeAppointment(@Path("id") id: String): Response<Map<String, Any>>

    @PUT("api/appointments/{id}/cancel")
    suspend fun cancelAppointment(@Path("id") id: String): Response<Map<String, Any>>

    @GET("api/products")
    suspend fun getProducts(): Response<Map<String, Any>>

    @POST("api/products")
    suspend fun createProduct(@Body body: Map<String, String>): Response<Map<String, Any>>

    @PUT("api/products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body body: Map<String, String>): Response<Map<String, Any>>

    @DELETE("api/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): Response<Map<String, Any>>

    @GET("api/services")
    suspend fun getServices(): Response<Map<String, Any>>

    @POST("api/services")
    suspend fun createService(@Body body: Map<String, String>): Response<Map<String, Any>>

    @GET("api/payments/pending")
    suspend fun getPendingPayments(): Response<Map<String, Any>>

    @POST("api/payments/{id}/charge")
    suspend fun chargePayment(@Path("id") id: String, @Body body: Map<String, String>): Response<Map<String, Any>>

    @GET("api/dashboard")
    suspend fun getDashboard(): Response<Map<String, Any>>
}