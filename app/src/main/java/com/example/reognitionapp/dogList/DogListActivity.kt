package com.example.reognitionapp.dogList

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.reognitionapp.Dog
import com.example.reognitionapp.DogDetailActivity
import com.example.reognitionapp.DogDetailActivity.Companion.DOG_KEY
import com.example.reognitionapp.R
import com.example.reognitionapp.databinding.ActivityDogListBinding

class DogListActivity : AppCompatActivity() {

    private val dogListViewModel: DogListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recycler = binding.dogRecycler
        recycler.layoutManager = LinearLayoutManager(this)

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
    }
}