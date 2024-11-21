package com.example.zelo.transference

import android.content.Context
import android.provider.ContactsContract
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State

class ContactsViewModel : ViewModel() {
    // State to hold the list of contacts
    private val _contacts = mutableStateOf<List<Contact>>(emptyList())
    val contacts: State<List<Contact>> = _contacts

    // Function to fetch contacts and update the state
    fun fetchContacts(context: Context) {
        _contacts.value = fetchContactsWithEmail(context)
    }

    // Function to fetch contacts from the device
    private fun fetchContactsWithEmail(context: Context): List<Contact> {
        val contacts = mutableListOf<Contact>()
        val contentResolver = context.contentResolver

        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Email.CONTENT_URI,
            arrayOf(ContactsContract.CommonDataKinds.Email.ADDRESS, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),
            null,
            null,
            null
        )

        cursor?.use {
            while (it.moveToNext()) {
                val name = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME))
                val email = it.getString(it.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Email.ADDRESS))
                contacts.add(Contact(name, email))
            }
        }

        return contacts
    }
}

// Contact data class to represent the contact's name and email
data class Contact(val name: String, val email: String?)
