package com.mr_17.vidconnect.ui.call.fragments

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.mr_17.vidconnect.R
import com.mr_17.vidconnect.databinding.FragmentCallBinding
import com.mr_17.vidconnect.service.ServiceRepository
import javax.inject.Inject

class CallFragment : Fragment(R.layout.fragment_call) {
    private lateinit var binding: FragmentCallBinding
    private val args by navArgs<CallFragmentArgs>()

    private var targetId: String? = null
    private var isVideoCall = true
    private var isCaller = true

    @Inject
    lateinit var serviceRepository: ServiceRepository

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCallBinding.bind(view)

        init()
    }

    private fun init() {
        args.targetId?.let {
            this.targetId = it
        }?: kotlin.run {
            findNavController().popBackStack()
        }

        isVideoCall = args.isVideoCall
        isCaller = isCaller

        binding.apply {
            if (!isVideoCall) {
                toggleCameraButton.isVisible = false
                screenShareButton.isVisible = false
                switchCameraButton.isVisible = false
            }
        }
    }
}
