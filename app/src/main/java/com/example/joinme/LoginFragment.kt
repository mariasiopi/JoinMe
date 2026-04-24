package com.example.joinme

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController

class LoginFragment : Fragment(R.layout.fragment_login_fragment) {

    private val activityViewModel: ActivityViewModel by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        val btn = view.findViewById<Button>(R.id.SignInBtn)
        val email = view.findViewById<EditText>(R.id.editEmail)
        val name = view.findViewById<EditText>(R.id.editName)

        btn.setOnClickListener {
            val emailInput = email.text.toString()
            val nameInput = name.text.toString()

            if (nameInput.isEmpty()) {
                name.error = "Βάλε ένα όνομα"
            }else if (emailInput.isEmpty()){
                email.error = "Βάλε ένα email"
            }else{
                activityViewModel.login(nameInput, emailInput)
            }
        }

        activityViewModel.currentId.observe(viewLifecycleOwner) { id ->
            if (id != null && id > 0) {
                // Ο NavController διαβάζει το nav_graph.xml και σε πηγαίνει στο AvailableActFragment
                findNavController().navigate(R.id.AvailableActFragment)
            }
        }
    }
}