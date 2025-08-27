package com.example.test;

public class Event {
    private String date; // Event date
    private String title; // Event title
    private double amount; // Event amount, could represent cost or a numerical value associated with the event
    private String details; // Detailed description of the event

    /**
     * Constructor to initialize an Event object with date, title, amount, and details.
     *
     * @param date The date of the event.
     * @param title The title of the event.
     * @param amount The amount associated with the event. This could be a cost or any numerical value.
     * @param details A detailed description of the event.
     */
    public Event(String date, String title, double amount, String details) {
        this.date = date;
        this.title = title;
        this.amount = amount;
        this.details = details;
    }

    // Getter for the event date
    public String getDate() {
        return date;
    }

    // Getter for the event title
    public String getTitle() {
        return title;
    }

    // Getter for the event amount
    public double getAmount() {
        return amount;
    }

    // Getter for the event details
    public String getDetails() {
        return details;
    }

    /**
     * Overrides the toString method to provide a string representation of the event
     * combining the date and title.
     *
     * @return A string representation of the event, combining date and title.
     */
    @Override
    public String toString() {
        return date + " -- " + title;
    }

}
