package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentSearchNewsBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewmodel
import com.example.newsapp.util.Constants.Companion.SEARCH_NEWS_TIMES_DELAY
import com.example.newsapp.util.Resource
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SearchNewsFragment : Fragment() {
   private lateinit var binding : FragmentSearchNewsBinding
   private lateinit var viewmodel : NewsViewmodel
   private lateinit var newsAdapter : NewsAdapter
   val TAG = "Searching news Fragment"
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_search_news,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = (activity as NewsActivity).viewmodel

        createRecycleView()

        var job : Job? = null

        binding.etSearch.addTextChangedListener { text->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIMES_DELAY)
                text?.let{
                    if(text.toString().isNotEmpty()){
                        viewmodel.getSearchNews(text.toString())
                    }
                }
            }
        }

        newsAdapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(R.id.action_searchNewsFragment_to_articleFragment,
                bundle)
        }

        viewmodel.searchNews.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is Resource.Success->{
                    hideProgressBar()
                    newsAdapter.diifer.submitList(response.data?.articles)
                }
                is Resource.Error->{
                    Log.e(TAG,"Error in SearchFragment")
                }
                is Resource.Loading->{
                    showProgressBar()
                }
            }
        }
        )
    }

    private fun hideProgressBar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun createRecycleView(){
        newsAdapter = NewsAdapter()
        binding.rvSearchNews.apply{
            layoutManager = LinearLayoutManager(activity)
            adapter = newsAdapter
        }

    }
}