package com.example.calculadora

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    CalculatorScreen()
                }
            }
        }
    }
}

@Composable
fun CalculatorScreen() {
    var display by remember { mutableStateOf("0") }
    var expression by remember { mutableStateOf("") }
    var isNewOperation by remember { mutableStateOf(false) }
    var showScientific by remember { mutableStateOf(false) }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
    val scrollState = rememberScrollState()

    fun onButtonClick(value: String) {
        when (value) {
            "C" -> {
                display = "0"
                expression = ""
                isNewOperation = false
            }
            "=" -> {
                try {
                    val fullExpr = expression + display
                    val result = evaluateExpression(fullExpr)
                    display = if (result % 1.0 == 0.0) result.toLong().toString() else result.toString()
                    expression = ""
                    isNewOperation = true
                } catch (_: Exception) {
                    display = "Erro"
                    expression = ""
                    isNewOperation = true
                }
            }
            "+", "-", "*", "/" -> {
                if (display != "Erro") {
                    if (isNewOperation) {
                        expression = "$display $value "
                        isNewOperation = false
                    } else {
                        expression += "$display $value "
                    }
                    display = "0"
                }
            }
            "." -> {
                if (isNewOperation) {
                    display = "0."
                    isNewOperation = false
                } else if (!display.contains(".")) {
                    display += "."
                }
            }
            "sin", "cos", "tan", "log", "^" -> {
                try {
                    val num = display.toDouble()
                    val res = when (value) {
                        "sin" -> sin(Math.toRadians(num))
                        "cos" -> cos(Math.toRadians(num))
                        "tan" -> tan(Math.toRadians(num))
                        "log" -> if (num > 0) log10(num) else throw IllegalArgumentException()
                        "^" -> {
                            expression = "$display ^ "
                            display = "0"
                            isNewOperation = false
                            return
                        }
                        else -> 0.0
                    }
                    display = res.toString()
                    isNewOperation = true
                } catch (_: Exception) {
                    display = "Erro"
                    isNewOperation = true
                }
            }
            else -> {
                if (isNewOperation) {
                    display = value
                    expression = ""
                    isNewOperation = false
                } else {
                    display = if (display == "0") value else display + value
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp),
        verticalArrangement = Arrangement.Bottom
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(if (isLandscape) 0.6f else 1f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = expression,
                fontSize = 18.sp,
                color = Color.Gray,
                maxLines = 1,
                modifier = Modifier.horizontalScroll(scrollState)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = display,
                fontSize = if (isLandscape) 36.sp else 48.sp,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.End,
                maxLines = 1,
                modifier = Modifier.horizontalScroll(rememberScrollState())
            )
        }

        Spacer(modifier = Modifier.height(8.dp))


        if (!isLandscape) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = { showScientific = !showScientific }) {
                    Text(
                        text = if (showScientific) "Ocultar Científica" else "🔬 Modo Científico",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
        }


        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            if (showScientific || isLandscape) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CalcButton("sin", Modifier.weight(1f), MaterialTheme.colorScheme.secondary) { onButtonClick(it) }
                        CalcButton("cos", Modifier.weight(1f), MaterialTheme.colorScheme.secondary) { onButtonClick(it) }
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        CalcButton("tan", Modifier.weight(1f), MaterialTheme.colorScheme.secondary) { onButtonClick(it) }
                        CalcButton("log", Modifier.weight(1f), MaterialTheme.colorScheme.secondary) { onButtonClick(it) }
                    }
                }
            }


            Column(
                modifier = Modifier.weight(if (showScientific || isLandscape) 2f else 1f),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val buttons = listOf(
                    listOf("C", "(", ")", "/"),
                    listOf("7", "8", "9", "*"),
                    listOf("4", "5", "6", "-"),
                    listOf("1", "2", "3", "+"),
                    listOf("0", ".", "=", "^")
                )

                buttons.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        row.forEach { text ->
                            CalcButton(
                                text = text,
                                modifier = Modifier.weight(1f),
                                containerColor = when (text) {
                                    "C" -> MaterialTheme.colorScheme.errorContainer
                                    "=", "+", "-", "*", "/" -> MaterialTheme.colorScheme.primary
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            ) {
                                onButtonClick(text)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CalcButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.surfaceVariant,
    onClick: (String) -> Unit
) {
    Button(
        onClick = { onClick(text) },
        modifier = modifier.height(if (LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE) 48.dp else 56.dp),
        shape = CircleShape,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor),
        contentPadding = PaddingValues(0.dp)
    ) {
        Text(
            text = text,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}


fun evaluateExpression(expression: String): Double {
    val tokens = expression.trim().split(" ")
    if (tokens.isEmpty()) return 0.0
    if (tokens.size == 1) return tokens[0].toDouble()

    var result = tokens[0].toDouble()
    var i = 1
    while (i < tokens.size - 1) {
        val operator = tokens[i]
        val nextVal = tokens[i + 1].toDouble()
        result = when (operator) {
            "+" -> result + nextVal
            "-" -> result - nextVal
            "*" -> result * nextVal
            "/" -> {
                if (nextVal == 0.0) throw ArithmeticException("Divisão por zero")
                result / nextVal
            }
            "^" -> result.pow(nextVal)
            else -> result
        }
        i += 2
    }
    return result
}

@Preview(showBackground = true, backgroundColor = 0xFF121212)
@Composable
fun CalculatorPreview() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CalculatorScreen()
        }
    }
}