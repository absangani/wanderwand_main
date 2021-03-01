package com.example.wanderwand.utilities.helper

import com.blongho.country_data.Country
import com.blongho.country_data.World

class FlagHelper {
    companion object {
        fun retrieveFlagDrawable(country: String) : Int {
            return World.getCountryFrom(country).flagResource
        }
    }
}