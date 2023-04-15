package com.cupcake.todo.model.network

import com.cupcake.todo.BuildConfig
import com.cupcake.todo.model.network.response.AddPersonalTaskResponse
import com.cupcake.todo.model.network.response.AddTeamTaskResponse
import com.cupcake.todo.model.network.response.BaseResponse
import com.cupcake.todo.model.network.response.Register
import com.cupcake.todo.model.network.response.TeamTaskResponse
import com.cupcake.todo.model.network.util.ApiCallback
import com.cupcake.todo.model.network.util.ApiEndPoint
import com.cupcake.todo.model.network.util.enqueueCall
import okhttp3.FormBody

class ApiServiceImpl : ApiService {

    private val client = ApiClient()

    override fun register(
        username: String,
        password: String,
        onSuccess: (response: BaseResponse<Register>) -> Unit,
        onFailure: (throwable: Throwable, statusCode: Int?, message: String?) -> Unit
    ) {
        val registerBody = FormBody.Builder()
            .add(USERNAME, username)
            .add(PASSWORD, password)
            .add(TEAM_ID, BuildConfig.TEAM_ID)
            .build()

        client.postRequest(ApiEndPoint.register, registerBody)
            .enqueueCall(
                object : ApiCallback<BaseResponse<Register>> {
                    override fun onSuccess(response: BaseResponse<Register>) {
                        onSuccess(response)
                    }

                    override fun onFailure(
                        throwable: Throwable,
                        statusCode: Int?,
                        message: String?
                    ) {
                        onFailure(throwable, statusCode, message)
                    }
                })
    }

    override fun addTeamTask(
        title: String,
        description: String,
        assignee: String,
        callback: ApiCallback<BaseResponse<AddTeamTaskResponse>>
    ) {
        val teamTask = FormBody.Builder()
            .add(TITLE, title)
            .add(DESCRIPTION, description)
            .add(ASSIGNEE, assignee)
            .build()

        client.postRequest(ApiEndPoint.toDoTeam, teamTask)
            .enqueueCall(
                object : ApiCallback<BaseResponse<AddTeamTaskResponse>>{
                    override fun onSuccess(response: BaseResponse<AddTeamTaskResponse>) {
                        callback.onSuccess(response)
                    }

                    override fun onFailure(
                        throwable: Throwable,
                        statusCode: Int?,
                        message: String?
                    ) {
                        callback.onFailure(throwable, statusCode, message)
                    }

                }
            )
    }

    override fun getTeamTasks(callback: ApiCallback<BaseResponse<List<TeamTaskResponse>>>) {
        client.getRequest(ApiEndPoint.toDoTeam).enqueueCall(
            object : ApiCallback<BaseResponse<List<TeamTaskResponse>>>{
                override fun onSuccess(response: BaseResponse<List<TeamTaskResponse>>) {
                    callback.onSuccess(response)
                }

                override fun onFailure(throwable: Throwable, statusCode: Int?, message: String?) {
                    callback.onFailure(throwable,statusCode,message)
                }

            }
        )
    }

    override fun addPersonalTask(
        title: String,
        description: String,
        callback: ApiCallback<BaseResponse<AddPersonalTaskResponse>>,
    ) {
        val body=FormBody.Builder()
            .add(TITLE,title)
            .add(DESCRIPTION,description)
            .build()
        client.postRequest(ApiEndPoint.toDoPersonal,body).enqueueCall(
            object :ApiCallback<BaseResponse<AddPersonalTaskResponse>>{
                override fun onSuccess(response: BaseResponse<AddPersonalTaskResponse>) {
                    callback.onSuccess(response)
                }

                override fun onFailure(throwable: Throwable, statusCode: Int?, message: String?) {
                    callback.onFailure(throwable, statusCode, message)
                }

            }
        )
    }

    override fun updateTaskStatus(
        id: String,
        status: Int,
        isPersonalTask: Boolean,
        callback: ApiCallback<BaseResponse<String>>
    ) {
        val updateStatusBody = FormBody.Builder()
            .add(ID, id)
            .add(STATUS, status.toString())
            .build()

        var endPoint = ApiEndPoint.toDoTeam
        if (isPersonalTask) {
            endPoint = ApiEndPoint.toDoPersonal
        }
        client.putRequest(endPoint, updateStatusBody)
            .enqueueCall(
                object : ApiCallback<BaseResponse<String>> {
                    override fun onSuccess(response: BaseResponse<String>) {
                        callback.onSuccess(response)
                    }

                    override fun onFailure(
                        throwable: Throwable,
                        statusCode: Int?,
                        message: String?
                    ) {
                        callback.onFailure(throwable, statusCode, message)
                    }
                })
    }

    private companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val TEAM_ID = "teamId"

        const val TITLE = "title"
        const val DESCRIPTION = "description"
        const val ASSIGNEE = "assignee"

        const val ID = "id"
        const val STATUS = "status"
    }

}