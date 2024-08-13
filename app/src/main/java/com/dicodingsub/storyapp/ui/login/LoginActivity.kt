package com.dicodingsub.storyapp.ui.login

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
import com.dicodingsub.storyapp.data.pref.UserModel
import com.dicodingsub.storyapp.databinding.ActivityLoginBinding
import com.dicodingsub.storyapp.di.ViewModelFactory
import com.dicodingsub.storyapp.ui.main.MainActivity
import com.dicodingsub.storyapp.ui.register.RegisterActivity

class LoginActivity : AppCompatActivity() {
    private val loginViewModel by viewModels<LoginViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupView()
        setupAction()
        showLoading()
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
        binding.loginButton.setOnClickListener {
            showLoading()
            val email = binding.loginEmailEditText.text.toString()
            val password = binding.loginPasswordEditText.text.toString()
            if (email.isEmpty()) {
                binding.loginEmailEditTextLayout.error = "Email cannot be empty"
                return@setOnClickListener
            } else if (password.isEmpty()) {
                binding.loginPasswordEditTextLayout.error = "Password cannot be empty"
                return@setOnClickListener
            } else {
                loginViewModel.login(email, password)
            }
            loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
                loginViewModel.saveSession(
                    UserModel(
                        email = response.loginResult?.name.toString(),
                        token = response.loginResult?.token.toString(),
                        isLogin = true
                    )
                )
            }
            loginViewModel.toastText.observe(this@LoginActivity) {
                it.getContentIfNotHandled()?.let { toastText ->
                    Toast.makeText(
                        this@LoginActivity, toastText, Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        binding.tvLoginToRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
        observeViewModel()
    }

    private fun observeViewModel() {
        loginViewModel.loginResponse.observe(this@LoginActivity) { response ->
            if (response.error == false) {
                AlertDialog.Builder(this).apply {
                    setTitle("Success!")
                    setMessage("Login successful! Let's post your stories")
                    setPositiveButton("Continue") { _, _ ->
                        val intent = Intent(context, MainActivity::class.java)
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
        val image = ObjectAnimator.ofFloat(binding.ivLoginImage, View.ALPHA, 1f).setDuration(300)
        val title = ObjectAnimator.ofFloat(binding.tvLoginTitle, View.ALPHA, 1f).setDuration(500)
        val emailT = ObjectAnimator.ofFloat(binding.tvLoginTextEmail, View.ALPHA,1f).setDuration(300)
        val emailI = ObjectAnimator.ofFloat(binding.loginEmailEditTextLayout, View.ALPHA, 1f).setDuration(300)
        val passwordT = ObjectAnimator.ofFloat(binding.tvLoginTextPassword, View.ALPHA, 1f).setDuration(500)
        val passwordI = ObjectAnimator.ofFloat(binding.loginPasswordEditTextLayout, View.ALPHA, 1f).setDuration(500)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(300)
        val toRegister = ObjectAnimator.ofFloat(binding.tvLoginToRegister, View.ALPHA, 1f).setDuration(300)
        val toRegister2 = ObjectAnimator.ofFloat(binding.tvLoginToRegisterText, View.ALPHA, 1f).setDuration(300)

        val together = AnimatorSet().apply {
            playTogether(emailT, emailI, passwordT, passwordI)
        }
        val together2 = AnimatorSet().apply {
            playTogether(toRegister, toRegister2)
        }
        val titleAnim = AnimatorSet().apply {
            playTogether(image, title)
        }

        AnimatorSet().apply {
            playSequentially(titleAnim, together, login, together2)
            start()
        }

    }

    private fun showLoading() {
        loginViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}