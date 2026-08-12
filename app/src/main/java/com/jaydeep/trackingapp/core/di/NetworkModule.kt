package com.jaydeep.trackingapp.core.di

import android.util.Log
import com.jaydeep.trackingapp.BuildConfig
import com.jaydeep.trackingapp.core.data.remote.api.AuthApi
import com.jaydeep.trackingapp.core.data.remote.api.ExpenseApi
import com.jaydeep.trackingapp.core.data.remote.api.HealthApi
import com.jaydeep.trackingapp.core.data.remote.api.ProteinApi
import com.jaydeep.trackingapp.core.network.AuthAuthenticator
import com.jaydeep.trackingapp.core.network.AuthInterceptor
import com.jaydeep.trackingapp.core.network.LoggingInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Suppress("unused")
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    private const val TAG = "NetworkModule"

    @Provides
    @Singleton
    fun provideMoshi(): Moshi =
        Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenStore: TokenStore,
        authAuthenticator: AuthAuthenticator
    ): OkHttpClient {

        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("HTTP", message)
        }.apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor(AuthInterceptor(tokenStore))
            .authenticator(authAuthenticator)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit {


        Log.d(
            TAG,
            "========== provideRetrofit() CALLED =========="
        )

        Log.d(
            TAG,
            "BASE_URL = ${BuildConfig.BASE_URL}"
        )

        return Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(
                MoshiConverterFactory.create(moshi)
            )
            .client(okHttpClient)
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthApi(
        retrofit: Retrofit
    ): AuthApi =
        retrofit.create(AuthApi::class.java)

    @Provides
    @Singleton
    fun provideExpenseApi(
        retrofit: Retrofit
    ): ExpenseApi =
        retrofit.create(ExpenseApi::class.java)

    @Provides
    @Singleton
    fun provideProteinApi(
        retrofit: Retrofit
    ): ProteinApi =
        retrofit.create(ProteinApi::class.java)

    @Provides
    @Singleton
    fun provideHealthApi(retrofit: Retrofit): HealthApi =
        retrofit.create(HealthApi::class.java)

//    @Provides
//    @Singleton
//    fun provideSummaryApi(retrofit: Retrofit): SummaryApi =
//        retrofit.create(SummaryApi::class.java)
//
//    @Provides
//    @Singleton
//    fun provideUserApi(retrofit: Retrofit): UserApi =
//        retrofit.create(UserApi::class.java)
}