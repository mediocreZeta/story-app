package com.example.storyapp.ui.screen.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.databinding.FragmentRegisterBinding
import com.example.storyapp.data.Resources
import com.example.storyapp.ui.RegisterViewModelFactory
import com.example.storyapp.ui.customviews.CustomAlertDialog
import com.example.storyapp.ui.customviews.CustomLoadingDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null

    private lateinit var viewModel: RegisterViewModel
    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var customDialog: CustomAlertDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        val factory = RegisterViewModelFactory.getInstance(requireActivity())
        viewModel = ViewModelProvider(requireActivity(), factory)[RegisterViewModel::class.java]

        loadingDialog = CustomLoadingDialog(requireActivity())
        customDialog = CustomAlertDialog(requireActivity())

        setupAppBar()
        playAnimation()
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupBinding()
        viewLifecycleOwner.lifecycleScope.launch {
            validInputObserver()
        }

        binding.btnRegister.setOnClickListener {
            val email = binding.textInputEmail.text.toString()
            val name = binding.textInputName.text.toString()
            val password = binding.textInputPassword.text.toString()

            job?.cancel()
            viewModel.register(name, email,password)
            job = lifecycleScope.launch {
                viewModel.uiState.collect { result ->
                    when (result) {
                        is Resources.Error -> {
                            loadingDialog.closeLoadingDialog()
                            customDialog.startDialog(result.errorMessage, "Go back")
                            cancel()
                        }

                        is Resources.Loading -> {
                            binding.btnRegister.isEnabled = false
                            loadingDialog.startLoadingDialog()
                        }

                        is Resources.Success -> {
                            loadingDialog.closeLoadingDialog()
                            Toast.makeText(requireActivity(),
                                getString(R.string.account_has_been_successfully_created), Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }

                }
            }
        }
    }

    private fun setupBinding() {
        with(binding) {
            tvRegisterDesc.text =
                resources.getString(
                    R.string.auth_section_desc,
                    resources.getString(R.string.register)
                )
            btnRegister.setOnClickListener {
                val email = binding.textInputEmail.text.toString()
                val name = binding.textInputName.text.toString()
                val password = binding.textInputPassword.text.toString()

                job?.cancel()
                viewModel.register(name, email, password)
                job = lifecycleScope.launch {
                    viewModel.uiState.collect { result ->
                        when (result) {
                            is Resources.Error -> {
                                loadingDialog.closeLoadingDialog()
                                cancel()
                            }

                            is Resources.Loading -> {
                                binding.btnRegister.isEnabled = false
                                loadingDialog.startLoadingDialog()
                            }

                            is Resources.Success -> {
                                loadingDialog.closeLoadingDialog()
                                customDialog.startDialog(
                                    result.data,
                                    "Navigate"
                                ) { findNavController().popBackStack() }
                            }
                        }

                    }
                }
            }

        }

    }

    private suspend fun validInputObserver() {
        with(binding) {
            val emailFlow = textInputEmail.isValidInput
            val nameFlow = textInputName.isValidInput
            val passwordFlow = textInputPassword.isValidInput
            combine(emailFlow, nameFlow, passwordFlow) { email, name, password ->
                btnRegister.isEnabled = email && name && password
            }.stateIn(lifecycleScope)
        }
    }

    private fun setupAppBar() {
        binding.registerToolbar.apply {
            inflateMenu(R.menu.main_menu)
            menu.findItem(R.id.btn_logout).setEnabled(false).setVisible(false)
            menu.findItem(R.id.btn_map).setEnabled(false).setVisible(false)
            setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.btn_language -> {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                        true
                    }
                    else -> false
                }
            }
            setNavigationIcon(R.drawable.baseline_arrow_back_black_24dp)
            setNavigationContentDescription(R.string.navigate_up)
            setNavigationOnClickListener { findNavController().navigateUp() }
        }
    }

    private fun playAnimation() {
        val registerButton =
            ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(100)
        val registerSection =
            ObjectAnimator.ofFloat(binding.tvRegisterSection, View.ALPHA, 1f).setDuration(100)
        val registerDesc =
            ObjectAnimator.ofFloat(binding.tvRegisterDesc, View.ALPHA, 1f).setDuration(100)
        val image = ObjectAnimator.ofFloat(binding.imgProfile, View.ALPHA, 1f).setDuration(100)
        val textInputName =
            ObjectAnimator.ofFloat(binding.textInputName, View.ALPHA, 1f).setDuration(100)
        val textInputPassword =
            ObjectAnimator.ofFloat(binding.textInputPassword, View.ALPHA, 1f).setDuration(100)
        val textInputEmail =
            ObjectAnimator.ofFloat(binding.textInputEmail, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(textInputEmail, textInputName, textInputPassword)
        }

        AnimatorSet().apply {
            playSequentially(registerSection, registerDesc, image, together, registerButton)
            start()
        }
    }

    companion object {
        val TAG = RegisterFragment::class.java.simpleName
    }

}
