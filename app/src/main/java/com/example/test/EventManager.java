package com.example.test;

import java.util.Comparator;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class EventManager {
    private static List<Event> events = new ArrayList<>();

    // Static block to populate initial sample events
    static {
        populateSampleEvents();
    }

    // Adds an event to the list
    public static void addEvent(Event event) {
        events.add(event);
        events.sort(Comparator.comparing(Event::getDate));
    }

    // Returns a new list containing all events
    public static List<Event> getEvents() {
        return new ArrayList<>(events);
    }

    // Filters and returns events for a specific date
    public static List<Event> getEventsForDate(String date) {
        return events.stream()
                .filter(event -> event.getDate().equals(date))
                .collect(Collectors.toList());
    }

    // Updates an event at a specified index
    public static void updateEvent(int index, Event updatedEvent) {
        if (index >= 0 && index < events.size()) {
            events.set(index, updatedEvent);
        }
    }

    // Deletes an event at a specified index
    public static void deleteEvent(int index) {
        if (index >= 0 && index < events.size()) {
            events.remove(index);
        }
    }

    // Populates the events list with sample events
    private static void populateSampleEvents() {
        events.add(new Event("2024-01-01", "New Year's Day", 0, "Celebration of the new year."));
        events.add(new Event("2024-02-14", "Valentine's Day", 0, "Day of love and friendship."));
        events.add(new Event("2024-03-17", "St. Patrick's Day", 0, "Irish holiday celebrated worldwide."));
    }

}
