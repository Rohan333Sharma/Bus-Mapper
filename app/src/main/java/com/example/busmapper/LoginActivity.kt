package com.example.busmapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import pl.droidsonroids.gif.GifDrawable
import pl.droidsonroids.gif.GifImageView

class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val firebaseAuth = FirebaseAuth.getInstance()
        val loginButton = findViewById<Button>(R.id.login_button)
        val createNewAccount = findViewById<TextView>(R.id.create_new_textView)
        val gifImageView = findViewById<GifImageView>(R.id.gifImageView)
        val gifDrawable : GifDrawable = gifImageView.drawable as GifDrawable
        gifDrawable.setSpeed(0.5f)

        createNewAccount.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
            finish()
        }

        loginButton.setOnClickListener {
            val email = findViewById<EditText>(R.id.email_editText).text.toString()
            val password = findViewById<EditText>(R.id.password_editText).text.toString()
            if(email != "" && password != "")
            {
                val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                if(isEmailValid)
                {
                    if(password.length > 6)
                    {
                        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener {
                            if (it.isSuccessful)
                            {
                                finish()
                            }
                            else
                            {
                                Toast.makeText(this,"Login Failed",Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    else
                    {
                        Toast.makeText(this,"Password too small.",Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(this,"Email not valid.",Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this,"Fields cannot be empty.",Toast.LENGTH_SHORT).show()
            }
        }
    }
}