fun doAllTheJob() = runBlocking {
    // put your code here
    // use suspending functions
    // connectToServer() and loadData()
    val connectionJob = GlobalScope.launch {
        connectToServer()
    }
    connectionJob.join()
    val loadingJob = GlobalScope.launch {
        loadData()
    }
    loadingJob.join()
}