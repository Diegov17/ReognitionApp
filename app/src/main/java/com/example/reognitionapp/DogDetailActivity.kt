package com.example.reognitionapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
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

        binding.dog = dog
    }
}