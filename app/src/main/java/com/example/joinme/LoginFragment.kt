package com.example.joinme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText

class LoginFragment : Fragment() {

    private lateinit var activityViewModel: ActivityViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btn = view.findViewById<Button>(R.id.SignInBtn)
        val email = view.findViewById<EditText>(R.id.editEmail)
        val name = view.findViewById<EditText>(R.id.editName)

        val emailInput = email.text.toString()
        val nameInput = name.text.toString()

        btn.setOnClickListener {
            if (name.text.isNotEmpty()) {
                name.error = "Βάλε ένα όνομα"
            }else if (email.text.isNotEmpty()){
                email.error = "Βάλε ένα email"
            }else{
                activityViewModel.login(nameInput, emailInput)
            }
        }

        activityViewModel.currentId.observe(viewLifecycleOwner){ id ->
            if (id != null && id > 0) {
                val nextFragment = AvailableActivityFragment()

                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, nextFragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }
}