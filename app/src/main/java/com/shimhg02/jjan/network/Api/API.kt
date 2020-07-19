package com.shimhg02.jjan.network.Api

import com.shimhg02.jjan.network.Data.LogIn
import com.shimhg02.jjan.network.Data.Users
import retrofit2.Call
import retrofit2.http.*


interface API {

    @POST("/auth/user/login")
    @FormUrlEncoded
    fun logIn(@Field("userID") id : String, @Field("password") pw : String) :  Call<LogIn>


    @POST("/auth/user")
    @FormUrlEncoded
    fun signUp(@Field("userID") id : String, @Field("password") pw : String, @Field("sex") sex : String, @Field("age") age : String) :  Call<LogIn>


    @GET("/auth/user/my")
    fun getLogIn(@Header("Authorization") Authorization : String) :  Call<Users>


}