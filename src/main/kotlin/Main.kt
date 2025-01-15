import com.google.gson.Gson
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val gson = Gson()
    val stellarRpc = StellarRpc("https://soroban-testnet.stellar.org")
    println(gson.toJson(stellarRpc.getHealth()))
    val latestLedger = stellarRpc.getLatestLedger()!!.get("result") as Map<*, *>

    // The sequence number of the latest ledger
    val latestSequence = latestLedger.get("sequence").toString().toDouble().toInt()
    println("Latest ledger sequence: $latestSequence")

    // Stream the events
    var cursor: String? = null
    var result: Map<*, *>?
    while (true) {
        result = if (cursor == null) {
            stellarRpc.getEvents(latestSequence - 10000, null)!!["result"] as Map<*, *>?
        } else {
            stellarRpc.getEvents(null, cursor)!!["result"] as Map<*, *>?
        }
        if (result == null) break

        cursor = result["cursor"] as String
        val events = result["events"] as List<*>
        if (events.isEmpty()) break
        for (event in events) {
            println(event)
        }
    }
}
