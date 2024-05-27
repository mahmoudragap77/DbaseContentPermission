package com.training.dbasecontentpermission

import android.content.ContentResolver
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.ContactsContract
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.training.dbasecontentpermission.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding
    lateinit var dataBaseHelper: DataBaseHelper
    lateinit var contactAdapter: ArrayAdapter<String>
    lateinit var contactList: MutableList<String>

    companion object {
        private const val PERMISSIONS_REQUEST_READ_CONTACTS = 100
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dataBaseHelper = DataBaseHelper(this)
        contactList = mutableListOf()
        contactAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, contactList)
        binding.contactsListView.adapter = contactAdapter

        binding.addButton.setOnClickListener { addContact() }
        binding.deleteButton.setOnClickListener { deleteContact() }
        binding.viewButton.setOnClickListener { viewContacts() }
        binding.importButton.setOnClickListener { importContacts() }
    }

    private fun importContacts() {
        if (checkSelfPermission(android.Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                PERMISSIONS_REQUEST_READ_CONTACTS
            )

        }
        else{
            readContact()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                readContact()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun readContact() {
       contactList.clear()
        val contentResolver =contentResolver
        val cursor =contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val phoneIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
            while (cursor.moveToNext()) {
                val name = cursor.getString(nameIndex)
                val phone = cursor.getString(phoneIndex)
                contactList.add("$name - $phone")
            }
        }
        contactAdapter.notifyDataSetChanged()
    }

    private fun deleteContact() {
        val name = binding.nameEditText.text.toString()
        if (name.isNotEmpty()) {
            val db = dataBaseHelper.writableDatabase
            db.delete(DBCONSTATNT.TABLE_NAME, "${DBCONSTATNT.COLUMN_NAME} = ?", arrayOf(name))
            Toast.makeText(this, "Contact deleted successfully", Toast.LENGTH_SHORT).show()
            clearFields()
            viewContacts()
        } else {
            Toast.makeText(this, "Please enter the name", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addContact() {
        val name = binding.nameEditText.text.toString()
        val phone = binding.phoneEditText.text.toString()
        val email = binding.emailEditText.text.toString()
        if (name.isNotEmpty() && phone.isNotEmpty() && email.isNotEmpty()) {
            val db = dataBaseHelper.writableDatabase
            val values = ContentValues().apply {
                put(DBCONSTATNT.COLUMN_NAME, name)
                put(DBCONSTATNT.COLUMN_PHONE, phone)
                put(DBCONSTATNT.COLUMN_EMAIL, email)
            }
            db.insert(DBCONSTATNT.TABLE_NAME, null, values)
            Toast.makeText(this, "Contact added successfully", Toast.LENGTH_SHORT).show()
            clearFields()
            viewContacts()
        } else {
            Toast.makeText(this, "Please enter all the fields", Toast.LENGTH_SHORT).show()
        }

    }

    private fun viewContacts() {
        contactList.clear()
        val db = dataBaseHelper.readableDatabase
        val projection = arrayOf(
            DBCONSTATNT.COLUMN_NAME, DBCONSTATNT.COLUMN_PHONE, DBCONSTATNT.COLUMN_EMAIL
        )
       val cursor = db.query(
            DBCONSTATNT.TABLE_NAME,
            projection,
            null,
            null,
            null,
           null,
           null
        )
        with(cursor) {
            while (moveToNext()) {
                val name = getString(getColumnIndexOrThrow(DBCONSTATNT.COLUMN_NAME))
                val phone = getString(getColumnIndexOrThrow(DBCONSTATNT.COLUMN_PHONE))
                val email = getString(getColumnIndexOrThrow(DBCONSTATNT.COLUMN_EMAIL))
                contactList.add("$name - $phone - $email")
            }
        }
        contactAdapter.notifyDataSetChanged()

    }

    private fun clearFields() {
        binding.nameEditText.text.clear()
        binding.phoneEditText.text.clear()
        binding.emailEditText.text.clear()
    }
}