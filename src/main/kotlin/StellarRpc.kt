import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

class StellarRpc
    (url: String) {
    private val typeToken = object : TypeToken<Map<String, Any>>() {}.type
    private var builder: okhttp3.Request.Builder = okhttp3.Request.Builder().url(url)
    private val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    private val client = OkHttpClient()
    private val gson = Gson()

    fun getHealth(): Map<String, Any>? {
        return callRpc(
            """ {
            "jsonrpc": "2.0",
            "id": 8675309,
            "method": "getHealth"
        }""".trimIndent().toRequestBody(jsonMediaType)
        )
    }

    fun getLatestLedger(): Map<String, Any>? {
        return callRpc(
            """
            {
              "jsonrpc": "2.0",
              "id": 8675309,
              "method": "getLatestLedger"
            }
        """.trimIndent().toRequestBody(jsonMediaType)
        )
    }

    fun getEvents(startLedger: Int?, cursor: String?): Map<String, Any>? {
        if (cursor != null) {
            return callRpc(
                """
                {
                   "jsonrpc":"2.0",
                   "id":8675309,
                   "method":"getEvents",
                   "params":{
                      "filters":[
                          { "type": "diagnostic"}
                      ],
                      "pagination":{
                         "cursor":"$cursor",
                         "limit": 10
                      }
                   }
                }
            """.trimIndent().toRequestBody(jsonMediaType)
            )
        } else {
            return callRpc(
                """
            {
               "jsonrpc":"2.0",
               "id":8675309,
               "method":"getEvents",
               "params":{
                  "startLedger":$startLedger,
                  "filters":[
                          { "type": "diagnostic"}
                  ],
                  "pagination":{
                     "limit": 10
                  }
               }
            }
        """.trimIndent().toRequestBody(jsonMediaType)
            )
        }
    }

    fun getTransactions(startLedger: Int?, cursor: String?): Map<String, Any>? {
        if (cursor != null) {
            return callRpc(
                """
                {
                   "jsonrpc":"2.0",
                   "id":8675309,
                   "method":"getTransactions",
                   "params":{
                      "pagination":{
                         "cursor":"$cursor",
                         "limit": 10
                      }
                   }
                }                    
            """.trimIndent().toRequestBody(jsonMediaType)
            )
        } else {
            return callRpc(
                """                
                {
                  "jsonrpc": "2.0",
                  "id": 8675309,
                  "method": "getTransactions",
                  "params": {
                    "startLedger": $startLedger,
                    "pagination": {
                      "limit": 10
                    }
                  }
                }
        """.trimIndent().toRequestBody(jsonMediaType)
            )
        }
    }

    private fun callRpc(body: RequestBody): Map<String, Any>? {
        val request = builder.post(body).build()
        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) throw IOException("Unexpected code $response")
            return gson.fromJson(response.body?.string() ?: "", typeToken)
        }
    }

}
