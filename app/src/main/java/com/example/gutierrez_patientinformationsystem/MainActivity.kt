package com.example.gutierrez_patientinformationsystem

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.navigation.NavigationView
import java.util.Calendar

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var pageRegister: View
    private lateinit var pageRecords: View
    private lateinit var tvEmpty: TextView

    private lateinit var etId: EditText
    private lateinit var etName: EditText
    private lateinit var etAge: EditText
    private lateinit var etContact: EditText
    private lateinit var etAddress: EditText
    private lateinit var etReason: EditText
    private lateinit var spinnerType: Spinner
    private lateinit var tvSelectedDateTime: TextView
    private lateinit var imgPhoto: ImageView
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: PatientAdapter

    private var selectedDate: String = ""
    private var selectedTime: String = ""
    private var selectedImageUri: Uri? = null

    private val mPatientList = mutableListOf<Patient>()

    private val PICK_IMAGE_REQUEST = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        // Bind views
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.nav_view)
        pageRegister = findViewById(R.id.pageRegister)
        pageRecords = findViewById(R.id.pageRecords)
        tvEmpty = findViewById(R.id.tvEmpty)

        etId = findViewById(R.id.etId)
        etName = findViewById(R.id.etName)
        etAge = findViewById(R.id.etAge)
        etContact = findViewById(R.id.etContact)
        etAddress = findViewById(R.id.etAddress)
        etReason = findViewById(R.id.etReason)
        spinnerType = findViewById(R.id.spinnerPatientType)
        tvSelectedDateTime = findViewById(R.id.tvSelectedDateTime)
        imgPhoto = findViewById(R.id.imgPhoto)
        mRecyclerView = findViewById(R.id.recyclerPatients)

        // Spinner
        ArrayAdapter.createFromResource(
            this, R.array.patient_types, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinnerType.adapter = adapter
        }

        // RecyclerView
        mAdapter = PatientAdapter(this, mPatientList) { patient -> showPatientDetails(patient) }
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mRecyclerView.adapter = mAdapter

        // Navigation drawer (the toggle adds the three-line icon to the toolbar)
        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        navigationView.setNavigationItemSelectedListener(this)

        // Show who logged in
        val personnelName = intent.getStringExtra("PERSONNEL_NAME") ?: "Unknown"
        supportActionBar?.subtitle = "Logged in: $personnelName"

        showRegisterPage()
    }

    //  Pages

    private fun showRegisterPage() {
        pageRegister.visibility = View.VISIBLE
        pageRecords.visibility = View.GONE
        supportActionBar?.title = "Register Patient"
    }

    private fun showRecordsPage() {
        pageRegister.visibility = View.GONE
        pageRecords.visibility = View.VISIBLE
        supportActionBar?.title = "Patient Records"

        if (mPatientList.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
        } else {
            tvEmpty.visibility = View.GONE
        }
    }

    // Drawer menu

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_register -> showRegisterPage()
            R.id.nav_records -> showRecordsPage()
            R.id.nav_logout -> finish()   // goes back to the login screen
        }
        drawerLayout.closeDrawers()
        return true
    }

    // Options menu

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_options, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val myAlertBuilder = AlertDialog.Builder(this)
                myAlertBuilder.setTitle("About")
                myAlertBuilder.setMessage("Patient Information System\nLocal health clinic registration app.")
                myAlertBuilder.setPositiveButton("OK", null)
                myAlertBuilder.show()
                return true
            }
            R.id.action_exit -> {
                finishAffinity()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Image picker

    // Called from android:onClick="pickImage"
    fun pickImage(view: View) {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            imgPhoto.setImageURI(selectedImageUri)
        }
    }

    // Date and time pickers

    // Called from android:onClick="showDatePicker"
    fun showDatePicker(view: View) {
        val c = Calendar.getInstance()
        DatePickerDialog(this, { _, year, month, day ->
            selectedDate = "$day/${month + 1}/$year"
            updateDateTimeDisplay()
        }, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show()
    }

    // Called from android:onClick="showTimePicker"
    fun showTimePicker(view: View) {
        val c = Calendar.getInstance()
        TimePickerDialog(this, { _, hour, minute ->
            selectedTime = String.format("%02d:%02d", hour, minute)
            updateDateTimeDisplay()
        }, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show()
    }

    private fun updateDateTimeDisplay() {
        tvSelectedDateTime.text = "Date: $selectedDate   Time: $selectedTime"
    }

    // Register

    // android:onClick="registerPatient"
    fun registerPatient(view: View) {
        val id = etId.text.toString().trim()
        val name = etName.text.toString().trim()
        val age = etAge.text.toString().trim()
        val contact = etContact.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val reason = etReason.text.toString().trim()

        if (id.isEmpty()) {
            etId.error = "Patient ID is required"
            etId.requestFocus()
            return
        }

        // Check that the ID is not used yet
        for (p in mPatientList) {
            if (p.id.equals(id, ignoreCase = true)) {
                etId.error = "This Patient ID is already registered"
                etId.requestFocus()
                return
            }
        }

        if (name.isEmpty()) {
            etName.error = "Name is required"
            etName.requestFocus()
            return
        }
        if (age.isEmpty()) {
            etAge.error = "Age is required"
            etAge.requestFocus()
            return
        }
        if (age.length > 3 || age.toInt() > 120) {
            etAge.error = "Enter a valid age (0 to 120)"
            etAge.requestFocus()
            return
        }
        if (contact.isEmpty()) {
            etContact.error = "Contact number is required"
            etContact.requestFocus()
            return
        }
        if (contact.length < 7 || contact.length > 15) {
            etContact.error = "Enter a valid contact number"
            etContact.requestFocus()
            return
        }
        if (address.isEmpty()) {
            etAddress.error = "Address is required"
            etAddress.requestFocus()
            return
        }
        if (spinnerType.selectedItemPosition == 0) {
            Toast.makeText(this, "Please select a patient type.", Toast.LENGTH_SHORT).show()
            return
        }
        if (reason.isEmpty()) {
            etReason.error = "Reason for visit is required"
            etReason.requestFocus()
            return
        }
        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select the registration date.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedTime.isEmpty()) {
            Toast.makeText(this, "Please select the registration time.", Toast.LENGTH_SHORT).show()
            return
        }
        if (selectedImageUri == null) {
            Toast.makeText(this, "Please select a patient photo.", Toast.LENGTH_SHORT).show()
            return
        }

        val type = spinnerType.selectedItem.toString()
        val patient = Patient(
            id, name, age, contact, address, type, reason,
            selectedDate, selectedTime, selectedImageUri.toString()
        )
        mAdapter.addPatient(patient)
        Toast.makeText(this, "Patient registered successfully!", Toast.LENGTH_SHORT).show()
        clearForm()

        // Show the records page so the new record is visible
        showRecordsPage()
    }

    private fun clearForm() {
        etId.text.clear()
        etName.text.clear()
        etAge.text.clear()
        etContact.text.clear()
        etAddress.text.clear()
        etReason.text.clear()
        spinnerType.setSelection(0)
        selectedDate = ""
        selectedTime = ""
        selectedImageUri = null
        imgPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
        tvSelectedDateTime.text = "No date/time selected"
    }

    // Patient details (with photo)

    private fun showPatientDetails(patient: Patient) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_patient_details, null)
        val imgDetailPhoto = dialogView.findViewById<ImageView>(R.id.imgDetailPhoto)
        val tvDetailInfo = dialogView.findViewById<TextView>(R.id.tvDetailInfo)

        if (!patient.imageUri.isNullOrEmpty()) {
            imgDetailPhoto.setImageURI(Uri.parse(patient.imageUri))
        } else {
            imgDetailPhoto.setImageResource(android.R.drawable.ic_menu_gallery)
        }

        tvDetailInfo.text =
            "ID: ${patient.id}\n" +
                    "Name: ${patient.name}\n" +
                    "Age: ${patient.age}\n" +
                    "Contact: ${patient.contactNumber}\n" +
                    "Address: ${patient.address}\n" +
                    "Type: ${patient.patientType}\n" +
                    "Reason for Visit: ${patient.reasonForVisit}\n" +
                    "Date: ${patient.registrationDate}\n" +
                    "Time: ${patient.registrationTime}"

        val myAlertBuilder = AlertDialog.Builder(this)
        myAlertBuilder.setTitle("Patient Details")
        myAlertBuilder.setView(dialogView)
        myAlertBuilder.setPositiveButton("Close", null)
        myAlertBuilder.show()
    }
}