package com.example.presentation.add_edit_customer

import android.content.Context
import android.net.Uri
import android.provider.ContactsContract

object ContactPickerUtils {
    fun getContactDetails(context: Context, uri: Uri): Pair<String, String>? {
        var name = ""
        var phone = ""
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
        )
        context.contentResolver.query(uri, projection, null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                val nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
                val phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                if (nameIndex != -1) name = cursor.getString(nameIndex)
                if (phoneIndex != -1) phone = cursor.getString(phoneIndex)
                return Pair(name, phone)
            }
        }
        return null
    }
}
