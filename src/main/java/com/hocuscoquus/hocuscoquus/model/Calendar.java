package com.hocuscoquus.hocuscoquus.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Represents a calendar containing a collection of calendar entries.
 */
public class Calendar {
    
    private long calendarId;
    private String calendarName;
    private String description;
    private List<CalendarEntry> entries = new ArrayList<>();

    // TODO: review/add constructor(s)
    
    public Calendar() {
    }

    /**
     * Constructs a new Calendar with a unique ID, name, and description.
     * Initializes an empty list of calendar entries.
     *
     * @param calendarId the unique ID of the calendar
     * @param calendarName the name of the calendar
     * @param description the description of the calendar
     */
    public Calendar(long calendarId, String calendarName, String description) {
        this.calendarId = calendarId;
        this.calendarName = calendarName;
        this.description = description;
    }

    /**
     * Returns the unique ID of the calendar.
     *
     * @return the calendar ID
     */
    public long getCalendarId() {
        return calendarId;
    }

    /**
     * Returns the name of the calendar.
     *
     * @return the calendar name
     */
    public String getCalendarName() {
        return calendarName;
    }

    /**
     * Returns the description of the calendar.
     *
     * @return the calendar description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the list of entries associated with the calendar.
     *
     * @return the list of calendar entries
     */
    public List<CalendarEntry> getEntries() {
        return entries;
    }

    /**
     * Updates the name of the calendar.
     *
     * @param calendarName the new name for the calendar
     */
    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

     /**
     * Updates the description of the calendar.
     *
     * @param description the new description for the calendar
     */
    public void setDescription(String description) {
        this.description = description;
    }

}
