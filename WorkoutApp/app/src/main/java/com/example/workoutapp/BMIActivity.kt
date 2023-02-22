package com.example.workoutapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.RecyclerView.ViewCacheExtension
import java.math.BigDecimal
import java.math.RoundingMode

class BMIActivity : AppCompatActivity() {

    val METRIC_UNITS_VIEW = "METRIC_UNIT_VIEW"
    val US_UNITS_VIEW = "US_UNIT_VIEW"
    var currentVisibleView = METRIC_UNITS_VIEW

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bmiactivity)

        val toolbarBMI = findViewById<Toolbar>(R.id.toolbar_bmi_activity)

        setSupportActionBar(toolbarBMI)
        val actionbar = supportActionBar
        if(actionbar!= null){
            actionbar.setDisplayHomeAsUpEnabled(true)
            actionbar.title = "CALCULATE BMI"
        }

        toolbarBMI.setNavigationOnClickListener{
            onBackPressed()
        }

        val btnCalculate = findViewById<Button>(R.id.btnCalculateUnits)
        val etMetricWeight = findViewById<EditText>(R.id.etMetricUnitWeight)
        val etMetricHeight = findViewById<EditText>(R.id.etMetricUnitHeight)
        val etUsUnitHeightFeet = findViewById<EditText>(R.id.etUsUnitHeightFeet)
        val etUsUnitHeightInch = findViewById<EditText>(R.id.etUsUnitHeightInch)
        val etUsUnitWeight = findViewById<EditText>(R.id.etUsUnitWeight)

        btnCalculate.setOnClickListener {
            if(currentVisibleView.equals(METRIC_UNITS_VIEW)){
                if(validateMetricUnits()){
                    val heightValue :Float = etMetricHeight.text.toString().toFloat() / 100
                    val weightValue :Float = etMetricWeight.text.toString().toFloat()

                    val bmi = weightValue / (heightValue * heightValue)
                    displayBMIResult(bmi)
                }else{
                    Toast.makeText(this@BMIActivity, "Please enter valid values.", Toast.LENGTH_SHORT).show()
                }
            }else{
                if(validateUSUnits()){
                    val usUnitHeightValueFeet: String = etUsUnitHeightFeet.text.toString()
                    val usUnitHeightValueInch: String = etUsUnitHeightInch.text.toString()
                    val usUnitWeightValue: Float = etUsUnitWeight.text.toString().toFloat()

                    val heightValue = usUnitHeightValueInch.toFloat() + usUnitHeightValueFeet.toFloat() * 12

                    val bmi = 703 * (usUnitWeightValue / (heightValue * heightValue))
                    displayBMIResult(bmi)
                }else{
                    Toast.makeText(this@BMIActivity, "Please enter valid values.", Toast.LENGTH_SHORT).show()
                }
            }
        }
        makeVisibleMetricUnitsView()
        val rgUnits = findViewById<RadioGroup>(R.id.rgUnits)
        rgUnits.setOnCheckedChangeListener { group, checkedId ->
            if(checkedId == R.id.rbMetricUnits){
                makeVisibleMetricUnitsView()
            }else{
                makeVisibleUSUnitsView()
            }
        }
    }

    private fun validateMetricUnits(): Boolean{
        var isValid = true
        val etMetricWeight = findViewById<EditText>(R.id.etMetricUnitWeight)
        val etMetricHeight = findViewById<EditText>(R.id.etMetricUnitHeight)
        if(etMetricWeight.text.toString().isEmpty())
            isValid = false
        else if(etMetricHeight.text.toString().isEmpty())
            isValid = false

        return isValid
    }

    private fun validateUSUnits(): Boolean{
        var isValid = true
        val etUSUnitHeightFeet = findViewById<EditText>(R.id.etUsUnitHeightFeet)
        val etUSUnitHeightInch = findViewById<EditText>(R.id.etUsUnitHeightInch)
        val etUSUnitWeight = findViewById<EditText>(R.id.etUsUnitWeight)
        if(etUSUnitHeightFeet.text.toString().isEmpty())
            isValid = false
        else if(etUSUnitHeightInch.text.toString().isEmpty())
            isValid = false
        else if(etUSUnitWeight.text.toString().isEmpty())
            isValid = false

        return isValid
    }

    private fun makeVisibleMetricUnitsView(){
        currentVisibleView = METRIC_UNITS_VIEW
        val etMetricWeight = findViewById<EditText>(R.id.etMetricUnitWeight)
        val etMetricHeight = findViewById<EditText>(R.id.etMetricUnitHeight)

        val etUSWeight = findViewById<EditText>(R.id.etUsUnitWeight)
        val etUSHeightFeet = findViewById<EditText>(R.id.etUsUnitHeightFeet)
        val etUSHeightInch = findViewById<EditText>(R.id.etUsUnitHeightInch)

        val llDisplayBMIResults = findViewById<LinearLayout>(R.id.llDisplayBMIResult)

        etMetricHeight.text!!.clear()
        etMetricWeight.text!!.clear()

        etMetricHeight.visibility = View.VISIBLE
        etMetricWeight.visibility = View.VISIBLE

        etUSWeight.visibility = View.GONE
        etUSHeightFeet.visibility = View.GONE
        etUSHeightInch.visibility = View.GONE

        llDisplayBMIResults.visibility = View.INVISIBLE

    }

    private fun makeVisibleUSUnitsView(){
        currentVisibleView = US_UNITS_VIEW
        val etMetricWeight = findViewById<EditText>(R.id.etMetricUnitWeight)
        val etMetricHeight = findViewById<EditText>(R.id.etMetricUnitHeight)

        val etUSWeight = findViewById<EditText>(R.id.etUsUnitWeight)
        val etUSHeightFeet = findViewById<EditText>(R.id.etUsUnitHeightFeet)
        val etUSHeightInch = findViewById<EditText>(R.id.etUsUnitHeightInch)

        val llDisplayBMIResults = findViewById<LinearLayout>(R.id.llDisplayBMIResult)

        etUSWeight.text!!.clear()
        etUSHeightFeet.text!!.clear()
        etUSHeightInch.text!!.clear()

        etMetricHeight.visibility = View.GONE
        etMetricWeight.visibility = View.GONE

        etUSWeight.visibility = View.VISIBLE
        etUSHeightFeet.visibility = View.VISIBLE
        etUSHeightInch.visibility = View.VISIBLE

        llDisplayBMIResults.visibility = View.INVISIBLE

    }

    private fun displayBMIResult(bmi: Float){
        val bmiLabel :String
        val bmiDescription :String

        if (bmi.compareTo(15f) <= 0) {
            bmiLabel = "Very severely underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(15f) > 0 && bmi.compareTo(16f) <= 0
        ) {
            bmiLabel = "Severely underweight"
            bmiDescription = "Oops!You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(16f) > 0 && bmi.compareTo(18.5f) <= 0
        ) {
            bmiLabel = "Underweight"
            bmiDescription = "Oops! You really need to take better care of yourself! Eat more!"
        } else if (bmi.compareTo(18.5f) > 0 && bmi.compareTo(25f) <= 0
        ) {
            bmiLabel = "Normal"
            bmiDescription = "Congratulations! You are in a good shape!"
        } else if (java.lang.Float.compare(bmi, 25f) > 0 && java.lang.Float.compare(
                bmi,
                30f
            ) <= 0
        ) {
            bmiLabel = "Overweight"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(30f) > 0 && bmi.compareTo(35f) <= 0
        ) {
            bmiLabel = "Obese Class | (Moderately obese)"
            bmiDescription = "Oops! You really need to take care of your yourself! Workout maybe!"
        } else if (bmi.compareTo(35f) > 0 && bmi.compareTo(40f) <= 0
        ) {
            bmiLabel = "Obese Class || (Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        } else {
            bmiLabel = "Obese Class ||| (Very Severely obese)"
            bmiDescription = "OMG! You are in a very dangerous condition! Act now!"
        }

        val tvYourBMI = findViewById<TextView>(R.id.tvYourBMI)
        val tvBMIValue = findViewById<TextView>(R.id.tvBMIValue)
        val tvBMIType = findViewById<TextView>(R.id.tvBMIType)
        val tvBMIDescription = findViewById<TextView>(R.id.tvBMIDescription)

        val llDisplayBMIResults = findViewById<LinearLayout>(R.id.llDisplayBMIResult)
        llDisplayBMIResults.visibility = View.VISIBLE

        // This is used to round the result value to 2 decimal values after "."
        val bmiValue = BigDecimal(bmi.toDouble()).setScale(2, RoundingMode.HALF_EVEN).toString()

        tvBMIValue.text = bmiValue // Value is set to TextView
        tvBMIType.text = bmiLabel // Label is set to TextView
        tvBMIDescription.text = bmiDescription // Description is set to TextView

    }
}