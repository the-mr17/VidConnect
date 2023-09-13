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
import com.mr_17.vidconnect.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login) {
    private lateinit var binding: FragmentLoginBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        (activity as MainActivity).setToolbarTitle("Login")

        binding.apply {
            btnLogin.setOnClickListener {
                authViewModel.loginUser(etEmailAddress.text.toString(), etPassword.text.toString())
            }
        }

        initObservers()
    }

    private fun initObservers() {
        lifecycleScope.launch {
            authViewModel.loginFlow.collectLatest {
                when(it) {
                    is Resource.Failure -> {
                        toggleLoading(false)
                        showToast(it.exception.message.toString())
                    }
                    is Resource.Success -> {
                        toggleLoading(false)
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
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
        binding.btnLogin.visibility = if(!isLoading) View.VISIBLE else View.INVISIBLE
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }
}