package com.example.chatapp.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.example.chatapp.Constant
import com.example.chatapp.UserDiffUtil
import com.example.chatapp.R
import com.example.chatapp.databinding.ActivityMainBinding
import com.example.chatapp.viewmodel.SignUpViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val signUpViewModel : SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.signupBTN.setOnClickListener {
            if (showError()){
                if (Constant.isValidEmail(binding.crEmail.text.toString())){
                    if (Constant.isPasswordValid(password = binding.crPasswordET.text.toString())){
                        if (binding.crPasswordET.text.toString() ==  binding.crConfirmPasswordET.text.toString()){
                            signUpViewModel.createUserProfile(binding.crUsernameET.text.toString(), binding.crPasswordET.text.toString(),binding.crEmail.text.toString())
                        }else{
                            UserDiffUtil.showToast("Passwords do not match")
                        }
                    }else{
                        UserDiffUtil.showToast("Password must contain at least 8 characters, 1 capital letter, and 1 symbol")
                    }
                }else{
                    UserDiffUtil.showToast("Email not valid")
                }
            }
        }

        binding.loginPageBTN.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

    }

    private fun showError() :Boolean{
        if (binding.crUsernameET.text.isEmpty()){
            binding.crUsernameET.error = "Please Enter Username"
            return false
        }else if (binding.crEmail.text.isEmpty()){
            binding.crEmail.error = "Please Enter Email"
            return false
        }else if (binding.crPasswordET.text?.isEmpty() == true){
            binding.crPasswordET.error = "Please Enter Password"
            return false
        }else if(binding.crConfirmPasswordET.text?.isEmpty() == true){
            binding.crConfirmPasswordET.error = "Please Enter Password"
            return false
        }
        return true
    }


}