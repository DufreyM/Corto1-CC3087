import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun TicTacToeApp() {
    var boardSize by remember { mutableStateOf(3) }
    var currentPlayer by remember { mutableStateOf("X") }
    var board by remember { mutableStateOf(generateEmptyBoard(boardSize)) }
    var winner by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Selector de tamaño del tablero
        BoardSizeSelector(boardSize) {
            boardSize = it
            board = generateEmptyBoard(boardSize)
            winner = null // Reiniciar el ganador cuando se cambia el tamaño del tablero
            currentPlayer = "X" // Reiniciar el jugador actual
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Tablero del Tic-Tac-Toe
        TicTacToeBoard(board, boardSize, currentPlayer) { row, col ->
            if (board[row][col] == "" && winner == null) {
                board[row][col] = currentPlayer
                if (checkForWin(board, currentPlayer)) {
                    winner = currentPlayer // Actualiza el ganador
                } else {
                    currentPlayer = if (currentPlayer == "X") "O" else "X"
                }
            }
        }

        // Mostrar mensaje del ganador si existe
        if (winner != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "¡Jugador $winner ha ganado!",
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
                currentPlayer = "X"
            }) {
                Text(text = "Reiniciar")
            }
        }
    }
}


// Composable para seleccionar el tamaño del tablero
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

// Composable para el tablero
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


// Composable para las celdas del tablero
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

// Función para generar un tablero vacío
fun generateEmptyBoard(size: Int): Array<Array<String>> {
    return Array(size) { Array(size) { "" } }
}

// Lógica para verificar si hay un ganador con 3 en línea
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