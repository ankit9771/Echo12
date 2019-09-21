package com.example.rmpiv.echo.fragments


import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rmpiv.echo.R


/**
 * A simple [Fragment] subclass.
 */
class AboutUsFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        activity.title = "About Us"
        return inflater!!.inflate(R.layout.fragment_about_us, container, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }


    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }



    override fun onAttach(activity: Activity?) {
        super.onAttach(activity)
    }

}// Required empty public constructor
