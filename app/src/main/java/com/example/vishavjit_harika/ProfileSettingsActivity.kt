/**
 * Resources used:
 * 1. For creating menu items: https://developer.android.com/develop/ui/views/components/menus
 * 2. For accessing camera and saving the profile photo: CameraDemoKotlin provided by the professor {https://canvas.sfu.ca/courses/80625/pages/the-phone-camera-and-data-storage} and {https://canvas.sfu.ca/courses/80625/files/22243351?wrap=1}
 * 3. For saving data: https://developer.android.com/training/data-storage/shared-preferences
 * 4. For saving data using shared preferences: https://www.geeksforgeeks.org/shared-preferences-in-android-with-examples/
 * 5. For implementing form validation: https://www.geeksforgeeks.org/implement-form-validation-error-to-edittext-in-android/
 * 6. For implementing email validation: https://www.geeksforgeeks.org/implement-email-validator-in-android/
 * 7. For basic queries and debugging: ChatGPT (Version 3.5) {https://chat.openai.com/auth/login}
 * 8. Limit text length of EditText: https://stackoverflow.com/questions/3285412/whats-the-best-way-to-limit-text-length-of-edittext-in-android
 */

package com.example.vishavjit_harika

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Patterns
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import java.io.File

/**
 * For choosing photo from gallery: https://www.youtube.com/watch?v=poAUbNY2dEs
 */

@Suppress("NAME_SHADOWING")
class ProfileSettingsActivity : AppCompatActivity() {
    // Profile photo
    private lateinit var profilePhotoImageView: ImageView
    private lateinit var changePhotoButton: Button
    private lateinit var galleryButton: Button
    private lateinit var currentPhotoUri: Uri
    private lateinit var myViewModel: MyViewModel
    private lateinit var cameraResult: ActivityResultLauncher<Intent>

    private val tempImgFileName = "profile_photo.jpg"

    // Input
    private lateinit var editTextArray: Array<EditText>
    private lateinit var radioGroup: RadioGroup
    private lateinit var saveButton: Button
    private lateinit var cancelButton: Button
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_settings)

        // Profile photo
        profilePhotoImageView = findViewById(R.id.profilePhoto_imageView)
        changePhotoButton = findViewById(R.id.changePhoto_Button)
        galleryButton = findViewById(R.id.gallery_button)

        // Text input
        // Initialize EditText fields
        editTextArray = arrayOf(
            findViewById(R.id.name_editTextText),
            findViewById(R.id.email_editTextTextEmailAddress),
            findViewById(R.id.editTextPhone),
            findViewById(R.id.class_editTextNumber),
            findViewById(R.id.major_editTextText)
        )

        radioGroup = findViewById(R.id.gender_radioGroup)
        saveButton = findViewById(R.id.save_button)
        cancelButton = findViewById(R.id.cancel_button)
        sharedPreferences = getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        // Load saved text for each editText
        for (i in editTextArray.indices) {
            val savedText = sharedPreferences.getString("userText$i", "")
            editTextArray[i].setText(savedText)
        }

        // Load saved radio button selection
        val savedRadioSelection = sharedPreferences.getInt("radioSelection", -1)
        if (savedRadioSelection != -1) {
            radioGroup.check(savedRadioSelection)
        }

        saveButton.setOnClickListener {
            // Save text from each EditText
            val editor = sharedPreferences.edit()
            for (i in editTextArray.indices) {
                val inputText = editTextArray[i].text.toString()
                editor.putString("userText$i", inputText)
            }

            // Save radio button selection
            val selectedRadioButtonId = radioGroup.checkedRadioButtonId
            editor.putInt("radioSelection", selectedRadioButtonId)

            editor.apply()

            if (checkInput()) {
                // Show a toast message
                Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show()

                val intent: Intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }

        cancelButton.setOnClickListener {
            // Reset input fields to saved data
            for (i in editTextArray.indices) {
                val savedText = sharedPreferences.getString("userText$i", "")
                editTextArray[i].setText(savedText)
            }
            val savedRadioSelection = sharedPreferences.getInt("radioSelection", -1)
            if (savedRadioSelection != -1) {
                radioGroup.check(savedRadioSelection)
            }

            finish()
        }

        // Ask for permission to access camera
        Util.checkPermissions(this)

        // Profile Photo
        val tempImgFile = File(getExternalFilesDir(null), tempImgFileName)
        currentPhotoUri =
            FileProvider.getUriForFile(this, "com.example.vishavjit_harika", tempImgFile)

        val galleryImage = registerForActivityResult(ActivityResultContracts.GetContent(),
            ActivityResultCallback {
                profilePhotoImageView.setImageURI(it)
            })

        galleryButton.setOnClickListener {
            galleryImage.launch("image/*")
        }

        changePhotoButton.setOnClickListener {
//            val myDialog = MyDialog()
//            val bundle = Bundle()
//            bundle.putInt(MyDialog.DIALOG_KEY, MyDialog.PICK_PICTURE_DIALOG)
//            myDialog.arguments = bundle
//            myDialog.show(supportFragmentManager, "pick picture dialog")
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, currentPhotoUri)
            cameraResult.launch(intent)
        }

        cameraResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult())
        { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = Util.getBitmap(this, currentPhotoUri)
                myViewModel.userImage.value = bitmap
            }
        }

        myViewModel = ViewModelProvider(this)[MyViewModel::class.java]
        myViewModel.userImage.observe(this) {
            profilePhotoImageView.setImageBitmap(it)
        }

        if (tempImgFile.exists()) {
            val bitmap = Util.getBitmap(this, currentPhotoUri)
            profilePhotoImageView.setImageBitmap(bitmap)
        }
    }

    override fun onPause() {
        super.onPause()

        // Save data when the app goes into background
        val editor = sharedPreferences.edit()
        for (i in editTextArray.indices) {
            val inputText = editTextArray[i].text.toString()
            editor.putString("userText$i", inputText)
        }

        val selectedRadioButtonId = radioGroup.checkedRadioButtonId
        editor.putInt("radioSelection", selectedRadioButtonId)

        editor.apply()
    }

    private fun checkInput(): Boolean {
        if (editTextArray[0].length() == 0) {
            editTextArray[0].error = "Name is required"
            return false
        }
        if (editTextArray[1].length() == 0) {
            editTextArray[1].error = "Email is required"
            return false
        }
        if (!checkEmail()){
            editTextArray[1].error = "Email format incorrect"
            return false
        }
        if (editTextArray[2].length() == 0) {
            editTextArray[2].error = "Phone Number is required"
            return false
        }
        if (editTextArray[3].length() == 0) {
            editTextArray[3].error = "Class is required"
            return false
        }
        if (editTextArray[4].length() == 0) {
            editTextArray[4].error = "Major is required"
            return false
        }

        return true
    }

    private fun checkEmail(): Boolean {
        val emailToString = editTextArray[1].text.toString()
        return Patterns.EMAIL_ADDRESS.matcher(emailToString).matches()
    }
}
