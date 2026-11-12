package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "calendar")
public class Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long calendarId;
	
	@Column(nullable = false)
    private String calendarName;
	
	@Column
    private String description;
	
	@OneToMany(mappedBy="calendar")
    private List<CalendarEntry> entries = new ArrayList<>();
	
	@ManyToOne
	@JoinColumn(name="account_id")
	private Account account;

	public Calendar() {
		
	}

	public long getCalendarId() {
		return calendarId;
	}


	public void setCalendarId(long calendarId) {
		this.calendarId = calendarId;
	}


	public String getCalendarName() {
		return calendarName;
	}


	public void setCalendarName(String calendarName) {
		this.calendarName = calendarName;
	}


	public String getDescription() {
		return description;
	}


	public void setDescription(String description) {
		this.description = description;
	}


	public List<CalendarEntry> getEntries() {
		return entries;
	}


	public void setEntries(List<CalendarEntry> entries) {
		this.entries = entries;
	}

	public Account getAccount() {
		return account;
	}

	public void setAccount(Account account) {
		this.account = account;
	}

	public long getAccountId() {
		return this.account.getAccountId();
	}
    
}
