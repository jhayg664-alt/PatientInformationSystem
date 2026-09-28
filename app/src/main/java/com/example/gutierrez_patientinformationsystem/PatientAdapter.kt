package com.example.gutierrez_patientinformationsystem

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PatientAdapter(
    context: Context,
    private val mPatientList: MutableList<Patient>,
    private val onItemClick: (Patient) -> Unit
) : RecyclerView.Adapter<PatientAdapter.PatientViewHolder>() {

    private val mInflater: LayoutInflater = LayoutInflater.from(context)

    inner class PatientViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {

        val img: ImageView = itemView.findViewById(R.id.imgPatientPhoto)
        val name: TextView = itemView.findViewById(R.id.tvName)
        val id: TextView = itemView.findViewById(R.id.tvId)
        val type: TextView = itemView.findViewById(R.id.tvType)
        val reason: TextView = itemView.findViewById(R.id.tvReason)
        val dateTime: TextView = itemView.findViewById(R.id.tvDateTime)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            val position = layoutPosition
            onItemClick(mPatientList[position])
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PatientViewHolder {
        val itemView = mInflater.inflate(R.layout.item_patient, parent, false)
        return PatientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PatientViewHolder, position: Int) {
        val patient = mPatientList[position]
        holder.name.text = patient.name
        holder.id.text = "ID: ${patient.id}"
        holder.type.text = "Type: ${patient.patientType}"
        holder.reason.text = "Reason: ${patient.reasonForVisit}"
        holder.dateTime.text = "${patient.registrationDate}  ${patient.registrationTime}"

        if (!patient.imageUri.isNullOrEmpty()) {
            holder.img.setImageURI(Uri.parse(patient.imageUri))
        } else {
            holder.img.setImageResource(android.R.drawable.ic_menu_gallery)
        }
    }

    override fun getItemCount(): Int = mPatientList.size

    fun addPatient(patient: Patient) {
        mPatientList.add(patient)
        notifyItemInserted(mPatientList.size - 1)
    }
}