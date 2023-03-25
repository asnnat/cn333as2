package com.example.quizgame.ui

import android.app.Activity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.quizgame.data.allAnswers
import com.example.quizgame.ui.theme.QuizGameTheme

@Composable
fun GameScreen(
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel = viewModel()
) {
    val gameUiState by gameViewModel.uiState.collectAsState()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GameStatus(
            questionCount = gameUiState.currentQuestionCount,
            score = gameUiState.score
        )

        GameLayout(
            onKeyboardDone = { gameViewModel.checkUserGuess() },
            currentQuestion = gameUiState.currentQuestion as String,
            currentAnswer = gameUiState.currentAnswer,
            gameViewModel = gameViewModel
        )

        Row(
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            OutlinedButton(
                onClick = { gameViewModel.skipQuestion() },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 8.dp)
            ) {
                Text(text = "Skip")
            }
        }

        if (gameUiState.isGameOver) {
            FinalScoreDialog(
                score = gameUiState.score,
                onPlayAgain = { gameViewModel.resetGame() }
            )
        }
    }
}

@Composable
fun GameStatus(questionCount: Int, score: Int, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
            .size(48.dp),
    ) {
        Text(
            text = "$questionCount / 10",
            fontSize = 18.sp,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth(Alignment.End),
            text = "Score: $score",
            fontSize = 18.sp,
        )
    }
}

@Composable
fun GameLayout(
    currentQuestion: String,
    currentAnswer:String,
    onKeyboardDone: () -> Unit,
    modifier: Modifier = Modifier,
    gameViewModel: GameViewModel
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(24.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Text(
            text = currentQuestion,
            fontSize = 45.sp,
            modifier = modifier.align(Alignment.CenterHorizontally)
        )

        Text(
            text = "Which one is the best choice?",
            fontSize = 17.sp,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        val radioOptions = ArrayList<String>()
        radioOptions.add(currentAnswer)
        while (radioOptions.size != 4) {
            val answer = allAnswers.random()
            if (! radioOptions.contains(answer)) {
                radioOptions.add(answer)
            }
        }
        radioOptions.shuffle()
        var selectedItem by remember { mutableStateOf(radioOptions[0]) }

        Column(modifier = Modifier.selectableGroup()) {

            radioOptions.forEach { label ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .selectable(
                            selected = (selectedItem == label),
                            onClick = { gameViewModel.checkUserGuess(label) },
                            role = Role.RadioButton
                        )
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        modifier = Modifier.padding(end = 16.dp),
                        selected = (selectedItem == label),
                        onClick = {
                            gameViewModel.checkUserGuess(label) }
                    )
                    Text(text = label)
                }
            }
        }
    }
}

@Composable
private fun FinalScoreDialog(
    score: Int,
    onPlayAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    val activity = (LocalContext.current as Activity)

    AlertDialog(
        onDismissRequest = {},
        title = { Text(text = "Congratulations!") },
        text = { Text(text = "You got $score out of 10") },
        modifier = modifier,
        dismissButton = {
            TextButton(
                onClick = {
                    activity.finish()
                }
            ) {
                Text(text = "Exit")
            }
        },
        confirmButton = {
            TextButton(onClick = onPlayAgain) {
                Text(text = "Restart")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun GameScreenPreview() {
    QuizGameTheme {
        GameScreen()
    }
}