import redis.clients.jedis.Jedis

object Client {
    private val REDIS_HOST = System.getenv("REDIS_HOST")
    private val REDIS_PORT = System.getenv("REDIS_PORT").toInt()

    val jedis: Jedis by lazy {
        Jedis(REDIS_HOST, REDIS_PORT).apply {
            println("Connected to Redis")
        }
    }
}