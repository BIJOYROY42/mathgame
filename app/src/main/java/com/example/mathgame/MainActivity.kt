package com.example.mathgame

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.mathgame.ui.theme.MathgameTheme
import androidx.compose.foundation.layout.ColumnScope // Added import
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment // Added import

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MathgameTheme {
                MathGameApp()
            }
        }
    }
}

@Composable
fun MathGameApp() {
    val navController = rememberNavController()
    var numberOfQuestions by remember { mutableStateOf(0) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var correctCount by remember { mutableStateOf(0) }
    var wrongCount by remember { mutableStateOf(0) }
    var questions by remember { mutableStateOf(listOf<Pair<Int, Int>>()) }

    NavHost(navController = navController, startDestination = "start") {
        composable("start") {
            StartScreen(
                onStart = { count ->
                    numberOfQuestions = count
                    questions = createRandomQuestions(count)
                    navController.navigate("question")
                }
            )
        }
        composable("question") {
            if (currentQuestionIndex < numberOfQuestions) {
                QuestionScreen(
                    question = questions[currentQuestionIndex],
                    questionNumber = currentQuestionIndex + 1,
                    totalQuestions = numberOfQuestions,
                    correctCount = correctCount,
                    wrongCount = wrongCount,
                    onNext = { isCorrect ->
                        if (isCorrect) correctCount++ else wrongCount++
                        currentQuestionIndex++
                        if (currentQuestionIndex >= numberOfQuestions) {
                            navController.navigate("result")
                        }
                    },
                    onCancel = {
                        navController.navigate("result")
                    }
                )
            } else {
                navController.navigate("result")
            }
        }
        composable("result") {
            ResultScreen(
                correctCount = correctCount,
                wrongCount = wrongCount,
                onRestart = {
                    currentQuestionIndex = 0
                    correctCount = 0
                    wrongCount = 0
                    navController.navigate("start")
                }
            )
        }
    }
}

@Composable
fun StartScreen(onStart: (Int) -> Unit) {
    var input by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Enter number of questions:")
        Spacer(Modifier.height(8.dp))
        TextField(value = input, onValueChange = { input = it })
        Spacer(Modifier.height(8.dp))
        Button(onClick = {
            val count = input.toIntOrNull() ?: 0
            onStart(count)
        }) {
            Text("Start")
        }
    }
}

@Composable
fun QuestionScreen(
    question: Pair<Int, Int>,
    questionNumber: Int,
    totalQuestions: Int,
    correctCount: Int,
    wrongCount: Int,
    onNext: (Boolean) -> Unit,
    onCancel: () -> Unit
) {
    var answer by remember { mutableStateOf("") }
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Correct: $correctCount  |  Wrong: $wrongCount")
        Spacer(Modifier.height(16.dp))
        Text("Question $questionNumber / $totalQuestions")
        Text("${question.first} + ${question.second} = ?")
        Spacer(Modifier.height(16.dp))
        TextField(value = answer, onValueChange = { answer = it })
        Spacer(Modifier.height(16.dp))
        Row {
            Button(onClick = {
                val userAnswer = answer.toIntOrNull() ?: -999999
                onNext(userAnswer == question.first + question.second)
            }) {
                Text("Next")
            }
            Spacer(Modifier.width(8.dp))
            OutlinedButton(onClick = onCancel) {
                Text("Cancel")
            }
        }
    }
}

@Composable
fun ResultScreen(correctCount: Int, wrongCount: Int, onRestart: () -> Unit) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Game Over!")
        Text("Correct: $correctCount")
        Text("Wrong: $wrongCount")
        Spacer(Modifier.height(16.dp))
        Button(onClick = onRestart) {
            Text("Restart")
        }
    }
}

private fun createRandomQuestions(count: Int): List<Pair<Int, Int>> {
    return List(count) {
        val a = (1..20).random()
        val b = (1..20).random()
        a to b
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MathgameTheme {
        StartScreen {}
    }
}