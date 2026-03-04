package com.inesengel.travelapp.UI.view.activities

import android.animation.Animator
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.inesengel.travelapp.R
import com.inesengel.travelapp.UI.model.NavigationState
import com.inesengel.travelapp.UI.view.utils.navigateTo
import com.inesengel.travelapp.UI.viewmodel.SplashViewModel
import com.inesengel.travelapp.databinding.SplashScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private lateinit var binding: SplashScreenBinding

    private val splashViewModel: SplashViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = SplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAnimation()
        setupCollectors()

        splashViewModel.startAnimation()
    }

    private fun goToLoginScreen() {
        navigateTo(
            destination = AuthActivity::class.java
        )
    }

    private fun goToMainScreen() {
        navigateTo(
            destination = MainActivity::class.java
        )
    }

    private fun setupAnimation() {
        binding.apply {
            lottieAnimationView.setAnimation(R.raw.plane_loader)
            lottieAnimationView.repeatCount = 0
            lottieAnimationView.addAnimatorListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {}

                override fun onAnimationEnd(animation: Animator) {
                    splashViewModel.checkLoginStatus()
                }

                override fun onAnimationCancel(animation: Animator) {}
                override fun onAnimationRepeat(animation: Animator) {}
            })
        }
    }

    private fun setupCollectors() {
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    splashViewModel.playAnimationEvent.collect { shouldPlay ->
                        if (shouldPlay) {
                            binding.lottieAnimationView.playAnimation()
                        }
                    }
                }

                launch {
                    splashViewModel.navigationEvent.collect { destination ->
                        when (destination) {
                            NavigationState.GO_TO_MAIN -> goToMainScreen()
                            NavigationState.GO_TO_AUTH -> goToLoginScreen()
                            else -> return@collect
                        }
                        finish()
                    }
                }
            }
        }
    }
}
