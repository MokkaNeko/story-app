package com.dicodingsub.storyapp.ui.upload

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicodingsub.storyapp.R
import com.dicodingsub.storyapp.databinding.ActivityUploadBinding
import com.dicodingsub.storyapp.di.ViewModelFactory
import com.dicodingsub.storyapp.di.getImageUri
import com.dicodingsub.storyapp.di.reduceFileImage
import com.dicodingsub.storyapp.di.uriToFile
import com.dicodingsub.storyapp.ui.main.MainActivity

@Suppress("DEPRECATION")
class UploadActivity : AppCompatActivity() {
    private val uploadViewModel by viewModels<UploadViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityUploadBinding
    private var currentImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupListeners()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setupView() {
        supportActionBar?.apply {
            title = getString(R.string.title_upload_story)
            setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupListeners() {
        binding.galleryButton.setOnClickListener { startGallery() }
        binding.cameraButton.setOnClickListener { startCamera() }
        binding.uploadButton.setOnClickListener { uploadImage() }
    }

    private fun showImage() {
        currentImageUri?.let {
            Log.d("Image URI", "showImage: $it")
            binding.previewImageView.setImageURI(it)
        }
    }

    private val launcherGallery = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        if (uri != null) {
            currentImageUri = uri
            showImage()
        } else {
            Log.d("Photo Picker", "No media selected")
        }
    }

    private fun startGallery() {
        launcherGallery.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { isSuccess ->
        if (isSuccess) {
            showImage()
        }
    }

    private fun startCamera() {
        currentImageUri = getImageUri(this)
        launcherIntentCamera.launch(currentImageUri!!)
    }

    private fun uploadImage() {
        // Show loading spinner
        showLoading(true)

        // Fetch user token and proceed with upload
        uploadViewModel.getUser().observe(this) { user ->
            if (user != null) {
                val token = "Bearer " + user.token
                performUpload(token)
            } else {
                showLoading(false)
                showToast(getString(R.string.user_not_found))
            }
        }
        observeUploadResponse()
    }

    private fun performUpload(token: String) {
        currentImageUri?.let { uri ->
            val imageFile = uriToFile(uri, this).reduceFileImage()
            Log.d("Image File", "showImage: ${imageFile.path}")
            val description = binding.descriptionEditText.text.toString()

            uploadViewModel.uploadStory(token, imageFile, description)
        } ?: run {
            showLoading(false)
            showToast(getString(R.string.empty_image_warning))
        }
    }

    private fun observeUploadResponse() {
        uploadViewModel.uploadResponse.observe(this@UploadActivity) { response ->
            // Hide loading spinner
            showLoading(false)

            // Handle the upload response safely
            if (response != null) {
                if (response.error == false) {
                    // Handle successful upload
                    showToast(getString(R.string.upload_success))
                    val intent = Intent(this, MainActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    finish()
                } else {
                    // Handle upload failure
                    showToast(response.message ?: getString(R.string.upload_failed))
                }
            } else {
                // Handle the case where response is null
                showToast(getString(R.string.upload_failed))
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}