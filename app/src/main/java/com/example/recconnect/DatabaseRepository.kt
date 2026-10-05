package com.example.recconnect

import android.content.ContentValues.TAG
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

/**
 * Interface containing all relevant database functions
 */
interface DatabaseRepository {
    /**
     * Attempt to add a new user account to database
     * @param name The name the user chooses for their account
     * @param email the UNIQUE email the user chooses for their account
     * @return Whether the account was successfully created
     */
    fun createAccount(name: String, email: String): Boolean

    /**
     * Change the users name
     * @param newName The new name the user chooses
     */
    fun changeAccountName(newName: String)

    /**
     * Get a list of event ID's that the user has added themselves to
     * @return List of integers of event ID's
     */
    fun getJoinedEvents():List<Int>

    /**
     * Get a list of event ID's of all currently available events
     * @return List of integers of event ID's
     */
    fun getAllEvents():List<Int>

    /**
     * Add user to an event
     * @param eventID The ID of the event the user will be added to
     */
    fun joinEvent(eventID: Int)

    /**
     * Remove the user from an event
     * @param eventID The ID of the event the user will be removed from
     */
    fun leaveEvent(eventID: Int)

}