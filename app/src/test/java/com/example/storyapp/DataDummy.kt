package com.example.storyapp

import com.example.storyapp.data.local.story.StoryEntity
import java.time.LocalDateTime

object DataDummy {
    fun generateDummyStoryResponse(): List<StoryEntity> {
        val items: MutableList<StoryEntity> = arrayListOf()
        for (i in 0..20) {
            val story = StoryEntity(
                i.toString(),
                "photoUrl$i",
                name = "Mr. $i",
                createdDate = LocalDateTime.now().toString(),
                description = "this is description number $i"
            )
            items.add(story)
        }
        return items
    }
}