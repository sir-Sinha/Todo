package Cache

import Client

class CacheReposImp : CacheRepos {
    override fun get(key: String): String? {
        return Client.jedis.get(key)
    }

    override fun set(key: String, value: String) {
        Client.jedis.set(key, value)
    }

    override fun del(key: String) {
        Client.jedis.del(key)
    }

    override fun sadd(key: String, value: String) {
        Client.jedis.sadd(key, value)
    }

    override fun srem(key: String, value: String) {
        Client.jedis.srem(key, value)
    }

    override fun hexists(key: String, field: String): Boolean {
        return Client.jedis.hexists(key, field)
    }

    override fun hset(key: String, field: String, value: String) {
        Client.jedis.hset(key, field, value)
    }

    override fun hdel(key: String, field: String) {
        Client.jedis.hdel(key, field)
    }

    override fun smembers(key: String): Set<String> {
        return Client.jedis.smembers(key)
    }
}
