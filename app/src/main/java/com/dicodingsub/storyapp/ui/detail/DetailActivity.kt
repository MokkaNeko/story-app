package com.dicodingsub.storyapp.ui.detail

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.dicodingsub.storyapp.R
import com.dicodingsub.storyapp.databinding.ActivityDetailBinding

@Suppress("DEPRECATION")
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding

    private val name: String by lazy {
        intent.getStringExtra("name") ?: ""
    }
    private val description: String by lazy {
        intent.getStringExtra("description") ?: ""
    }
    private val photo: String by lazy {
        intent.getStringExtra("photo") ?: ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setupView()
        setupDetailStory()
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    private fun setupView() {
        supportActionBar?.apply {
            title = getString(R.string.story_detail)
            setDisplayHomeAsUpEnabled(true)
        }
    }
    private fun setupDetailStory() {
        binding.apply {
            detailStoryTitle.text = name
            detailStoryDescription.text = description
            Glide.with(this@DetailActivity)
                .load(photo)
                .fitCenter()
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(detailStoryView)
        }
    }
}