import redis.clients.jedis.Jedis

object Client {
    // FIXME: Read config properties from config/properties file only, do not hardcode values
    private const val REDIS_HOST = "localhost"
    private const val REDIS_PORT = 6379

    val jedis: Jedis by lazy {
        Jedis(REDIS_HOST, REDIS_PORT).apply {
            println("Connected to Redis")
        }
    }
}