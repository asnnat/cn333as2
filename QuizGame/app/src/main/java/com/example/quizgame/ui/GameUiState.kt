package com.example.quizgame.ui

data class GameUiState(
    val currentQuestion: String = "",
    val currentAnswer: String = "",
    val currentQuestionCount: Int = 1,
    val score: Int = 0,
    val isGameOver: Boolean = false
)