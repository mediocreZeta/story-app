package com.example.storyapp.ui.customviews

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentErrorBinding

class ErrorFragment : Fragment() {
    private var _binding: FragmentErrorBinding? = null
    private val binding get() = _binding!!

    private val message: String
        get() = requireArguments().getString(MESSAGE)
            ?: throw IllegalArgumentException("Argument $MESSAGE required")

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val errorMessage = resources.getString(R.string.error, message)
        binding.tvError.text = errorMessage
    }

    companion object {
        val TAG: String = ErrorFragment::class.java.simpleName
        const val MESSAGE = "error"
        fun newInstance(errorMessage: String) = ErrorFragment().apply {
            arguments = bundleOf(
                MESSAGE to errorMessage
            )
        }
    }
}