package DI.modules

import Cache.CacheRepos
import Interface.Todo.TodoRepos
import Interface.Todo.TodoReposImp
import Interface.TodoUtils.TodoUtils
import dagger.Provides
import javax.inject.Singleton
import dagger.Module

@Module
object TodoModule {

    @Provides
    @Singleton
    fun provideTodoRepos(
        todoUtils: TodoUtils,
        cacheRepos: CacheRepos
    ): TodoRepos {
        return TodoReposImp(todoUtils, cacheRepos) // Your implementation class
    }
}