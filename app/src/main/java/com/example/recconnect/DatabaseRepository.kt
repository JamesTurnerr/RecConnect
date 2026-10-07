package com.example.recconnect

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
     * Change the users name and or email. If no/empty parameters are entered no database read will take place
     * @param newName (OPTIONAL) The new name the user chooses
     * @param newEmail (OPTIONAL) The new email the user choose
     */
    fun changeAccountDetails(newName: String? = null, newEmail: String? = null)

    /**
     * Get a list of event ID's that the user has been added to
     * @param userID The ID of the user whose events we want to get
     * @return List of joined event ID's
     */
    suspend fun getJoinedEventIDs(userID: String):List<String>

    /**
     * Get a list of event instances of all currently available events
     * @return List of all event instances
     */
    suspend fun getAllEvents():List<EventData>

    /**
     * Create a new event
     * @param title The name of the event
     * @param type The type of event/event category
     * @param location The location of the event
     * @param timestamp The date/time of the event in timestamp format
     * @param description A description of the event/any additional notes
     * @param skillLevel The level of skill the event will need
     * @param maxSpots The maximum amount of users that can attend the event
     */
    fun createEvent(title: String,
                    type: String,
                    location: String,
                    timestamp: Long,
                    description: String,
                    skillLevel: String,
                    maxSpots: String
    )

    /**
     * Add user to an event
     * @param eventID The ID of the event the user will be added to
     */
    fun joinEvent(eventID: String)

    /**
     * Remove the user from an event
     * @param eventID The ID of the event the user will be removed from
     */
    fun leaveEvent(eventID: String)

    /**
     *  Get a users details from their ID
     *  @param userID The targeted users ID
     *  @return UserData class containing username,  email and joined events
     */
    suspend fun getUserById(userID: String): UserData

    /**
     *  Get an events details from its ID
     *  @param eventID The targeted events ID
     *  @return EventData class containing all event details
     */
    suspend fun getEventById(eventID: String): EventData

}