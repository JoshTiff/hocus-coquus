package hocuscoquus.model;

import java.util.ArrayList;
import java.util.List;

public class Calendar {
    
    private int calendarId;
    private String calendarName;
    private String description;
    private List<CalendarEntry> entries;

    public Calendar(int calendarId, String calendarName, String description) {
        this.calendarId = calendarId;
        this.calendarName = calendarName;
        this.description = description;
        this.entries = new ArrayList<>();
    }

    public int getCalendarId() {
        return calendarId;
    }

    public String getCalendarName() {
        return calendarName;
    }

    public String getDescription() {
        return description;
    }

    public List<CalendarEntry> getEntries() {
        return entries;
    }

    public void setCalendarName(String calendarName) {
        this.calendarName = calendarName;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void addEntry(CalendarEntry entry) {
        entries.add(entry);
    }

    public void deleteEntry(int calendarEntryId) {
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).getCalendarEntryId() == calendarEntryId) {
                entries.remove(i);
                return;
            }
        }
    }

    public boolean downloadCalendar() {
        
        // TODO: add implementation for this method

        return false;
    }

    public boolean syncGoogleAccount(String email) {
       
        // TODO: add implementation for this method

        return false;
    }

}
