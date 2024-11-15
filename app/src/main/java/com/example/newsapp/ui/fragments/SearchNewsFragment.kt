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
   lateinit var viewmodel : NewsViewmodel
   lateinit var newsadapter : NewsAdapter
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
        setupRecycleView()

        newsadapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_searchNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewmodel = (activity as NewsActivity).viewmodel

        var job : Job? = null

        binding.etSearch.addTextChangedListener {editable->
            job?.cancel()
            job = MainScope().launch {
                delay(SEARCH_NEWS_TIMES_DELAY)
                editable?.let {
                    if(editable.toString().isNotEmpty()){
                        viewmodel.searchNews(editable.toString())
                    }
                }
            }
        }

        viewmodel.searchNews.observe(viewLifecycleOwner, Observer {response->
            when(response){
                is Resource.Success ->{
                    hideProgressbar()
                    response.data?.let {newsResponse ->
                        newsadapter.diifer.submitList(newsResponse.articles)
                    }
                }
                is Resource.Loading ->{
                    showProgressbar()
                }
                is Resource.Error ->{
                    hideProgressbar()
                    response.massage?.let{massage->
                        Log.e(TAG,"An error occured $massage")
                    }
                }
            }

        })

    }

    private fun hideProgressbar(){
        binding.paginationProgressBar.visibility = View.INVISIBLE
    }


    private fun showProgressbar(){
        binding.paginationProgressBar.visibility = View.VISIBLE
    }

    private fun setupRecycleView(){
        newsadapter = NewsAdapter()
        binding.rvSearchNews.apply {
            adapter = newsadapter
            layoutManager = LinearLayoutManager(activity)
        }
    }


}