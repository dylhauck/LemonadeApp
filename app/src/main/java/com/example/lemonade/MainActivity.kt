/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.lemonade

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    /**
     * DO NOT ALTER ANY VARIABLE OR VALUE NAMES OR THEIR INITIAL VALUES.
     *
     * Anything labeled var instead of val is expected to be changed in the functions but DO NOT
     * alter their initial values declared here, this could cause the app to not function properly.
     */
    private val LEMONADE_STATE = "LEMONADE_STATE"
    private val LEMON_SIZE = "LEMON_SIZE"
    private val SQUEEZE_COUNT = "SQUEEZE_COUNT"
    // SELECT represents the "pick lemon" state
    private val SELECT = "select"
    // SQUEEZE represents the "squeeze lemon" state
    private val SQUEEZE = "squeeze"
    // DRINK represents the "drink lemonade" state
    private val DRINK = "drink"
    // RESTART represents the state where the lemonade has been drunk and the glass is empty
    private val RESTART = "restart"
    // Default the state to select
    private var lemonadeState = "select"
    // Default lemonSize to -1
    private var lemonSize = -1
    // Default the squeezeCount to -1
    private var squeezeCount = -1

    private var lemonTree = LemonTree()
    private var lemonImage: ImageView? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // === DO NOT ALTER THE CODE IN THE FOLLOWING IF STATEMENT ===
        if (savedInstanceState != null) {
            lemonadeState = savedInstanceState.getString(LEMONADE_STATE, "select")
            lemonSize = savedInstanceState.getInt(LEMON_SIZE, -1)
            squeezeCount = savedInstanceState.getInt(SQUEEZE_COUNT, -1)
        }
        // === END IF STATEMENT ===

        lemonImage = findViewById(R.id.image_lemon_state)
        setViewElements()
        lemonImage!!.setOnClickListener {
            // Allows user to switch the state of the image to a lemon once clicked.
            clickLemonImage()
        }
        lemonImage!!.setOnLongClickListener {
            // Holding down the image for a period of time will show the user their
            // squeeze count.
            showSnackbar()
        }
    }

    /**
     * === DO NOT ALTER THIS METHOD ===
     *
     * This method saves the state of the app if it is put in the background.
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(LEMONADE_STATE, lemonadeState)
        outState.putInt(LEMON_SIZE, lemonSize)
        outState.putInt(SQUEEZE_COUNT, squeezeCount)
        super.onSaveInstanceState(outState)
    }

    /**
     * Clicking will elicit a different response depending on the state.
     * This method determines the state and proceeds with the correct action.
     */
    private fun clickLemonImage() {
        // When the image is clicked, these set the lemon state.
        when (lemonadeState) {
            // If the state is select, then select to choose a lemon off of the tree.
            SELECT -> {
                // Switches the state from select to squeeze to juice the lemon.
                lemonadeState = SQUEEZE
                // Determines the size of the tree and lemon.
                val tree: LemonTree = lemonTree
                lemonSize = tree.pick()
                // There is no squeeze count yet, so this sets the count to 0 until its
                // time to squeeze.
                squeezeCount = 0
            }
            // The state is set to squeeze, so click to squeeze to juice the lemon.
            SQUEEZE -> {
                // Squeeze count increases by increments of 1.
                squeezeCount += 1
                // The size of the lemon decreases as well with each squeeze.
                lemonSize -=1

                // Once the lemon is completely squeezed, the size is set to 0 and the
                // state is switched to drink.
                if (lemonSize == 0) {
                    lemonadeState = DRINK
                }
                // If there is still juices left and the lemon isn't done yet, keep
                // squeezing!
                else SQUEEZE
            }
            // Sets the state to drink to click and finish the lemonade.
            DRINK -> {
                // When the lemonade is finished, this empties the cup and asks to restart.
                lemonadeState = RESTART
                // There is no lemon or lemonade left.
                lemonSize = -1
            }
            // Once the user clicks to restart, it has brings the tree back to choose
            // a lemon again.
            RESTART -> lemonadeState = SELECT
        }
        // Views and images are set and chosen accordingly.
        setViewElements()
    }

    /**
     * Set up the view elements according to the state.
     */
    private fun setViewElements() {
        val textAction: TextView = findViewById(R.id.text_action)

        when (lemonadeState) {
            // When the state is select, it will print a message for the user to select a
            // lemon, and it will present a lemon tree image.
            SELECT -> {
                textAction.text = "Click to select a lemon!"
                lemonImage!!.setImageResource(R.drawable.lemon_tree)
            }
            // When the state is squeeze, it will print a message for the user to juice their
            // lemon, and it will present a lemon image.
            SQUEEZE -> {
                textAction.text = "Click to juice the lemon!"
                lemonImage!!.setImageResource(R.drawable.lemon_squeeze)
            }
            // When the state is drink, it will print a message for the user to drink their
            // lemonade, and it will present an image of a full glass of lemonade.
            DRINK -> {
                textAction.text = "Click to drink your lemonade!"
                lemonImage!!.setImageResource(R.drawable.lemon_drink)
            }
            // When the state is restart, it will print a message for the user to restart their
            // process, and it will present an image of an empty glass.
            RESTART -> {
                textAction.text = "Click to restart and try again!"
                lemonImage!!.setImageResource(R.drawable.lemon_restart)
            }
        }
    }

    /**
     * === DO NOT ALTER THIS METHOD ===
     *
     * Long clicking the lemon image will show how many times the lemon has been squeezed.
     */
    private fun showSnackbar(): Boolean {
        if (lemonadeState != SQUEEZE) {
            return false
        }
        val squeezeText = getString(R.string.squeeze_count, squeezeCount)
        Snackbar.make(
            findViewById(R.id.constraint_Layout),
            squeezeText,
            Snackbar.LENGTH_SHORT
        ).show()
        return true
    }
}

/**
 * A Lemon tree class with a method to "pick" a lemon. The "size" of the lemon is randomized
 * and determines how many times a lemon needs to be squeezed before you get lemonade.
 */
class LemonTree {
    fun pick(): Int {
        return (2..4).random()
    }
}
