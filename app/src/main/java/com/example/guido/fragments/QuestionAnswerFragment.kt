package com.example.guido.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.example.guido.Constants
import com.example.guido.QuizQuestionsActivity
import com.example.guido.R
import com.example.guido.R.layout.fragment_question_answer


/**
 * A simple [Fragment] subclass.
 * Use the [QuestionAnswerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class QuestionAnswerFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view: View = inflater.inflate(fragment_question_answer,container,false)

        val btnStart = view.findViewById<Button>(R.id.btn_start)
        val name = view.findViewById<androidx.appcompat.widget.AppCompatEditText>(R.id.et_name)
        btnStart.setOnClickListener{ view ->
            if (name.text.toString().isEmpty()){
                Toast.makeText(activity,"Please enter your name", Toast.LENGTH_SHORT).show()
            }else{
                val intent = Intent(activity,QuizQuestionsActivity::class.java)
                intent.putExtra(Constants.USER_NAME, name.text.toString())
                startActivity(intent)
                activity?.finish()
            }
        }
        // Inflate the layout for this fragment
        return view
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment.
         *
         * @return A new instance of fragment QuestionAnswerFragment.
         */
        @JvmStatic
        fun newInstance() =
            QuestionAnswerFragment().apply {
            }
    }
}