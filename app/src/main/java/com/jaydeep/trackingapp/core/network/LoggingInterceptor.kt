package com.jaydeep.trackingapp.core.network

import android.util.Log
import okhttp3.Interceptor
import okhttp3.Response

class LoggingInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        // Implementation for logging interceptor
        var request = chain.request()
        val startTime = System.currentTimeMillis()

        Log.d("HTTP_REQUEST", "-> ${request.method} ${request.url}")

        Log.d("HTTP_REQUEST", "→ ${request.method} ${request.url}")
        request.headers.forEach { (name, value) ->
            Log.d("HTTP_HEADER", "$name: $value")
        }
        request.body?.let {
            Log.d("HTTP_BODY", it.toString())
        }

        val response = chain.proceed(request)
        val duration = System.currentTimeMillis() - startTime

        Log.d("HTTP_RESPONSE", "← ${response.code} (${duration}ms)")
        response.headers.forEach { (name, value) ->
            Log.d("HTTP_HEADER_RESP", "$name: $value")
        }

        return response
    }
}