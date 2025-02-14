package DI.modules

import Interface.TodoUtils.TodoUtils
import Interface.TodoUtils.TodoUtilsImp
import dagger.Provides
import javax.inject.Singleton
import dagger.Module

@Module
object TodoUtilsModule {

    @Provides
    @Singleton
    fun provideTodoUtils(): TodoUtils {
        return TodoUtilsImp() // Your implementation class
    }
}