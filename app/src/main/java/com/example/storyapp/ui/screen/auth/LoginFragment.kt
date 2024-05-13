package com.example.storyapp.ui.screen.auth

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.storyapp.R
import com.example.storyapp.data.Resources
import com.example.storyapp.databinding.FragmentLoginBinding
import com.example.storyapp.other.EspressoIdlingResource
import com.example.storyapp.ui.LoginViewModelFactory
import com.example.storyapp.ui.customviews.CustomAlertDialog
import com.example.storyapp.ui.customviews.CustomLoadingDialog
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var job: Job? = null

    private lateinit var viewModel: LoginViewModel
    private var isEmailValid = false
    private var isPasswordValid = false

    private lateinit var loadingDialog: CustomLoadingDialog
    private lateinit var customDialog: CustomAlertDialog
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        val factory = LoginViewModelFactory.getInstance(requireActivity())

        viewModel = ViewModelProvider(this, factory)[LoginViewModel::class.java]
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
            repeatOnLifecycle(Lifecycle.State.STARTED){
                validInputObserver()
            }
        }

    }

    private fun setupBinding() {
        binding.btnLogin.isEnabled = isEmailValid && isPasswordValid
        binding.tvLoginDesc.text =
            resources.getString(R.string.auth_section_desc, resources.getString(R.string.login))
        binding.btnLoginGuest.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_cameraFragment)
        }
        binding.btnLogin.setOnClickListener {
            val email = binding.textInputEmailLogin.text.toString()
            val password = binding.textInputPasswordLogin.text.toString()
            loginValidation(email, password)
        }
    }

    private suspend fun validInputObserver() {
        with(binding) {
            val emailFlow = textInputEmailLogin.isValidInput
            val passwordFlow = textInputPasswordLogin.isValidInput
            combine(emailFlow, passwordFlow) { email, password ->
                btnLogin.isEnabled = email && password
            }.stateIn(lifecycleScope)
        }
    }


    private fun loginValidation(email: String, password: String) {
        job?.cancel()
        job = lifecycleScope.launch {
            EspressoIdlingResource.increment()
            viewModel.login(email, password).collect { result ->
                when (result) {
                    is Resources.Loading -> {
                        loadingDialog.startLoadingDialog()
                    }

                    is Resources.Error -> {
                        loadingDialog.closeLoadingDialog()
                        customDialog.startDialog(result.errorMessage, "Go back")
                        cancel()
                    }

                    is Resources.Success ->
                        lifecycleScope.launch {
                            viewModel.updateTokenAndSession(result.data.token)
                            loadingDialog.closeLoadingDialog()
                            findNavController().navigate(R.id.action_loginFragment_to_mainFragment)
                            EspressoIdlingResource.decrement()

                        }
                }
            }
        }
    }

    private fun setupAppBar() {
        binding.toolbarLogin.apply {
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
        }
    }


    private fun playAnimation() {
        val loginSection =
            ObjectAnimator.ofFloat(binding.tvLoginSection, View.ALPHA, 1f).setDuration(100)
        val loginDesc =
            ObjectAnimator.ofFloat(binding.tvLoginDesc, View.ALPHA, 1f).setDuration(100)
        val image = ObjectAnimator.ofFloat(binding.imgProfile, View.ALPHA, 1f).setDuration(100)

        val loginGuest =
            ObjectAnimator.ofFloat(binding.btnLoginGuest, View.ALPHA, 1f).setDuration(200)
        val login = ObjectAnimator.ofFloat(binding.btnLogin, View.ALPHA, 1f).setDuration(200)
        val textInputEmail =
            ObjectAnimator.ofFloat(binding.textInputEmailLogin, View.ALPHA, 1f).setDuration(200)
        val textInputPassword =
            ObjectAnimator.ofFloat(binding.textInputPasswordLogin, View.ALPHA, 1f).setDuration(200)
        val together = AnimatorSet().apply {
            playTogether(textInputEmail, textInputPassword)
        }
        AnimatorSet().apply {
            playSequentially(loginSection, loginDesc, image, together, loginGuest, login)
            start()
        }
    }

}