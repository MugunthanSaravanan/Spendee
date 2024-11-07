package com.example.spendee

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.spendee.feature_expenses.data.data_source.ExpenseDao
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch
import androidx.room.Room
import com.example.spendee.core.data.db.SpendeeDatabase

class PieChartActivity : AppCompatActivity() {

    private lateinit var expenseDatabase: SpendeeDatabase
    private lateinit var pieChart: PieChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pie_chart)

        // Initialize Room Database
        expenseDatabase = Room.databaseBuilder(
            applicationContext,
            SpendeeDatabase::class.java,
            "spendee_database"
        ).build()

        pieChart = findViewById(R.id.pieChart)  // Assuming you have a PieChart view in the layout

        // Fetch expenses and display pie chart
        lifecycleScope.launch {
            val totalExpenses = expenseDatabase.expenseDao().getTotalExpenses()
            Log.d("PieChart", "Total Expenses: $totalExpenses")  // Debug line
            setupPieChart(totalExpenses)
        }

    }

    private fun setupPieChart(totalExpenses: Float) {
        // Check if totalExpenses is greater than 0
        if (totalExpenses > 0) {
            val pieEntries = ArrayList<PieEntry>()
            pieEntries.add(PieEntry(totalExpenses, "Total Expenses"))

            val pieDataSet = PieDataSet(pieEntries, "Expenses")
            pieDataSet.colors = listOf(ColorTemplate.MATERIAL_COLORS[0])

            val pieData = PieData(pieDataSet)
            pieChart.data = pieData

            pieChart.setUsePercentValues(true)
            pieChart.description.isEnabled = false
            pieChart.setExtraOffsets(5f, 10f, 5f, 5f)
            pieChart.isRotationEnabled = true
            pieChart.setCenterText("Total Expenses")
            pieChart.setCenterTextSize(20f)

            // Refresh the chart
            pieChart.invalidate()
        } else {
            // Handle the case when there are no expenses to display
            pieChart.setCenterText("No Expenses Available")
            pieChart.invalidate()
        }
    }
}
