package com.dicodingsub.storyapp

import com.dicodingsub.storyapp.data.response.StoryResponse

object DummyData {

    fun generateDummyStoryResponse(): List<StoryResponse> {
        val items: MutableList<StoryResponse> = arrayListOf()
        for (i in 0..100) {
            val story = StoryResponse(
                "https://story-api.dicoding.dev/images/stories/photos-1667058715359_iRitxc-I.jpg",
                i.toString(),
                "Ichsan",
                "Testing",
                -17.1696877,
                i.toString(),
                104.523145,
            )
            items.add(story)
        }
        return items
    }
}