package com.example.baseproject.util

import com.example.baseproject.common.ApiService
import com.example.baseproject.common.Constants
import com.sun.dev.entity.*
import com.google.gson.Gson
import io.reactivex.Observable
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 *       Created by xiaolanlaia on 2019/5/6 13:39
 */
object RetrofitManager {


    /**
     * 设置okhttp
     */
    var okHttp = OkHttpClient.Builder()
        .addInterceptor {
            val request = it.request().newBuilder()
            request.addHeader("x-client-token", SharedHelper.getShared().getString("token", ""))
            val response = it.proceed(request.build())

            response
        }

        .addInterceptor(HttpLoggingInterceptor().apply {
            this.level = HttpLoggingInterceptor.Level.BODY
        })
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .writeTimeout(15, TimeUnit.SECONDS)

    val apiService = getRetrofit().create(ApiService::class.java)


    /**
     * 获取retrofit
     */
    private fun getRetrofit(): Retrofit {

        return Retrofit.Builder()
            .client(okHttp.build())
            .baseUrl(Constants.URL.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * 封装RequestBody
     */
    fun getRequestBody(str: String): RequestBody {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), str)
    }



    /**
     * 获取验证码
     */
    fun getPhoneCode(phone: String?): Observable<BaseBean> {
        var json = JSONObject()
        json.apply {
            this.put("phone", phone)
            this.put("secret", CodeUtil.encode(phone?.substring(phone.length - 4, phone.length) + Constants.URL.YAN))
        }
        return apiService.getPhoneCode(getRequestBody(json.toString()))
    }




}