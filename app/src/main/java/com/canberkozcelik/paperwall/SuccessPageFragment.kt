package com.canberkozcelik.paperwall

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.canberkozcelik.paperwall.databinding.FragmentSuccessPageBinding
import java.util.*
import kotlin.concurrent.schedule

/**
 * Created by canberkozcelik on 2019-05-26.
 */
class SuccessPageFragment : Fragment() {

    private lateinit var binding : FragmentSuccessPageBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_success_page, container, false)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        Timer().schedule(3000) {
            fragmentManager?.popBackStack()
        }
    }
}