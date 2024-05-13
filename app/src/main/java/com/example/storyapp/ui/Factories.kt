package com.example.storyapp.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.storyapp.data.repository.StoryRepository
import com.example.storyapp.di.Injection
import com.example.storyapp.other.SettingsPreference
import com.example.storyapp.ui.screen.auth.LoginViewModel
import com.example.storyapp.ui.screen.auth.RegisterViewModel
import com.example.storyapp.ui.screen.main.MainViewModel
import com.example.storyapp.ui.screen.maps.MapsViewModel
import com.example.storyapp.ui.screen.upload.UploadViewModel

@Suppress("UNCHECKED_CAST")
class LoginViewModelFactory(
    private val repository: StoryRepository,
    private val preference: SettingsPreference
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LoginViewModel(repository, preference) as T
    }

    companion object {
        @Volatile
        private var instance: LoginViewModelFactory? = null
        fun getInstance(context: Context): LoginViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: LoginViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideSettingPreferences(context),
                )
            }.also { instance = it }
    }
}

@Suppress("UNCHECKED_CAST")
class RegisterViewModelFactory(
    private val repository: StoryRepository,
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RegisterViewModel(repository) as T
    }

    companion object {
        @Volatile
        private var instance: RegisterViewModelFactory? = null
        fun getInstance(context: Context): RegisterViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: RegisterViewModelFactory(
                    Injection.provideRepository(context),
                )
            }.also { instance = it }
    }
}


@Suppress("UNCHECKED_CAST")
class MainViewModelFactory(
    private val repository: StoryRepository,
    private val preference: SettingsPreference
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository, preference) as T
    }

    companion object {
        @Volatile
        private var instance: MainViewModelFactory? = null
        fun getInstance(context: Context): MainViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MainViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideSettingPreferences(context),
                )
            }.also { instance = it }
    }
}

@Suppress("UNCHECKED_CAST")
class UploadViewModelFactory(
    private val repository: StoryRepository,
    private val preference: SettingsPreference
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UploadViewModel(repository, preference) as T
    }

    companion object {
        @Volatile
        private var instance: UploadViewModelFactory? = null
        fun getInstance(context: Context): UploadViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: UploadViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideSettingPreferences(context),
                )
            }.also { instance = it }

    }
}

@Suppress("UNCHECKED_CAST")
class MapsViewModelFactory(
    private val repository: StoryRepository,
     private val preference: SettingsPreference
) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MapsViewModel(repository, preference) as T
    }

    companion object {
        @Volatile
        private var instance: MapsViewModelFactory? = null
        fun getInstance(context: Context): MapsViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MapsViewModelFactory(
                    Injection.provideRepository(context),
                    Injection.provideSettingPreferences(context),
                )
            }.also { instance = it }

    }

}

