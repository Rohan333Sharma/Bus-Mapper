package com.example.busmapper

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val firebaseAuth = FirebaseAuth.getInstance()
        val fireStore = FirebaseFirestore.getInstance()
        val signUpButton = findViewById<Button>(R.id.sign_up_button)
        val alreadyHaveAccountButton = findViewById<TextView>(R.id.already_account_textView)

        alreadyHaveAccountButton.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
            finish()
        }

        signUpButton.setOnClickListener {
            val name = findViewById<EditText>(R.id.name_editText).text.toString()
            val email = findViewById<EditText>(R.id.email_editText).text.toString()
            val password = findViewById<EditText>(R.id.password_editText).text.toString()
            val rePassword = findViewById<EditText>(R.id.re_password_editText).text.toString()
            if(email != "" && password != "" && name != "" && rePassword != "")
            {
                val isEmailValid = android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
                if(isEmailValid)
                {
                    if(password.length > 6)
                    {
                        if(rePassword==password)
                        {
                            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener {
                                if (it.isSuccessful)
                                {

                                    val data = HashMap<String, String>()
                                    data["name"] = name
                                    data["favourite"] = ""
                                    fireStore.collection("user_profile").document(firebaseAuth.currentUser!!.uid).set(data).addOnSuccessListener{
                                        finish();
                                    }
                                }
                                else
                                {
                                    Toast.makeText(this,"Login Failed:"+it.exception!!.localizedMessage, Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                        else
                        {
                            Toast.makeText(this,"Re-typed password does not match", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else
                    {
                        Toast.makeText(this,"Password too small", Toast.LENGTH_SHORT).show()
                    }
                }
                else
                {
                    Toast.makeText(this,"Email not valid", Toast.LENGTH_SHORT).show()
                }
            }
            else
            {
                Toast.makeText(this,"Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
    }
}