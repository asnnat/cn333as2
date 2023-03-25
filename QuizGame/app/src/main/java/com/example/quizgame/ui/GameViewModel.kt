package com.example.quizgame.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.quizgame.data.MAX_NO_OF_QUESTIONS
import com.example.quizgame.data.SCORE_INCREASE
import com.example.quizgame.data.allQuestions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class GameViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    var userGuess by mutableStateOf("")
        private set

    // question used in the game
    private var usedQuestions: MutableSet<String> = mutableSetOf()

    // question and answer in the turn
    private lateinit var currentQuestion: String
    private lateinit var currentAnswer: String

    init {
        resetGame()
    }

    fun resetGame() {
        usedQuestions.clear()

        val data = pickRandomQuestionAndShuffle()

        _uiState.value = GameUiState(currentQuestion = data[0], currentAnswer = data[1])
    }

    fun checkUserGuess(selectedItem: String? = null) {
        if (selectedItem == currentAnswer) {
            val updatedScore = _uiState.value.score.plus(SCORE_INCREASE)

            updateGameState(updatedScore)
        } else {
            skipQuestion()
        }
    }

    fun skipQuestion() {
        updateGameState(_uiState.value.score)
    }

    private fun updateGameState(updatedScore: Int) {
        if (usedQuestions.size == MAX_NO_OF_QUESTIONS){
            // last turn
            _uiState.update { currentState ->
                currentState.copy(
                    score = updatedScore,
                    isGameOver = true
                )
            }
        } else {
            val data = pickRandomQuestionAndShuffle()

            _uiState.update { currentState ->
                currentState.copy(
                    currentQuestion = data[0],
                    currentAnswer = data[1],
                    currentQuestionCount = currentState.currentQuestionCount.inc(),
                    score = updatedScore
                )
            }
        }
    }

    private fun pickRandomQuestionAndShuffle(): List<String> {
        // random unique question
        val randomQuestion = allQuestions.random()
        currentQuestion = randomQuestion[0]
        currentAnswer = randomQuestion[1]

        if (usedQuestions.contains(currentQuestion)) {
            return pickRandomQuestionAndShuffle()
        } else {
            usedQuestions.add(currentQuestion)

            return listOf(currentQuestion, currentAnswer)
        }
    }
}
