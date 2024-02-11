/**
 * Resource used:
 * CameraDemoKotlin provided by the professor {https://canvas.sfu.ca/courses/80625/files/22243351?wrap=1}
 */

package com.example.vishavjit_harika

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MyViewModel: ViewModel() {
    val userImage = MutableLiveData<Bitmap>()
}