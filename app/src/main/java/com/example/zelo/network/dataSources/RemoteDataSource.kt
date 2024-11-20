package com.example.zelo.network.dataSources

import android.util.Log
import com.example.zelo.network.NetworkError
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.io.IOException

abstract class RemoteDataSource {
    private val json = Json { ignoreUnknownKeys = true }

    suspend fun <T : Any> handleApiResponse(
        apiCall: suspend () -> Response<T>
    ): T {
        try {
            val response = apiCall()
            val body = response.body()
            if (response.isSuccessful && body != null) {
                return body
            }
            response.errorBody()?.let {
                response.code()
                val error = json.decodeFromString<NetworkError>(it.string())
                throw DataSourceException(response.code(), error.message)
            }
            throw DataSourceException(UNEXPECTED_ERROR, "Missing error")
        } catch (e: DataSourceException) {
            Log.d("RemoteDataSource", "handleApiResponse: $e")
            throw e
        }catch (e: IOException)
        {
            Log.d("RemoteDataSource", "handleApiResponse: $e")
            throw DataSourceException(CONNECTION_ERROR, "Connection error")
        } catch (e: Exception) {
            Log.d("RemoteDataSource", "handleApiResponse: $e")
            throw DataSourceException(UNEXPECTED_ERROR, "Unexpected error")
        }
    }
    companion object {
        const val CONNECTION_ERROR = 0
        const val UNEXPECTED_ERROR = 1
    }
}
