package DI.modules

import Cache.CacheRepos
import Cache.CacheReposImp
import dagger.Provides
import javax.inject.Singleton
import dagger.Module

@Module
object CacheModule {

    @Provides
    @Singleton
    fun provideCacheRepos(): CacheRepos {
        return CacheReposImp() // Your implementation class
    }
}