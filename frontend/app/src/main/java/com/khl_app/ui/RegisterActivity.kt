package com.khl_app.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.khl_app.R
import com.khl_app.domain.ApiClient
import com.khl_app.domain.models.LoginResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var registerBtn: Button
    private lateinit var errorMessage: TextView
    private lateinit var backToLoginBtn: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_register)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.password)
        registerBtn = findViewById(R.id.register_btn)
        errorMessage = findViewById(R.id.errorMessage)
        backToLoginBtn = findViewById(R.id.back_to_login_btn)

        registerBtn.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()
            if (username.isNotEmpty() && password.isNotEmpty()) {
                register(username, password)
            } else {
                Toast.makeText(this, "Please enter both login and password", Toast.LENGTH_SHORT)
                    .show()
            }
        }
        backToLoginBtn.setOnClickListener {
            startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
        }
    }


    private fun register(username: String, password: String) {
        val credentials = mapOf("login" to username, "password" to password)
        ApiClient.apiService.register(credentials).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    val sharedPreferences = getSharedPreferences("tokens", MODE_PRIVATE)
                    sharedPreferences.edit().putString("access_token", loginResponse?.accessToken).apply()
                    sharedPreferences.edit().putString("refresh_token", loginResponse?.refreshToken).apply()
                    startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
                    finish()
                } else {
                    onErrorOccured("Failed to register")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                onErrorOccured("Error: ${t.message}")
            }
        })
    }

    private fun onErrorOccured(errorText: String) {
        errorMessage.text = errorText
    }
}