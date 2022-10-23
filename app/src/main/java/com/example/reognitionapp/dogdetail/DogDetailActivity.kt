package com.example.reognitionapp.dogdetail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.example.reognitionapp.R
import com.example.reognitionapp.api.ApiResponseStatus
import com.example.reognitionapp.databinding.ActivityDogDetailBinding
import com.example.reognitionapp.domain.Dog
import com.example.reognitionapp.main.MainViewModel
import com.example.reognitionapp.setVisibility
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DogDetailActivity : AppCompatActivity() {

    private val viewModel: DogDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)
        val isRecognition = intent?.extras?.getBoolean(IS_RECOGNITION_KEY, false) ?: false

        if (dog == null) {
            Toast.makeText(this, "Dog not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.dogIndex.text = getString(R.string.dog_index_format, dog.index)
        binding.lifeExpectancy.text =
            getString(R.string.dog_life_expectancy_format, dog.lifeExpectancy)
        Glide.with(this)
            .load(dog.imgUrl)
            .into(binding.dogImage)

        viewModel.status.observe(this) { status ->

            when (status) {
                is ApiResponseStatus.Loading -> setVisibility(binding.progressBar, true)
                is ApiResponseStatus.Success -> {
                    setVisibility(binding.progressBar, false)
                    finish()
                }
                is ApiResponseStatus.Error -> {
                    setVisibility(binding.progressBar, false)
                    Toast.makeText(this, status.messageId, Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.closeButton.setOnClickListener {
            if (isRecognition) {
                viewModel.addDogToUser(dog.id)
            } else {
                finish()
            }
        }


        binding.dog = dog
    }

    companion object {
        const val DOG_KEY = "dog"
        const val IS_RECOGNITION_KEY = "is_recognition"
    }
}