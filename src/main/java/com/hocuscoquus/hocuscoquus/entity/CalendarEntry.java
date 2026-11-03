package com.hocuscoquus.hocuscoquus.entity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "calendarEntry")
public class CalendarEntry {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long calendarEntryId;
	
	@Column(nullable = false)
    private Date dateTime;
	
	@ManyToOne
	@JoinColumn(name="calendar_id", nullable=false)
	private Calendar calendar;
	
	@ManyToMany
	@JoinTable(
			name = "calendarEntry_recipe", 
			joinColumns = @JoinColumn(name = "calendarEntryId"), 
			inverseJoinColumns = @JoinColumn(name = "recipe_id"))
	private List<Recipe> recipes = new ArrayList<>();

	public CalendarEntry() {
		
	}
	
	public long getCalendarEntryId() {
		return calendarEntryId;
	}

	public void setCalendarEntryId(long calendarEntryId) {
		this.calendarEntryId = calendarEntryId;
	}

	public Date getDateTime() {
		return dateTime;
	}

	public void setDateTime(Date dateTime) {
		this.dateTime = dateTime;
	}

	public Calendar getCalendar() {
		return calendar;
	}

	public void setCalendar(Calendar calendar) {
		this.calendar = calendar;
	}

	public List<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(List<Recipe> recipes) {
		this.recipes = recipes;
	}
	
	
}
