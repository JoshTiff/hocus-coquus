package com.hocuscoquus.hocuscoquus.entity;

import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private List<CalendarEntry> entries;
    
    
}
