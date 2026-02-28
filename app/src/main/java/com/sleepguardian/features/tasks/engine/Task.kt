package com.sleepguardian.features.tasks.engine

interface Task {
    fun generate(): TaskData
    fun validate(answer: String): Boolean
    fun getCurrentProgress(): TaskProgress
    fun reset()
}

data class TaskData(
    val question: String,
    val hint: String? = null,
    val metadata: Map<String, Any> = emptyMap()
)

data class TaskProgress(
    val current: Int,
    val total: Int,
    val mistakes: Int
)
