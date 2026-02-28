package com.sleepguardian.features.tasks.implementations

import com.sleepguardian.domain.models.Difficulty
import com.sleepguardian.features.tasks.engine.Task
import com.sleepguardian.features.tasks.engine.TaskData
import com.sleepguardian.features.tasks.engine.TaskProgress
import kotlin.random.Random

class MathTask(private val difficulty: Difficulty) : Task {

    private val totalProblems = when (difficulty) {
        Difficulty.EASY -> 3
        Difficulty.MEDIUM -> 4
        Difficulty.HARD -> 5
    }

    private var solvedCount = 0
    private var mistakeCount = 0
    private var currentAnswer = 0
    private var currentData: TaskData? = null

    override fun generate(): TaskData {
        val (question, answer) = when (difficulty) {
            Difficulty.EASY -> generateEasy()
            Difficulty.MEDIUM -> generateMedium()
            Difficulty.HARD -> generateHard()
        }
        currentAnswer = answer
        currentData = TaskData(
            question = question,
            metadata = mapOf("answer" to answer)
        )
        return currentData!!
    }

    override fun validate(answer: String): Boolean {
        val parsed = answer.trim().toIntOrNull() ?: return false.also { mistakeCount++ }
        return if (parsed == currentAnswer) {
            solvedCount++
            true
        } else {
            mistakeCount++
            false
        }
    }

    override fun getCurrentProgress(): TaskProgress {
        return TaskProgress(
            current = solvedCount,
            total = totalProblems,
            mistakes = mistakeCount
        )
    }

    override fun reset() {
        solvedCount = 0
        mistakeCount = 0
        currentAnswer = 0
        currentData = null
    }

    private fun generateEasy(): Pair<String, Int> {
        val a = Random.nextInt(1, 10)
        val b = Random.nextInt(1, 10)
        return if (Random.nextBoolean()) {
            "$a + $b = ?" to (a + b)
        } else {
            val big = maxOf(a, b)
            val small = minOf(a, b)
            "$big − $small = ?" to (big - small)
        }
    }

    private fun generateMedium(): Pair<String, Int> {
        val op = Random.nextInt(3)
        return when (op) {
            0 -> {
                val a = Random.nextInt(10, 100)
                val b = Random.nextInt(10, 100)
                "$a + $b = ?" to (a + b)
            }
            1 -> {
                val a = Random.nextInt(10, 100)
                val b = Random.nextInt(10, a + 1)
                "$a − $b = ?" to (a - b)
            }
            else -> {
                val a = Random.nextInt(2, 20)
                val b = Random.nextInt(2, 10)
                "$a × $b = ?" to (a * b)
            }
        }
    }

    private fun generateHard(): Pair<String, Int> {
        val variant = Random.nextInt(3)
        return when (variant) {
            0 -> {
                val a = Random.nextInt(10, 100)
                val b = Random.nextInt(2, 10)
                val c = Random.nextInt(10, 100)
                "$a × $b + $c = ?" to (a * b + c)
            }
            1 -> {
                val a = Random.nextInt(10, 100)
                val b = Random.nextInt(2, 10)
                val c = Random.nextInt(1, a * b)
                "$a × $b − $c = ?" to (a * b - c)
            }
            else -> {
                val a = Random.nextInt(100, 500)
                val b = Random.nextInt(100, 500)
                "$a + $b = ?" to (a + b)
            }
        }
    }
}
