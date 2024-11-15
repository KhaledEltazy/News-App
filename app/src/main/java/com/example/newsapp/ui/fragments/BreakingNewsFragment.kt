package com.example.newsapp.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.newsapp.R
import com.example.newsapp.adapters.NewsAdapter
import com.example.newsapp.databinding.FragmentBreakingNewBinding
import com.example.newsapp.ui.NewsActivity
import com.example.newsapp.ui.NewsViewmodel
import com.example.newsapp.util.Resource


class BreakingNewsFragment : Fragment() {
   private lateinit var binding : FragmentBreakingNewBinding
   private lateinit var viewmodel : NewsViewmodel
   private lateinit var newsadapter : NewsAdapter
   val TAG = "BreakingNewsFragment"
   override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater,R.layout.fragment_breaking_new,container,false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewmodel = (activity as NewsActivity).viewmodel

        setupRecyclerView()

        newsadapter.setOnItemClickListener {
            val bundle = Bundle().apply {
                putSerializable("article",it)
            }
            findNavController().navigate(
                R.id.action_breakingNewsFragment_to_articleFragment,
                bundle
            )
        }

        viewmodel.breakingNews.observe(viewLifecycleOwner, Observer{ response->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                        newsadapter.diifer.submitList(newsResponse.articles)
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    response.massage?.let {massage->
                        Log.e(TAG,"An error occured: $massage")
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                }
            }
        })
    }

    private fun hideProgressBar(){
        binding.pbBreakingNews.visibility = View.INVISIBLE
    }

    private fun showProgressBar(){
        binding.pbBreakingNews.visibility = View.VISIBLE
    }

    private fun setupRecyclerView(){
        newsadapter = NewsAdapter()
        binding.rvBreakingNews.apply {
            adapter = newsadapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}