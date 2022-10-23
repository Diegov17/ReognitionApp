package com.example.reognitionapp.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.viewModelScope
import androidx.navigation.findNavController
import com.example.reognitionapp.MainActivity
import com.example.reognitionapp.R
import com.example.reognitionapp.api.ApiResponseStatus
import com.example.reognitionapp.databinding.ActivityLoginBinding
import com.example.reognitionapp.domain.User

class LoginActivity : AppCompatActivity(), LoginFragment.LoginFragmentActions,
    SignUpFragment.SignUpFragmentActions {

    private val viewModel: AuthViewModel by viewModels()

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.status.observe(this) { status ->
            when (status) {
                is ApiResponseStatus.Loading -> showProgressBar()
                is ApiResponseStatus.Success -> hideProgressBar()
                is ApiResponseStatus.Error -> {
                    hideProgressBar()
                    showErrorDialog(status.messageId)
                }
            }
        }

        viewModel.user.observe(this) { user ->
            if (user != null) {
                User.setLoggedInUser(this, user)
                startMainActivity()
            }
        }
    }

    private fun startMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    override fun onRegisterButtonClick() {
        findNavController(R.id.nav_host_fragment).navigate(LoginFragmentDirections.actionLoginFragmentToSignUpFragment())
    }

    override fun onLoginFieldsValidated(email: String, password: String) {
        viewModel.login(email, password)
    }

    override fun onFieldsValidated(email: String, password: String, confirmationPassword: String) {
        viewModel.signUp(email, password, confirmationPassword)
    }

    private fun showErrorDialog(meesageId: Int) {
        AlertDialog.Builder(this)
            .setTitle(R.string.dialog_title_there_was_an_error)
            .setMessage(meesageId)
            .setPositiveButton(android.R.string.ok) { _, _ -> /** Dismiss dialog **/ }
            .create()
            .show()
    }

    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
    }

    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
    }
}