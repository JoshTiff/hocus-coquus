package hocuscoquus.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a calendar containing a collection of calendar entries.
 */
public class Calendar {
    
    private int calendarId;
    private String calendarName;
    private String description;
    private List<CalendarEntry> entries;

    /**
     * Constructs a new Calendar with a unique ID, name, and description.
     * Initializes an empty list of calendar entries.
     *
     * @param calendarId the unique ID of the calendar
     * @param calendarName the name of the calendar
     * @param description the description of the calendar
     */
    public Calendar(int calendarId, String calendarName, String description) {
        this.calendarId = calendarId;
        this.calendarName = calendarName;
        this.description = description;
        this.entries = new ArrayList<>();
    }

    /**
     * Returns the unique ID of the calendar.
     *
     * @return the calendar ID
     */
    public int getCalendarId() {
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

     /**
     * Adds a calendar entry to the calendar.
     *
     * @param entry the calendar entry to add
     */
    public void addEntry(CalendarEntry entry) {
        entries.add(entry);
    }

    /**
     * Removes the calendar entry with the matching unique ID.
     * If no entry with the given ID exists, the list remains unchanged.
     *
     * @param calendarEntryId the ID of the calendar entry to remove
     */
    public void deleteEntry(int calendarEntryId) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getCalendarEntryId() == calendarEntryId) {
                entries.remove(i);
                return;
            }
        }
    }

    /**
     * Downloads the calendar.
     *
     * @return true when calendar download is successful, false otherwise
     */
    public boolean downloadCalendar() {
        
        // TODO: add implementation for this method

        return false;
    }

    /**
     * Synchronizes the calendar with the specified Google account.
     *
     * @param email the email address of the Google account to synchronize with
     * @return true when sync with Google Account is successful, false otherwise
     */
    public boolean syncGoogleAccount(String email) {
       
        // TODO: add implementation for this method

        return false;
    }

}
