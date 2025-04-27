package com.arorashivoy.wifistrength

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class Screen1Activity : AppCompatActivity() {
    private external fun matrixOperation(op: String, dim: IntArray, matrixA: FloatArray, matrixB: FloatArray): String

    private lateinit var gridMatrixA: GridLayout
    private lateinit var gridMatrixB: GridLayout
    private lateinit var enterMatrixA: TextView
    private lateinit var enterMatrixB: TextView
    private lateinit var rowsAInput: EditText
    private lateinit var colsAInput: EditText
    private lateinit var rowsBInput: EditText
    private lateinit var colsBInput: EditText
    private lateinit var resultTextView: TextView
    private lateinit var radioGroup: RadioGroup

    companion object {
        init {
            System.loadLibrary("wifistrength")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_screen1)

        gridMatrixA = findViewById(R.id.gridMatrixA)
        gridMatrixB = findViewById(R.id.gridMatrixB)
        enterMatrixA = findViewById(R.id.enterMatrixA)
        enterMatrixB = findViewById(R.id.enterMatrixB)
        rowsAInput = findViewById(R.id.rowsA)
        colsAInput = findViewById(R.id.colsA)
        rowsBInput = findViewById(R.id.rowsB)
        colsBInput = findViewById(R.id.colsB)
        resultTextView = findViewById(R.id.tvResult)
        radioGroup = findViewById(R.id.radioGroupOperations)

        findViewById<Button>(R.id.btnEnterMatrixA).setOnClickListener {
            val rows = rowsAInput.text.toString().toIntOrNull() ?: return@setOnClickListener
            val cols = colsAInput.text.toString().toIntOrNull() ?: return@setOnClickListener
            createGrid(gridMatrixA, rows, cols)
            rowsAInput.visibility = View.GONE
            colsAInput.visibility = View.GONE
            findViewById<Button>(R.id.btnEnterMatrixA).visibility = View.GONE
            enterMatrixA.visibility = View.VISIBLE
            gridMatrixA.visibility = View.VISIBLE
        }

        findViewById<Button>(R.id.btnEnterMatrixB).setOnClickListener {
            val rows = rowsBInput.text.toString().toIntOrNull() ?: return@setOnClickListener
            val cols = colsBInput.text.toString().toIntOrNull() ?: return@setOnClickListener
            createGrid(gridMatrixB, rows, cols)
            rowsBInput.visibility = View.GONE
            colsBInput.visibility = View.GONE
            findViewById<Button>(R.id.btnEnterMatrixB).visibility = View.GONE
            enterMatrixB.visibility = View.VISIBLE
            gridMatrixB.visibility = View.VISIBLE
        }

        radioGroup.setOnCheckedChangeListener { _, _ ->
            performMatrixOperation()
        }


    }

    private fun createGrid(gridLayout: GridLayout, rows: Int, cols: Int) {
        gridLayout.removeAllViews()
        gridLayout.columnCount = cols
        for (i in 0 until rows * cols) {
            val editText = EditText(this)
            editText.inputType = InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
            editText.hint = "0"
            editText.layoutParams = GridLayout.LayoutParams().apply {
                width = 200
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(8, 8, 8, 8)
            }
            gridLayout.addView(editText)
        }
    }

    private fun getMatrixFromGrid(gridLayout: GridLayout, rows: Int, cols: Int): FloatArray {
        val matrix = FloatArray(rows * cols)
        for (i in 0 until rows * cols) {
            val editText = gridLayout.getChildAt(i) as EditText
            matrix[i] = editText.text.toString().toFloatOrNull() ?: 0f
        }
        return matrix
    }

    private fun performMatrixOperation() {
        val rowsA = rowsAInput.text.toString().toIntOrNull() ?: return
        val colsA = colsAInput.text.toString().toIntOrNull() ?: return
        val rowsB = rowsBInput.text.toString().toIntOrNull() ?: return
        val colsB = colsBInput.text.toString().toIntOrNull() ?: return

        val matrixA = getMatrixFromGrid(gridMatrixA, rowsA, colsA)
        val matrixB = getMatrixFromGrid(gridMatrixB, rowsB, colsB)

        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        val operation = when (selectedRadioButtonId) {
            R.id.radioAdd -> "add"
            R.id.radioSubtract -> "subtract"
            R.id.radioMultiply -> "multiply"
            R.id.radioDivide -> "divide"
            else -> return
        }

        val dimensions = intArrayOf(rowsA, colsA, rowsB, colsB)
        val result = matrixOperation(operation, dimensions, matrixA, matrixB)

        resultTextView.text = "Result of $operation: \n$result"
        Log.d("SHIVOY", "Result of $operation: \n$result")
    }

}