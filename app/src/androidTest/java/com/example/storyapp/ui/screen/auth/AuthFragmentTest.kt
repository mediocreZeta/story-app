package com.example.storyapp.ui.screen.auth

import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import com.example.storyapp.R
import com.example.storyapp.other.EspressoIdlingResource
import com.example.storyapp.ui.screen.main.MainFragment
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@LargeTest
@RunWith(AndroidJUnit4::class)
class AuthFragmentTest {
    private val name = "doublezetagundam@gmail.com"
    private val password = "zetagundam01"

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun loginTest() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        launchFragmentInContainer<LoginFragment>(themeResId = R.style.Theme_StoryApp).onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        onView(withId(R.id.fragment_login_parent)).check(matches(isDisplayed()))
        onView(withId(R.id.textInput_email_login)).perform(typeText(name))
        onView(withId(R.id.textInput_password_login)).perform(typeText(password)).perform(
            ViewActions.closeSoftKeyboard()
        )
        onView(withId(R.id.btn_login)).perform(click())

        Assert.assertEquals(R.id.mainFragment, navController.currentDestination?.id)
    }

    @Test
    fun logoutTest() {
        val navController = TestNavHostController(
            ApplicationProvider.getApplicationContext()
        )
        launchFragmentInContainer<MainFragment>(themeResId = R.style.Theme_StoryApp).onFragment { fragment ->
            navController.setGraph(R.navigation.nav_graph)
            navController.setCurrentDestination(R.id.mainFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        onView(withId(R.id.main_toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.rv_story)).check(matches(isDisplayed()))
        onView(withId(R.id.btn_logout)).perform(click())
        Assert.assertEquals(R.id.loginFragment, navController.currentDestination?.id)
    }
}