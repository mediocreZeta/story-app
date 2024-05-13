package com.example.storyapp.ui.screen.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.data.local.story.StoryEntity
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailStoryBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupAppBar()
        setupData()
    }

    private fun setupData() {
        val story = intent.getParcelableExtra<StoryEntity>(STORY) as StoryEntity
        Glide.with(applicationContext)
            .load(story.photoUrl)
            .into(binding.imgPhoto)
        binding.tvCreateDate.text = story.createdDate
        binding.tvContent.text = story.description
        binding.tvNameDesc.text = story.name
    }

    private fun setupAppBar() {
        binding.detailToolbar.apply {
            inflateMenu(R.menu.main_menu)
            menu.clear()
            setTitle(R.string.detail_story)
            setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp)
            setNavigationContentDescription(R.string.navigate_up)
            setNavigationOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }
        }
    }

    companion object {
        const val STORY = "story"
    }
}