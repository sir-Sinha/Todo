package DI.modules

import Cache.CacheRepos
import Interface.TodoUtils.TodoUtils
import Interface.User.UserRepos
import Interface.User.UserReposImp
import dagger.Provides
import javax.inject.Singleton
import dagger.Module

@Module
object UserModule {

    @Provides
    @Singleton
    fun provideUserRepos(
        todoUtils: TodoUtils,
        cacheRepos: CacheRepos
    ): UserRepos {
        return UserReposImp(todoUtils, cacheRepos) // Your implementation class
    }
}