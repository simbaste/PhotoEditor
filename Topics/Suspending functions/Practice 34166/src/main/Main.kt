suspend fun bakeCake() {
    println("I'm a cyber baker. I'm starting to bake fast!")
    delaySeconds(5)
    println("The cake is ready!")
}

// Please change only the "main" function:
suspend fun main() {
    bakeCake()
    bakeCake()
    bakeCake()
}