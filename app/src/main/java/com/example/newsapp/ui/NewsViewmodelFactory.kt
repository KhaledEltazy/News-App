package com.example.newsapp.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.newsapp.repository.NewsRepository

class NewsViewmodelFactory(private var newsRepository: NewsRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(NewsViewmodel::class.java)) {
            return NewsViewmodel(newsRepository) as T
        } else {
            throw IllegalArgumentException("unknown View Model")
        }
    }
}