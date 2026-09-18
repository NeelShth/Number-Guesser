package com.example.numberguesser

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.numberguesser.ui.theme.DeepPurple
import com.example.numberguesser.ui.theme.MidnightBlue
import com.example.numberguesser.ui.theme.NumberGuesserTheme

private val GUESS_RANGE = 0..10

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NumberGuesserTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    NumberGuesser(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

enum class GuessResult(val message: String) {
    Invalid("Enter a number from ${GUESS_RANGE.first} to ${GUESS_RANGE.last}"),
    TooLow("Too low, try a higher number"),
    TooHigh("Too high, try a lower number"),
    Correct("Correct!")
}

fun checkGuess(input: String, answer: Int, range: IntRange = GUESS_RANGE): GuessResult {
    val guess = input.toIntOrNull()
    return when {
        guess == null || guess !in range -> GuessResult.Invalid
        guess == answer -> GuessResult.Correct
        guess > answer -> GuessResult.TooHigh
        else -> GuessResult.TooLow
    }
}

@Composable
fun NumberGuesser(modifier: Modifier = Modifier) {
    // rememberSaveable keeps state across rotation / process death
    var answer by rememberSaveable { mutableStateOf(GUESS_RANGE.random()) }
    var guess by rememberSaveable { mutableStateOf("") }
    var result by rememberSaveable { mutableStateOf<GuessResult?>(null) }
    var attempts by rememberSaveable { mutableStateOf(0) }

    val won = result == GuessResult.Correct

    fun submit() {
        val outcome = checkGuess(guess, answer)
        if (outcome != GuessResult.Invalid) attempts++
        result = outcome
    }

    fun playAgain() {
        answer = GUESS_RANGE.random()
        guess = ""
        result = null
        attempts = 0
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(DeepPurple, MidnightBlue)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
        ) {
            Text(
                text = "Number Guesser",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontFamily = FontFamily.Serif
            )

            Text(
                text = "I'm thinking of a number from ${GUESS_RANGE.first} to ${GUESS_RANGE.last}",
                color = Color.White.copy(alpha = 0.8f),
                fontFamily = FontFamily.Serif
            )

            result?.let {
                Text(
                    text = it.message,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontFamily = FontFamily.Serif
                )
            }

            TextField(
                value = guess,
                onValueChange = { guess = it.filter(Char::isDigit).take(3) },
                label = { Text("Your guess") },
                singleLine = true,
                enabled = !won,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { submit() })
            )

            Button(onClick = { if (won) playAgain() else submit() }) {
                Text(if (won) "Play again" else "Submit", fontFamily = FontFamily.Serif)
            }

            if (attempts > 0) {
                Text(
                    text = "Attempts: $attempts",
                    color = Color.White.copy(alpha = 0.7f),
                    fontFamily = FontFamily.Serif
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun NumberGuesserPreview() {
    NumberGuesserTheme {
        NumberGuesser()
    }
}