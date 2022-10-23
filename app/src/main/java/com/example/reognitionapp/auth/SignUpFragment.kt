package com.example.reognitionapp.auth

import android.content.Context
import android.os.Bundle
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.reognitionapp.R
import com.example.reognitionapp.databinding.FragmentSignUpBinding
import com.example.reognitionapp.isValidEmail

class SignUpFragment : Fragment() {

    interface SignUpFragmentActions {
        fun onFieldsValidated(email: String, password: String, confirmationPassword: String)
    }

    private lateinit var signUpFragmentActions: SignUpFragmentActions

    override fun onAttach(context: Context) {
        super.onAttach(context)
        signUpFragmentActions = try {
            context as SignUpFragmentActions
        } catch (e: ClassCastException) {
            throw ClassCastException("$context must implement LoginFragmentActions")
        }
    }

    private lateinit var binding: FragmentSignUpBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSignUpBinding.inflate(inflater)
        setupSigningButton()
        return binding.root
    }

    private fun setupSigningButton() {
        binding.signUpButton.setOnClickListener {
            validateValues()
        }
    }

    private fun validateValues() {
        binding.emailInput.error = ""
        binding.passwordInput.error = ""
        binding.confirmPasswordInput.error = ""

        val email = binding.emailEdit.text.toString()
        if (!isValidEmail(email)) {
            binding.emailInput.error = getString(R.string.sign_up_email_not_valid_error)
            return
        }

        val password = binding.passwordEdit.text.toString()
        if (password.isEmpty()) {
            binding.passwordInput.error = getString(R.string.sign_up_password_empty_error)
            return
        }

        val passwordConfirmation = binding.confirmPasswordEdit.text.toString()
        if (passwordConfirmation.isEmpty()) {
            binding.confirmPasswordInput.error =
                getString(R.string.sign_up_confirmation_password_empty_error)
            return
        }

        if (password != passwordConfirmation) {
            binding.passwordInput.error = getString(R.string.sign_up_password_not_matching_error)
            return
        }

        signUpFragmentActions.onFieldsValidated(email, password, passwordConfirmation)
    }

}