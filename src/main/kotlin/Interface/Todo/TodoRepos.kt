package Interface.Todo

import DB.DataClasses.JsonTitle
import DB.DataClasses.RequestJsonTodo
import DB.DataClasses.TodoAll

interface TodoRepos{


    suspend fun addToDo(todo : TodoAll)
    suspend fun getAllTodo(email: String): List<RequestJsonTodo>
    suspend fun getTodoByTitle(input: JsonTitle): List<RequestJsonTodo>
    suspend fun deleteTodo(title : String , email: String): Boolean
    suspend fun updateDate(email:String , title: String , password:String , days : Long)
    suspend fun updateDes(email: String , title: String , password: String , description: String)
    suspend fun updateCompletion(email: String , title:String)
    suspend fun update(todo : JsonTitle, description: String, days : String, completed: String):Boolean

}