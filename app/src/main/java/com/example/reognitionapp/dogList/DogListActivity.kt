package com.example.reognitionapp.dogList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.reognitionapp.DogDetailActivity
import com.example.reognitionapp.DogDetailActivity.Companion.DOG_KEY
import com.example.reognitionapp.api.ApiResponseStatus
import com.example.reognitionapp.databinding.ActivityDogListBinding

class DogListActivity : AppCompatActivity() {

    private val dogListViewModel: DogListViewModel by viewModels()

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        progressBar = binding.progressBar

        val recycler = binding.dogRecycler
        recycler.layoutManager = GridLayoutManager(this, GRIP_SPAN_VALUE)

        val adapter = DogAdapter()
        adapter.setOnItemClickListener {
            val intent = Intent(this, DogDetailActivity::class.java)
            intent.putExtra(DOG_KEY, it)
            startActivity(intent)
        }

        recycler.adapter = adapter

        dogListViewModel.dogList.observe(this) { dogList ->
            adapter.submitList(dogList)
        }

        dogListViewModel.status.observe(this) { status ->

            when (status) {
                is ApiResponseStatus.Loading -> showProgressBar()
                is ApiResponseStatus.Success -> hideProgressBar()
                is ApiResponseStatus.Error -> {
                    hideProgressBar()
                    Toast.makeText(this, status.messageId, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hideProgressBar() {
        progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    companion object {

        private const val GRIP_SPAN_VALUE = 3
    }
}