import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import kotlin.random.Random

@Composable
fun TicTacToeApp() {
    var boardSize by remember { mutableStateOf(3) }
    var currentPlayer by remember { mutableStateOf("X") }
    var board by remember { mutableStateOf(generateEmptyBoard(boardSize)) }
    var winner by remember { mutableStateOf<String?>(null) }
    var isGameStarted by remember { mutableStateOf(false) }
    var player1Name by remember { mutableStateOf(TextFieldValue("")) }
    var player2Name by remember { mutableStateOf(TextFieldValue("")) }
    var startingPlayer by remember { mutableStateOf("") }

    if (!isGameStarted) {
        // Pantalla de inicio para ingresar los nombres de los jugadores y seleccionar el tamaño del tablero
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Tic Tac Toe", fontSize = 32.sp, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.height(16.dp))

            // Campo de texto para el nombre del jugador 1
            OutlinedTextField(
                value = player1Name,
                onValueChange = { player1Name = it },
                label = { Text("Nombre del Jugador 1 (X)") } // Se asocia con "X"
            )
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de texto para el nombre del jugador 2
            OutlinedTextField(
                value = player2Name,
                onValueChange = { player2Name = it },
                label = { Text("Nombre del Jugador 2 (O)") } // Se asocia con "O"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Selector de tamaño del tablero
            BoardSizeSelector(boardSize) {
                boardSize = it
                board = generateEmptyBoard(boardSize)
                winner = null
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botón para empezar el juego
            Button(onClick = {
                if (player1Name.text.isNotEmpty() && player2Name.text.isNotEmpty()) {
                    // Selección aleatoria de quién inicia
                    startingPlayer = if (Random.nextBoolean()) "X" else "O"
                    currentPlayer = startingPlayer
                    isGameStarted = true
                }
            }) {
                Text("Iniciar Juego")
            }
        }
    } else {
        // Pantalla del juego
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Mostrar a qué jugador le toca (X o O, pero mostrando el nombre del jugador)
            val currentPlayerName = if (currentPlayer == "X") player1Name.text else player2Name.text

            Text(
                text = "Turno de: $currentPlayerName",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Tablero del Tic-Tac-Toe
            TicTacToeBoard(board, boardSize, currentPlayer) { row, col ->
                if (board[row][col] == "" && winner == null) {
                    board[row][col] = currentPlayer
                    if (checkForWin(board, currentPlayer)) {
                        winner = currentPlayer
                    } else {
                        currentPlayer = if (currentPlayer == "X") "O" else "X"
                    }
                }
            }

            // Mostrar mensaje del ganador si existe
            if (winner != null) {
                val winnerName = if (winner == "X") player1Name.text else player2Name.text

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "¡$winnerName ha ganado!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Mostrar botón de reinicio cuando hay un ganador
            if (winner != null) {
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    board = generateEmptyBoard(boardSize)
                    winner = null
                    currentPlayer = startingPlayer
                }) {
                    Text(text = "Reiniciar")
                }
                // Botón para regresar al menú principal
                Button(onClick = {
                    isGameStarted = false
                    player1Name = TextFieldValue("")
                    player2Name = TextFieldValue("")
                    board = generateEmptyBoard(boardSize)
                    winner = null
                }) {
                    Text(text = "Menú Principal")
                }
            }
        }
    }
}

@Composable
fun BoardSizeSelector(boardSize: Int, onSizeChange: (Int) -> Unit) {
    val sizes = listOf(3, 4, 5)
    Row {
        sizes.forEach { size ->
            Button(
                onClick = { onSizeChange(size) },
                colors = if (size == boardSize) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                else ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
            ) {
                Text(text = "${size}x$size")
            }
        }
    }
}

@Composable
fun TicTacToeBoard(
    board: Array<Array<String>>,
    size: Int,
    currentPlayer: String,
    onCellClick: (Int, Int) -> Unit
) {
    Column {
        for (row in 0 until size) {
            Row {
                for (col in 0 until size) {
                    TicTacToeCell(board[row][col], onClick = { onCellClick(row, col) })
                }
            }
        }
    }
}

@Composable
fun TicTacToeCell(value: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .size(80.dp)
            .padding(4.dp),
        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

fun generateEmptyBoard(size: Int): Array<Array<String>> {
    return Array(size) { Array(size) { "" } }
}

fun checkForWin(board: Array<Array<String>>, currentPlayer: String): Boolean {
    val size = board.size
    val winCondition = 3

    // Verificar filas
    for (row in 0 until size) {
        for (col in 0..size - winCondition) {
            if ((0 until winCondition).all { board[row][col + it] == currentPlayer }) {
                return true
            }
        }
    }

    // Verificar columnas
    for (col in 0 until size) {
        for (row in 0..size - winCondition) {
            if ((0 until winCondition).all { board[row + it][col] == currentPlayer }) {
                return true
            }
        }
    }

    // Verificar diagonales principales
    for (row in 0..size - winCondition) {
        for (col in 0..size - winCondition) {
            if ((0 until winCondition).all { board[row + it][col + it] == currentPlayer }) {
                return true
            }
        }
    }

    // Verificar diagonales inversas
    for (row in 0..size - winCondition) {
        for (col in winCondition - 1 until size) {
            if ((0 until winCondition).all { board[row + it][col - it] == currentPlayer }) {
                return true
            }
        }
    }

    return false
}

@Preview(showBackground = true)
@Composable
fun PreviewTicTacToeApp() {
    TicTacToeApp()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TicTacToeApp()
        }
    }
}
