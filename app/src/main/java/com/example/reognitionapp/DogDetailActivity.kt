package com.example.reognitionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.reognitionapp.databinding.ActivityDogDetailBinding
import com.example.reognitionapp.databinding.ActivityDogListBinding

class DogDetailActivity : AppCompatActivity() {

    companion object {
        const val DOG_KEY = "dog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityDogDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dog = intent?.extras?.getParcelable<Dog>(DOG_KEY)

        if (dog == null) {
            Toast.makeText(this, "Dog not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        binding.dogIndex.text = getString(R.string.dog_index_format, dog.index)
        binding.lifeExpectancy.text = getString(R.string.dog_life_expectancy_format, dog.lifeExpectancy)
        Glide.with(this)
            .load(dog.imgUrl)
            .into(binding.dogImage)
        binding.closeButton.setOnClickListener {
            finish()
        }

        binding.dog = dog
    }
}