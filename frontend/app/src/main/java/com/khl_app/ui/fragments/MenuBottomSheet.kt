package com.khl_app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.khl_app.R
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.khl_app.ui.MainActivity
import com.khl_app.ui.TrackableActivity
import com.khl_app.ui.UserPageActivity

class MenuBottomSheet : BottomSheetDialogFragment() {


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_menu_bottom_sheet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val calendarButton = view.findViewById<Button>(R.id.calendar_button)
        val trackedButton = view.findViewById<Button>(R.id.tracked_button)
        val profileButton = view.findViewById<Button>(R.id.profile_button)
        val logoutButton = view.findViewById<Button>(R.id.logout_button)

        calendarButton.setOnClickListener {
            dismiss()
        }

        trackedButton.setOnClickListener {
            startActivity(Intent(requireContext(), TrackableActivity::class.java))
            dismiss()
        }

        profileButton.setOnClickListener {
            startActivity(Intent(requireContext(), UserPageActivity::class.java))
            dismiss()
        }

        logoutButton.setOnClickListener {
            dismiss()
        }
    }
}