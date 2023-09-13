package com.mr_17.vidconnect.ui.profile.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.mr_17.front_benchers.ui.auth.AuthViewModel
import com.mr_17.vidconnect.MainActivity
import com.mr_17.vidconnect.R
import com.mr_17.vidconnect.databinding.FragmentProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProfileFragment : Fragment(R.layout.fragment_profile) {
    private lateinit var binding: FragmentProfileBinding
    private val authViewModel by viewModels<AuthViewModel>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        (activity as MainActivity).setToolbarTitle("Profile")

        binding.apply {
            etFullName.setText(authViewModel.currentUser?.displayName.toString())
            etEmailAddress.setText(authViewModel.currentUser?.email.toString())
            etPhoneNumber.setText(authViewModel.currentUser?.phoneNumber.toString())
        }
    }
}