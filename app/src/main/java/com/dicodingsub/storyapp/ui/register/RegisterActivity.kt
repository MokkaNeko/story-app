package com.dicodingsub.storyapp.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.dicodingsub.storyapp.databinding.ActivityRegisterBinding
import com.dicodingsub.storyapp.di.ViewModelFactory
import com.dicodingsub.storyapp.ui.login.LoginActivity

class RegisterActivity : AppCompatActivity() {
    private val registerViewModel by viewModels<RegisterViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityRegisterBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    private fun setupAction() {
        binding.registerButton.setOnClickListener {
            showLoading()
            val name = binding.registerNameEditText.text.toString()
            val email = binding.registerEmailEditText.text.toString()
            val password = binding.registerPasswordEditText.text.toString()
            registerViewModel.register(name, email, password)
            registerViewModel.toastText.observe(this@RegisterActivity) {
                it.getContentIfNotHandled()?.let { toastText ->
                    Toast.makeText(
                        this@RegisterActivity, toastText, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.tvRegisterToLogin.setOnClickListener {
            val intent = Intent (this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        registerViewModel.registerResponse.observe(this@RegisterActivity) { response ->
            if (response.error == false) {
                AlertDialog.Builder(this).apply {
                    setTitle("Success!")
                    setMessage("Congratulations, your account has been successfully created")
                    setPositiveButton("Continue") { _, _ ->
                        val intent = Intent(this@RegisterActivity, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        try {
                            onDestroy()
                        } catch (e: Exception) {
                            finish()
                        }
                    }
                    create()
                    show()
                }
            }
        }
    }

    private fun playAnimation() {
        val image = ObjectAnimator.ofFloat(binding.ivRegisterImage, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(binding.tvRegisterTitle, View.ALPHA, 1f).setDuration(500)
        val nameT = ObjectAnimator.ofFloat(binding.tvRegisterTextName, View.ALPHA,1f).setDuration(300)
        val nameI = ObjectAnimator.ofFloat(binding.RegisterNameEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val emailT = ObjectAnimator.ofFloat(binding.tvRegisterTextEmail, View.ALPHA,1f).setDuration(300)
        val emailI = ObjectAnimator.ofFloat(binding.registerEmailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val passwordT = ObjectAnimator.ofFloat(binding.tvRegisterTextPassword, View.ALPHA, 1f).setDuration(500)
        val passwordI = ObjectAnimator.ofFloat(binding.registerPasswordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(300)
        val toRegister = ObjectAnimator.ofFloat(binding.tvRegisterToLoginText, View.ALPHA, 1f).setDuration(300)
        val toRegister2 = ObjectAnimator.ofFloat(binding.tvRegisterToLogin, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(nameT, nameI, emailT, emailI, passwordT, passwordI)
        }
        val together2 = AnimatorSet().apply {
            playTogether(toRegister, toRegister2)
        }
        val titleAnim = AnimatorSet().apply {
            playTogether(image, title)
        }

        AnimatorSet().apply {
            playSequentially(titleAnim, together, register, together2)
            start()
        }

    }

    private fun showLoading() {
        registerViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}