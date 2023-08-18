package com.simoni.name.mastermind.model.utils

data class Attempt(
    val guess : String,
    val rightNumRightPos : Int,
    val rightNumWrongPos: Int
)