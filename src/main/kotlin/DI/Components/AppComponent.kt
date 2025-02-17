package DI.Components

import DI.modules.CacheModule
import DI.modules.TodoModule
import DI.modules.TodoUtilsModule
import DI.modules.UserModule
import Interface.Todo.TodoRepos
import Interface.User.UserRepos
import dagger.Component
import io.ktor.server.application.*
import javax.inject.Singleton

@Singleton
@Component(modules = [TodoUtilsModule::class, CacheModule::class, TodoModule::class, UserModule::class])
interface AppComponent {

    fun inject(app: Application)

    fun getTodoRepos(): TodoRepos
    fun getUserRepos(): UserRepos

    @Component.Factory
    interface Factory {
        fun create(): AppComponent
    }
}
