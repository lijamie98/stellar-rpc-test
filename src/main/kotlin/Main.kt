import com.google.gson.Gson
import kotlinx.coroutines.runBlocking
import org.stellar.sdk.xdr.TransactionEnvelope
import org.stellar.sdk.xdr.TransactionResult

fun main() = runBlocking {
    val gson = Gson()
    val stellarRpc = StellarRpc("https://soroban-testnet.stellar.org")
    println(gson.toJson(stellarRpc.getHealth()))
    val latestLedger = stellarRpc.getLatestLedger()!!.get("result") as Map<*, *>

    // The sequence number of the latest ledger
    val latestSequence = latestLedger.get("sequence").toString().toDouble().toInt()
    println("Latest ledger sequence: $latestSequence")

    // Stream the transactions from the latest 50 ledgers and stops at 1000 items
    streamTransactions(stellarRpc, latestSequence - 50, 1000)

    // Stream the events from the latest 50 ledgers and stops at 1000 items
    streamEvents(stellarRpc, latestSequence - 50, 1000)
}

fun streamTransactions(stellarRpc: StellarRpc, startLedger: Int, count: Int = -1) {
    var cursor: String? = null
    var result: Map<*, *>?
    var counter = 0
    while (count == -1 || counter < count) {
        result = if (cursor == null) {
            stellarRpc.getTransactions(startLedger, null)!!["result"] as Map<*, *>?
        } else {
            stellarRpc.getTransactions(null, cursor)!!["result"] as Map<*, *>?
        }
        if (result == null) break

        cursor = result["cursor"] as String
        if (result["transactions"] == null) continue
        val transactions = result["transactions"] as List<*>
        if (transactions.isEmpty()) break
        for (transaction in transactions) {
            printTransaction(transaction as Map<*, *>)
            println("$counter ====================================")
        }
        counter++
    }
}

fun printTransaction(txnWrapper: Map<*, *>) {
    val txnEnvelopeXdr = txnWrapper["envelopeXdr"] as String
    val txnXdr = txnWrapper["resultXdr"] as String

    val envelope = TransactionEnvelope.fromXdrBase64(txnEnvelopeXdr)
    val txn = TransactionResult.fromXdrBase64(txnXdr)
    println("Transaction Envelop: $envelope")
    println("Transaction: $txn")
}

fun streamEvents(stellarRpc: StellarRpc, startLedger: Int, count: Int = -1) {
    var cursor: String? = null
    var result: Map<*, *>?
    var counter = 0
    while (count == -1 || counter < count) {
        result = if (cursor == null) {
            stellarRpc.getEvents(startLedger, null)!!["result"] as Map<*, *>?
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