package com.dicodingsub.storyapp.ui.welcome

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.dicodingsub.storyapp.databinding.ActivityWelcomeBinding
import com.dicodingsub.storyapp.di.ViewModelFactory
import com.dicodingsub.storyapp.ui.login.LoginActivity
import com.dicodingsub.storyapp.ui.main.MainActivity
import com.dicodingsub.storyapp.ui.main.MainViewModel
import com.dicodingsub.storyapp.ui.register.RegisterActivity

@Suppress("DEPRECATION")
class WelcomeActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityWelcomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityWelcomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUser()
        setupView()
        setupAction()
        playAnimation()
    }

    private fun setupUser() {
        mainViewModel.getUser().observe(this@WelcomeActivity) {
            if (it.isLogin) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                Handler(Looper.getMainLooper()).postDelayed({}, 3000)
            }
        }
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
            val intent = Intent(this, RegisterActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
        binding.loginButton.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
        }
    }

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.welcomeTitle, View.ALPHA, 1f).setDuration(300)
        val desc = ObjectAnimator.ofFloat(binding.welcomeDesc, View.ALPHA, 1f).setDuration(300)
        val register = ObjectAnimator.ofFloat(binding.registerButton, View.ALPHA, 1f).setDuration(1000)
        val login = ObjectAnimator.ofFloat(binding.loginButton, View.ALPHA, 1f).setDuration(500)

        val together = AnimatorSet().apply {
            playTogether(register, login)
        }

        AnimatorSet().apply {
            playSequentially(title, desc, together)
            start()
        }
    }
}
