package com.example.busmapper

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.get
import java.util.Locale

class MoreFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?) : View? {

        val fragmentView = inflater.inflate(R.layout.fragment_more, container, false)

        var intent: Intent
        val helpTextView = fragmentView.findViewById<TextView>(R.id.help_textview)
        val feedbackTextView = fragmentView.findViewById<TextView>(R.id.feedback_textview)
        val sosTextView = fragmentView.findViewById<TextView>(R.id.sos_textview)
        val shareTextView = fragmentView.findViewById<TextView>(R.id.share_textview)
        val changeLanguageTextView = fragmentView.findViewById<TextView>(R.id.change_language_textview)


        helpTextView.setOnClickListener {
            intent = Intent(requireContext(),HelpActivity::class.java)
            startActivity(intent)
        }
        feedbackTextView.setOnClickListener {
            intent = Intent(requireContext(),FeedbackActivity::class.java)
            startActivity(intent)
        }
        sosTextView.setOnClickListener {
            intent = Intent(requireContext(),SosActivity::class.java)
            startActivity(intent)
        }
        shareTextView.setOnClickListener {
            intent = Intent(Intent.ACTION_SEND)
            intent.type = "text/plain"
            intent.putExtra(Intent.EXTRA_SUBJECT,"app")
            val sharedMessage = "I recommend this app to be a must use app... \n https://www.google.com"
            intent.putExtra(Intent.EXTRA_TEXT,sharedMessage)
            startActivity(Intent.createChooser(intent,"Choose app to share"))
        }
        changeLanguageTextView.setOnClickListener {
            val alertDialog = AlertDialog.Builder(requireContext())
            val hindiRadioButton = RadioButton(requireContext())
            val englishRadioButton = RadioButton(requireContext())
            val radioGroup = RadioGroup(requireContext())
            hindiRadioButton.text = "Hindi"
            hindiRadioButton.tag = "hi"
            englishRadioButton.text = "English"
            englishRadioButton.tag = "en"
            radioGroup.addView(hindiRadioButton)
            radioGroup.addView(englishRadioButton)
            alertDialog.setView(radioGroup)
            alertDialog.setTitle("Choose language")
            alertDialog.setPositiveButton("Change"){_,_ ->
                setLanguage(radioGroup[radioGroup.checkedRadioButtonId].tag.toString())
            }
            alertDialog.show()
        }


        // Inflate the layout for this fragment
        return fragmentView
    }

    private fun setLanguage(language:String)
    {
        val configuration = resources.configuration
        val locale = Locale(language)
        Locale.setDefault(locale)
        configuration.setLocale(locale)
        resources.updateConfiguration(configuration,resources.displayMetrics)
        requireActivity().recreate()
    }



}