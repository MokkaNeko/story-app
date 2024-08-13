package com.dicodingsub.storyapp.ui.main

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicodingsub.storyapp.R
import com.dicodingsub.storyapp.data.pref.UserModel
import com.dicodingsub.storyapp.databinding.ActivityMainBinding
import com.dicodingsub.storyapp.di.ViewModelFactory
import com.dicodingsub.storyapp.ui.maps.MapsActivity
import com.dicodingsub.storyapp.ui.upload.UploadActivity
import com.dicodingsub.storyapp.ui.welcome.WelcomeActivity

class MainActivity : AppCompatActivity() {
    private val mainViewModel by viewModels<MainViewModel>{
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityMainBinding
    private lateinit var storyAdapter: StoryAdapter
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        storyAdapter = StoryAdapter()
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = storyAdapter.withLoadStateFooter(footer = LoadingStateAdapter {storyAdapter.retry()})
        }

        setupUser()
        setupAction()
        setupMenu()
    }

    override fun onResume() {
        super.onResume()
        setupData()
    }

    private fun setupMenu() {
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logoutItem -> {
                    AlertDialog.Builder(this).apply {
                        setTitle("Logout")
                        setMessage("Are you sure want to logout?")
                        setPositiveButton("Yes") { _, _ ->
                            mainViewModel.logout()
                            moveActivity()
                        }
                        setNegativeButton("No") { dialog, _ -> dialog.cancel() }
                        create()
                        show()
                    }
                    true
                }
                R.id.mapItem -> {
                    startActivity(Intent(this, MapsActivity::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun setupUser() {
        mainViewModel.getUser().observe(this@MainActivity) {
            token = it.token
            Log.d(TAG, "setupUser: $token")
            if (!it.isLogin) {
                moveActivity()
            } else {
                setupData()
            }
        }
    }
    private fun moveActivity() {
        startActivity(Intent(this@MainActivity, WelcomeActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
    private fun setupData() {
        showLoading()
        mainViewModel.getUser().observe(this@MainActivity) {
            token = "Bearer " + it.token
            Log.d(TAG, "setupUser: $token")
            Log.d(TAG, "setupName: ${it.email}")
        }
        mainViewModel.storiesListPaging.observe(this) {
            if (it != null) {
                storyAdapter.submitData(lifecycle, it)
            } else {
                Log.d(TAG, "setupData: null")
            }
        }
    }
    private fun setupAction() {
        binding.uploadStoryFab.setOnClickListener {
            startActivity(Intent(this, UploadActivity::class.java))
        }
    }
    private fun showLoading() {
        mainViewModel.isLoading.observe(this) {
            binding.progressBar.visibility = if (it) View.VISIBLE else View.GONE
        }
    }
}