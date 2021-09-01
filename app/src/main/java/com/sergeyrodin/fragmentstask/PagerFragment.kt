package com.sergeyrodin.fragmentstask

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.view.isVisible

private const val ARG_NUMBER = "fragment_number"

class PagerFragment : Fragment() {
    private var number = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            number = it.getInt(ARG_NUMBER)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pager, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val txtNumber = view.findViewById<TextView>(R.id.txt_number)
        val btnMinus = view.findViewById<Button>(R.id.btn_minus)
        val btnPlus = view.findViewById<Button>(R.id.btn_plus)

        txtNumber.text = number.toString()

        if (number == 1) {
            btnMinus.visibility = View.INVISIBLE
        }

        btnPlus.setOnClickListener {
            (requireActivity() as MainActivity).addFragment()
        }

        btnMinus.setOnClickListener {
            (requireActivity() as MainActivity).removeLastFragment()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(number: Int) =
            PagerFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_NUMBER, number)
                }
            }
    }
}