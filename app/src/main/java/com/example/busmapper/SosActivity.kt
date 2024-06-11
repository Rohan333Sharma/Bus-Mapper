package com.example.busmapper

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Bundle
import android.provider.ContactsContract
import android.telephony.SmsManager
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat


class SosActivity : AppCompatActivity() {

    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var contactLinear : LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sos)

        val messageEditText = findViewById<EditText>(R.id.alert_message_editText)
        val saveButton = findViewById<Button>(R.id.save_button)
        val sendAlertButton = findViewById<Button>(R.id.send_alert_button)
        val addContactButton = findViewById<Button>(R.id.add_contact_button)
        val smsManager: SmsManager = this.getSystemService(SmsManager::class.java)
        contactLinear = findViewById(R.id.contacts_linear)

        sharedPreferences = this.getSharedPreferences("alert_message", MODE_PRIVATE)

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_DENIED)
        {
            val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()){

                if(!it)
                {
                    Toast.makeText(this,"Please allow permission",Toast.LENGTH_SHORT).show()
                }
            }
            locationPermissionRequest.launch(android.Manifest.permission.READ_CONTACTS)
        }
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.SEND_SMS)== PackageManager.PERMISSION_DENIED)
        {
            val locationPermissionRequest = registerForActivityResult(ActivityResultContracts.RequestPermission()){

                if(!it)
                {
                    Toast.makeText(this,"Please allow permission",Toast.LENGTH_SHORT).show()
                }
            }
            locationPermissionRequest.launch(android.Manifest.permission.SEND_SMS)
        }

//        val startActivityResult = registerForActivityResult(ActivityResultContracts.PickContact()){
//
//            val cursor:Cursor = contentResolver.query(it!!, null, null, null, null)!!
//            cursor.moveToFirst()
//            val phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
//            Log.d("Tag123",phone)
//            val name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
//            Log.d("Tag123",name)
//            cursor.close()
//
//        }

        if (!sharedPreferences.contains("message"))
        {
            val contactSet = setOf<String>()
            sharedPreferences.edit().putString("message","I am in danger. Please help me out.").putStringSet("contacts",contactSet).putStringSet("contacts_name",contactSet).apply()
        }

        messageEditText.setText(sharedPreferences.getString("message",""))

        messageEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                saveButton.visibility = View.VISIBLE
            }
            override fun afterTextChanged(s: Editable?) {
            }

        })
        saveButton.setOnClickListener {
            sharedPreferences.edit().putString("message",messageEditText.text.toString()).apply()
            saveButton.visibility = View.INVISIBLE
        }


        addContactButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK,ContactsContract.CommonDataKinds.Phone.CONTENT_URI)
            startActivityForResult(intent,2)
        }

        sendAlertButton.setOnClickListener {
            val contacts = sharedPreferences.getStringSet("contacts", setOf())!!
            var isSuccessful = true
            for (phoneNo in contacts)
            {
                try {
                    smsManager.sendTextMessage(phoneNo, null, messageEditText.text.toString(), null, null)
                }
                catch (ex: Exception) {
                    Toast.makeText(this, ex.message.toString(), Toast.LENGTH_SHORT).show()
                    isSuccessful = false
                }
            }
            if (isSuccessful)
                Toast.makeText(this, "Message Sent", Toast.LENGTH_SHORT).show()
        }
        showSavedContacts()

    }

    private fun showSavedContacts() {
        val contacts = sharedPreferences.getStringSet("contacts", setOf())!!
        val contactsNames = sharedPreferences.getStringSet("contacts_name", setOf())!!
        if(contactLinear.childCount > 0)
            contactLinear.removeAllViews()
        for (i in 0 until contacts.size)
        {
            val str = contactsNames.elementAt(i) + "\n" + contacts.elementAt(i)
            val textView = TextView(this)
            val layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT)
            textView.layoutParams = layoutParams
            textView.setPadding(10,15,10,15)
            textView.setLineSpacing(5f,1f)
            textView.textSize = 15f
            textView.text = str
            contactLinear.addView(textView)
            textView.setOnClickListener {
                val alertDialogBuilder = AlertDialog.Builder(this)
                alertDialogBuilder.setTitle("Delete Contact").setMessage((it as TextView).text)
                alertDialogBuilder.setPositiveButton("Yes") {_,_->
                    val contactSet = sharedPreferences.getStringSet("contacts", setOf()) as MutableSet<String>
                    val contactSet1 = mutableSetOf<String>()
                    for (value in contactSet)
                    {
                        if(!it.text.contains(value))
                        {
                            contactSet1.add(value)
                        }
                    }
                    val contactNameSet = sharedPreferences.getStringSet("contacts_name", setOf()) as MutableSet<String>
                    val contactNameSet1 = mutableSetOf<String>()
                    for (value in contactNameSet)
                    {
                        if(!it.text.contains(value))
                        {
                            contactNameSet1.add(value)
                        }
                    }
                    sharedPreferences.edit().putStringSet("contacts",contactSet1).apply()
                    sharedPreferences.edit().putStringSet("contacts_name",contactNameSet1).apply()

                    contactLinear.removeView(it)
                }
                val alertDialog = alertDialogBuilder.create()
                alertDialog.show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            val uri = data?.data
            val phoneNo: String
            val name: String
            val cursor: Cursor = uri?.let { contentResolver.query(it, null, null, null, null) }!!
            if (cursor.moveToFirst()) {
                val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                phoneNo = cursor.getString(phoneIndex)
                name = cursor.getString(nameIndex)
                val contactSet = sharedPreferences.getStringSet("contacts", setOf()) as MutableSet<String>
                val contactSet1 = mutableSetOf<String>()
                contactSet1.addAll(contactSet)
                contactSet1.add(phoneNo)
                val contactNameSet = sharedPreferences.getStringSet("contacts_name", setOf()) as MutableSet<String>
                val contactNameSet1 = mutableSetOf<String>()
                contactNameSet1.addAll(contactNameSet)
                contactNameSet1.add(name)
                sharedPreferences.edit().putStringSet("contacts",contactSet1).apply()
                sharedPreferences.edit().putStringSet("contacts_name",contactNameSet1).apply()
                showSavedContacts()
            }
            cursor.close()
        }
    }
}