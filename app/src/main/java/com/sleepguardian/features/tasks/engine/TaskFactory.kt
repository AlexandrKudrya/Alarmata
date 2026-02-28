package com.sleepguardian.features.tasks.engine

import com.sleepguardian.domain.models.Difficulty
import com.sleepguardian.domain.models.TaskType
import com.sleepguardian.features.tasks.implementations.MathTask

object TaskFactory {

    fun create(type: TaskType, difficulty: Difficulty): Task {
        return when (type) {
            TaskType.MATH -> MathTask(difficulty)
            // Other task types will be added in future iterations
            else -> MathTask(difficulty)
        }
    }
}
