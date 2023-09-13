package com.mr_17.vidconnect.ui.auth.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.mr_17.vidconnect.ui.auth.AuthViewModel
import com.mr_17.vidconnect.MainActivity
import com.mr_17.vidconnect.R
import com.mr_17.vidconnect.data.Resource
import com.mr_17.vidconnect.databinding.FragmentSignUpBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignUpFragment : Fragment(R.layout.fragment_sign_up) {
    private lateinit var binding: FragmentSignUpBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSignUpBinding.bind(view)

        (activity as MainActivity).setToolbarTitle("Sign Up")

        binding.apply {
            btnCreateAccount.setOnClickListener {
                authViewModel.signupUser(
                    "${etFirstName.text.toString()} ${etLastName.text.toString()}",
                    etEmailAddress.text.toString(),
                    etPassword.text.toString(),
                    etConfirmPassword.text.toString()
                )
            }
        }

        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            authViewModel.signupFlow.collectLatest {
                when(it) {
                    is Resource.Failure -> {
                        toggleLoading(false)
                        showToast(it.exception.message.toString())
                    }
                    is Resource.Success -> {
                        toggleLoading(false)
                        findNavController().navigate(R.id.action_signUpFragment_to_homeFragment)
                    }
                    is Resource.Loading -> {
                        toggleLoading(true)
                    }
                    else -> {}
                }
            }
        }
    }

    private fun toggleLoading(isLoading: Boolean) {
        binding.loadingBar.isVisible = isLoading
        binding.btnCreateAccount.visibility = if(!isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}