package com.example.calchue

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

import android.graphics.Color // for the color wheel
import android.widget.EditText
import android.view.View
import android.text.Editable
import android.text.TextWatcher
import top.defaults.colorpicker.ColorPickerView

import android.widget.ImageButton // for color harmonies
import android.widget.Toast
import android.graphics.drawable.ColorDrawable
import android.graphics.PorterDuff
import android.widget.ImageView
import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator

import android.widget.Button // for the popup
import android.widget.PopupWindow
import android.view.LayoutInflater
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import android.view.animation.AlphaAnimation

class MainActivity : AppCompatActivity() {
    private lateinit var popupWindow: PopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        // for the color picker elements
        triggerColorPicker()

        // for the information popup button
        triggerInfoButton()

        // for the ImageButtons (color harmonies and get color scheme buttons)
        triggerImageButtons()
    }

    // function to get and display color from color wheel or user input with the help of
    // duanhong169's color picker android library on github
    private fun triggerColorPicker() {
        val colorPickerView = findViewById<ColorPickerView>(R.id.color_picker)
        val editText = findViewById<EditText>(R.id.edit_text)
        val colorDisplay = findViewById<View>(R.id.color_display)

        editText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val colorHex = s.toString()
                if (colorHex.length == 6) {
                    val color = Color.parseColor("#$colorHex")
                    colorPickerView.setInitialColor(color)
                    colorDisplay.setBackgroundColor(color)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
        })

        colorPickerView.subscribe { color, fromUser, isFromTouch ->
            if (fromUser && isFromTouch) {
                colorDisplay.setBackgroundColor(color)
                ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                    val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                    v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                    insets
                }
            }
        }
    }

    // function to show popup when button is clicked
    private fun triggerInfoButton() {
        val button = findViewById<Button>(R.id.info_btn)
        button.setOnClickListener {
            showPopup()
        }
    }

    private var selectedHarmony: String? = null

    // function for the 4 other buttons
    private fun triggerImageButtons() {
        val analogousButton = findViewById<ImageButton>(R.id.analogous_btn)
        val triadicButton = findViewById<ImageButton>(R.id.triadic_btn)
        val splitCompButton = findViewById<ImageButton>(R.id.splitcomp_btn)
        val getColorButton = findViewById<ImageButton>(R.id.get_color_btn)

        // for the color harmony buttons
        analogousButton.setOnClickListener {
            resetButtonTransparency(analogousButton, triadicButton, splitCompButton, getColorButton)
            selectedHarmony = "analogous"
            Toast.makeText(this@MainActivity, "Analogous Harmony Selected", Toast.LENGTH_SHORT).show()
            analogousButton.alpha = 0.2f // lowers opacity of button when selected
        }

        triadicButton.setOnClickListener {
            resetButtonTransparency(analogousButton, triadicButton, splitCompButton, getColorButton)
            selectedHarmony = "triadic"
            Toast.makeText(this@MainActivity, "Triadic Harmony Selected", Toast.LENGTH_SHORT).show()
            triadicButton.alpha = 0.2f
        }

        splitCompButton.setOnClickListener {
            resetButtonTransparency(analogousButton, triadicButton, splitCompButton, getColorButton)
            selectedHarmony = "split_complementary"
            Toast.makeText(this@MainActivity, "Split Complementary Harmony Selected", Toast.LENGTH_SHORT).show()
            splitCompButton.alpha = 0.2f
        }

        getColorButton.setOnClickListener {
            if (selectedHarmony != null) {
                // gets the current color displayed
                val colorDisplay = findViewById<View>(R.id.color_display)
                val color = (colorDisplay.background as ColorDrawable).color

                // calls function to calculate color harmonies and update palette view based on the selected harmony
                updatePaletteView(color, selectedHarmony!!)
            } else {
                Toast.makeText(this@MainActivity, "Please select a color harmony first!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // function to reset the transparency of deselected button
    private fun resetButtonTransparency(vararg buttons: ImageButton) {
        buttons.forEach { it.alpha = 1f } // makes button 100% transparent again
    }

    // function for updating palette colors (and calculating them)
    private fun updatePaletteView(color: Int, harmony: String) {
        // converts RGB to HSL
        val hsl = FloatArray(3)
        Color.colorToHSV(color, hsl)

        // calculates color harmonies
        val color1: Int
        val color2: Int
        when (harmony) {
            "analogous" -> {
                color1 = Color.HSVToColor(floatArrayOf((hsl[0] + 30f) % 360, hsl[1], hsl[2]))
                color2 = Color.HSVToColor(floatArrayOf((hsl[0] - 30f + 360f) % 360, hsl[1], hsl[2]))
            }
            "triadic" -> {
                color1 = Color.HSVToColor(floatArrayOf((hsl[0] + 120f) % 360, hsl[1], hsl[2]))
                color2 = Color.HSVToColor(floatArrayOf((hsl[0] - 120f + 360f) % 360, hsl[1], hsl[2]))
            }
            "split_complementary" -> {
                color1 = Color.HSVToColor(floatArrayOf((hsl[0] + 150f) % 360, hsl[1], hsl[2]))
                color2 = Color.HSVToColor(floatArrayOf((hsl[0] - 150f + 360f) % 360, hsl[1], hsl[2]))
            }
            else -> return
        }

        // updates palette hex codes
        findViewById<TextView>(R.id.left_hexcode).text = String.format("#%06X", (0xFFFFFF and color1))
        findViewById<TextView>(R.id.center_hexcode).text = String.format("#%06X", (0xFFFFFF and color))
        findViewById<TextView>(R.id.right_hexcode).text = String.format("#%06X", (0xFFFFFF and color2))

        // updates palette views to show color
        val leftImageView = findViewById<ImageView>(R.id.left_colorpalette)
        val centerImageView = findViewById<ImageView>(R.id.center_colorpalette)
        val rightImageView = findViewById<ImageView>(R.id.right_colorpalette)

        // calls function tp animate palette change
        animateColorChange(leftImageView, color1)
        animateColorChange(centerImageView, color)
        animateColorChange(rightImageView, color2)
    }

    // function for animation of when color palette is changed
    private fun animateColorChange(view: ImageView, newColor: Int) {
        val colorDrawable = ColorDrawable()
        val colorAnimation = ObjectAnimator.ofObject(colorDrawable, "color", ArgbEvaluator(), Color.TRANSPARENT, newColor)
        colorAnimation.duration = 270 // in ms
        colorAnimation.addUpdateListener { animator ->
            view.background.setColorFilter((animator.animatedValue as Int), PorterDuff.Mode.SRC_IN)
        }
        colorAnimation.start()
    }

    // function for popup
    private fun showPopup() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.popup, null)

        val closeButton = popupView.findViewById<TextView>(R.id.close_popup)
        closeButton.setOnClickListener {
            // adds fade-out animation when popup is closed
            val fadeOut = AlphaAnimation(1.0f, 0.0f)
            fadeOut.duration = 500
            popupView.startAnimation(fadeOut)

            popupWindow.dismiss()
        }

        popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )

        popupWindow.isFocusable = true

        // measures the view so that height and width can be used for calculations
        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)

        // gets the width and height of the window
        val width = popupView.measuredWidth
        val height = popupView.measuredHeight

        // calculates the location to put the popup window
        val xPos = (resources.displayMetrics.widthPixels - width) / 2
        val yPos = (resources.displayMetrics.heightPixels - height) / 2

        // shows popup window at the calculated location
        popupWindow.showAtLocation(
            findViewById(android.R.id.content),
            Gravity.NO_GRAVITY,
            xPos,
            yPos
        )

        // adds a fade-in animation when the popup is shown
        val fadeIn = AlphaAnimation(0.0f, 1.0f)
        fadeIn.duration = 500
        popupView.startAnimation(fadeIn)
    }
}



