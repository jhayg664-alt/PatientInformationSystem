package com.example.gutierrez_patientinformationsystem

    data class Patient(
        val id: String,
        val name: String,
        val age: String,
        val contactNumber: String,
        val address: String,
        val patientType: String,
        val reasonForVisit: String,
        val registrationDate: String,
        val registrationTime: String,
        val imageUri: String?
    )