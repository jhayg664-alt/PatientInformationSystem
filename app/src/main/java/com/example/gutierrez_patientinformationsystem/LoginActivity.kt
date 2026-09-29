package com.example.gutierrez_patientinformationsystem

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private val personnelList = arrayOf(
        "Nurse Charlie Kirk",
        "Nurse Lebron James",
        "Clerk Michael Jordan",
        "Clerk Jhay A. Gutierrez",
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }

    // Called from android:onClick="selectAccount" on the button
    fun selectAccount(view: View) {
        val myAlertBuilder = AlertDialog.Builder(this)
        myAlertBuilder.setTitle("Select Account")
        myAlertBuilder.setItems(personnelList) { _, position ->
            val selectedName = personnelList[position]
            goToMainActivity(selectedName)
        }
        myAlertBuilder.show()
    }

    // Called from Exit button
    fun Exit(view: View) {
        finishAffinity()
    }

    private fun goToMainActivity(personnelName: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("PERSONNEL_NAME", personnelName)
        startActivity(intent)
    }
}