package com.example.snake

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.random.Random

class SnakeGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : View(context, attrs) {

    interface GameEventListener {
        fun onScoreChanged(score: Int)
        fun onStatusChanged(status: String)
        fun onGameOver(score: Int)
    }

    private val boardSize = 20
    private val boardInset = 16f.dp
    private val cellGap = 3f.dp
    private val cornerRadius = 10f.dp
    private val overlayRadius = 26f.dp
    private val overlayTextSize = 18f.dp
    private val overlaySubTextSize = 12f.dp
    private val swipeThreshold = 24f.dp

    private val boardPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.boardBackground)
    }
    private val gridPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.boardGrid)
        strokeWidth = 1f.dp
    }
    private val snakeBodyPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.snakeBody)
    }
    private val snakeHeadPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.snakeHead)
    }
    private val foodPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.foodColor)
    }
    private val overlayPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.overlayColor)
    }
    private val overlayTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.overlayText)
        textAlign = Paint.Align.CENTER
        textSize = overlayTextSize
    }
    private val overlaySubTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context, R.color.overlayText)
        textAlign = Paint.Align.CENTER
        textSize = overlaySubTextSize
    }

    private val boardRect = RectF()
    private val random = Random(System.currentTimeMillis())
    private val snake = mutableListOf<GridPoint>()
    private var food = GridPoint(0, 0)
    private var currentDirection = Direction.RIGHT
    private var pendingDirection = Direction.RIGHT
    private var currentScore = 0
    private var gameRunning = false
    private var gameOver = false
    private var userPaused = false
    private var statusText = context.getString(R.string.status_ready)
    private var downX = 0f
    private var downY = 0f
    private var gameEventListener: GameEventListener? = null

    private val gameLoop = object : Runnable {
        override fun run() {
            if (!gameRunning || gameOver) {
                return
            }

            stepGame()
            if (gameRunning && !gameOver) {
                postDelayed(this, tickDelay())
            }
        }
    }

    init {
        isClickable = true
        isFocusable = true
        initializeGame()
    }

    fun setGameEventListener(listener: GameEventListener) {
        gameEventListener = listener
        listener.onScoreChanged(currentScore)
        listener.onStatusChanged(statusText)
    }

    fun queueDirection(direction: Direction) {
        if (gameOver) {
            return
        }

        if (direction == currentDirection.opposite() || direction == pendingDirection.opposite()) {
            return
        }

        pendingDirection = direction
        invalidate()
    }

    fun restartGame() {
        removeCallbacks(gameLoop)
        userPaused = false
        initializeGame()
        resumeGame()
    }

    fun pauseGame(fromUser: Boolean = true) {
        if (fromUser) {
            userPaused = true
        }
        if (!gameRunning) {
            if (!gameOver) {
                updateStatus(context.getString(R.string.status_paused))
            }
            return
        }

        gameRunning = false
        removeCallbacks(gameLoop)
        if (!gameOver) {
            updateStatus(context.getString(R.string.status_paused))
        }
    }

    fun resumeGame(fromUser: Boolean = true) {
        if (gameOver) {
            return
        }
        if (!fromUser && userPaused) {
            return
        }

        userPaused = false
        if (gameRunning) {
            return
        }

        gameRunning = true
        updateStatus(context.getString(R.string.status_running))
        removeCallbacks(gameLoop)
        postDelayed(gameLoop, tickDelay())
    }

    fun isRunning(): Boolean = gameRunning

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val availableWidth = width - paddingLeft - paddingRight
        val availableHeight = height - paddingTop - paddingBottom
        val boardPixels = max(0f, min(availableWidth, availableHeight).toFloat() - boardInset * 2)
        val left = paddingLeft + (availableWidth - boardPixels) / 2f
        val top = paddingTop + (availableHeight - boardPixels) / 2f
        boardRect.set(left, top, left + boardPixels, top + boardPixels)

        canvas.drawRoundRect(boardRect, overlayRadius, overlayRadius, boardPaint)

        if (boardPixels <= 0f) {
            return
        }

        val cellSize = boardPixels / boardSize
        drawGrid(canvas, cellSize)
        drawFood(canvas, cellSize)
        drawSnake(canvas, cellSize)

        if (!gameRunning || gameOver) {
            drawOverlay(canvas)
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.x
                downY = event.y
                return true
            }

            MotionEvent.ACTION_UP -> {
                val deltaX = event.x - downX
                val deltaY = event.y - downY
                if (abs(deltaX) > swipeThreshold || abs(deltaY) > swipeThreshold) {
                    if (abs(deltaX) > abs(deltaY)) {
                        queueDirection(if (deltaX > 0f) Direction.RIGHT else Direction.LEFT)
                    } else {
                        queueDirection(if (deltaY > 0f) Direction.DOWN else Direction.UP)
                    }
                }
                performClick()
                return true
            }
        }
        return super.onTouchEvent(event)
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(gameLoop)
        super.onDetachedFromWindow()
    }

    private fun initializeGame() {
        currentDirection = Direction.RIGHT
        pendingDirection = Direction.RIGHT
        currentScore = 0
        gameRunning = false
        gameOver = false

        snake.clear()
        val center = boardSize / 2
        snake += GridPoint(center, center)
        snake += GridPoint(center - 1, center)
        snake += GridPoint(center - 2, center)
        food = createFood()

        gameEventListener?.onScoreChanged(currentScore)
        updateStatus(context.getString(R.string.status_ready))
        invalidate()
    }

    private fun stepGame() {
        currentDirection = pendingDirection
        val nextHead = snake.first().move(currentDirection)
        val grows = nextHead == food
        val collisionBody = if (grows) snake else snake.dropLast(1)

        if (
            nextHead.x !in 0 until boardSize ||
            nextHead.y !in 0 until boardSize ||
            collisionBody.contains(nextHead)
        ) {
            handleGameOver()
            return
        }

        snake.add(0, nextHead)
        if (grows) {
            currentScore += 1
            gameEventListener?.onScoreChanged(currentScore)
            food = createFood()
        } else {
            snake.removeAt(snake.lastIndex)
        }
        invalidate()
    }

    private fun handleGameOver() {
        gameRunning = false
        gameOver = true
        removeCallbacks(gameLoop)
        updateStatus(context.getString(R.string.status_game_over))
        gameEventListener?.onGameOver(currentScore)
        invalidate()
    }

    private fun createFood(): GridPoint {
        if (snake.size >= boardSize * boardSize) {
            return snake.first()
        }

        var candidate: GridPoint
        do {
            candidate = GridPoint(
                x = random.nextInt(boardSize),
                y = random.nextInt(boardSize)
            )
        } while (snake.contains(candidate))
        return candidate
    }

    private fun drawGrid(canvas: Canvas, cellSize: Float) {
        for (index in 1 until boardSize) {
            val position = boardRect.left + index * cellSize
            canvas.drawLine(position, boardRect.top, position, boardRect.bottom, gridPaint)
            canvas.drawLine(boardRect.left, position, boardRect.right, position, gridPaint)
        }
    }

    private fun drawFood(canvas: Canvas, cellSize: Float) {
        val inset = cellGap * 1.5f
        val left = boardRect.left + food.x * cellSize + inset
        val top = boardRect.top + food.y * cellSize + inset
        val right = left + cellSize - inset * 2
        val bottom = top + cellSize - inset * 2
        canvas.drawOval(left, top, right, bottom, foodPaint)
    }

    private fun drawSnake(canvas: Canvas, cellSize: Float) {
        snake.forEachIndexed { index, point ->
            val inset = if (index == 0) cellGap else cellGap * 1.2f
            val paint = if (index == 0) snakeHeadPaint else snakeBodyPaint
            val left = boardRect.left + point.x * cellSize + inset
            val top = boardRect.top + point.y * cellSize + inset
            val right = left + cellSize - inset * 2
            val bottom = top + cellSize - inset * 2
            canvas.drawRoundRect(
                left,
                top,
                right,
                bottom,
                cornerRadius,
                cornerRadius,
                paint
            )
        }
    }

    private fun drawOverlay(canvas: Canvas) {
        val overlayWidth = boardRect.width() * 0.72f
        val overlayHeight = 92f.dp
        val overlayLeft = boardRect.centerX() - overlayWidth / 2f
        val overlayTop = boardRect.centerY() - overlayHeight / 2f
        val overlayBottom = overlayTop + overlayHeight

        canvas.drawRoundRect(
            overlayLeft,
            overlayTop,
            overlayLeft + overlayWidth,
            overlayBottom,
            overlayRadius,
            overlayRadius,
            overlayPaint
        )

        canvas.drawText(
            statusText,
            boardRect.centerX(),
            overlayTop + 38f.dp,
            overlayTextPaint
        )

        canvas.drawText(
            context.getString(R.string.overlay_hint),
            boardRect.centerX(),
            overlayTop + 66f.dp,
            overlaySubTextPaint
        )
    }

    private fun updateStatus(status: String) {
        statusText = status
        gameEventListener?.onStatusChanged(statusText)
    }

    private fun tickDelay(): Long {
        val accelerated = 180L - currentScore * 6L
        return accelerated.coerceAtLeast(90L)
    }

    private val Float.dp: Float
        get() = this * resources.displayMetrics.density
}

private data class GridPoint(
    val x: Int,
    val y: Int
) {
    fun move(direction: Direction): GridPoint {
        return when (direction) {
            Direction.UP -> copy(y = y - 1)
            Direction.DOWN -> copy(y = y + 1)
            Direction.LEFT -> copy(x = x - 1)
            Direction.RIGHT -> copy(x = x + 1)
        }
    }
}

enum class Direction {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    fun opposite(): Direction {
        return when (this) {
            UP -> DOWN
            DOWN -> UP
            LEFT -> RIGHT
            RIGHT -> LEFT
        }
    }
}
