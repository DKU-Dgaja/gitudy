package com.takseha.presentation.ui.mystudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.takseha.presentation.R

/**
 * A simple [Fragment] subclass.
 * Use the [StudyApplyMemberListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class StudyApplyMemberListFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(com.takseha.presentation.viewmodel.mystudy.ARG_PARAM1)
            param2 = it.getString(com.takseha.presentation.viewmodel.mystudy.ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_study_apply_member_list, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment StudyApplyMemberListFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            StudyApplyMemberListFragment().apply {
                arguments = Bundle().apply {
                    putString(com.takseha.presentation.viewmodel.mystudy.ARG_PARAM1, param1)
                    putString(com.takseha.presentation.viewmodel.mystudy.ARG_PARAM2, param2)
                }
            }
    }
}