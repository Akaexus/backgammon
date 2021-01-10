package com.example.backgammon

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import kotlin.reflect.KFunction1

class UserDialog : DialogFragment() {
    lateinit var callback: KFunction1<List<User>, Unit>
    lateinit var users:List<User>
    private val selectedItems = ArrayList<Int>() // Where we track the selected items
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            // Set the dialog title
            builder.setTitle("Users")
                // Specify the list array, the items to be selected by default (null for none),
                // and the listener through which to receive callbacks when items are selected
                .setMultiChoiceItems(
                    users.map { u -> u.login }.toTypedArray(), null,
                    DialogInterface.OnMultiChoiceClickListener { dialog, which, isChecked ->
                        if (isChecked) {
                            // If the user checked the item, add it to the selected items
                            selectedItems.add(which)
                        } else if (selectedItems.contains(which)) {
                            // Else, if the item is already in the array, remove it
                            selectedItems.remove(Integer.valueOf(which))
                        }
                    })
                // Set the action buttons
                .setPositiveButton("OK",
                    DialogInterface.OnClickListener { dialog, id ->
                        // User clicked OK, so save the selectedItems results somewhere
                        // or return them to the component that opened the dialog
                        var usersToReturn: MutableList<User> = arrayListOf()
                        this.selectedItems.forEach { index ->
                            usersToReturn.add(this.users[index])
                        }
                        this.callback(usersToReturn)

                    })
                .setNegativeButton("Cancel",
                    DialogInterface.OnClickListener { dialog, id ->

                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}
