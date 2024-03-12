import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

fun doAllTheJob() = runBlocking {
    // put your code here
    // use suspending functions
    // printProgress() and loadData()
    val loadJob = GlobalScope.launch(Dispatchers.IO) {
        loadData()
    }
    val progressJob = GlobalScope.launch(Dispatchers.IO) {
        printProgress()
    }
    loadJob.join()
    progressJob.cancel()
}