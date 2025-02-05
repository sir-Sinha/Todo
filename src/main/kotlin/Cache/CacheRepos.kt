package Cache

interface CacheRepos {
    fun get(key: String): String?
    fun set(key: String, value: String)
    fun del(key: String)
    fun sadd(key: String, value: String)
    fun srem(key: String, value: String)
    fun hexists(key: String, field: String): Boolean
    fun hset(key: String, field: String, value: String)
    fun hdel(key: String, field: String)
    fun smembers(key: String): Set<String>
}