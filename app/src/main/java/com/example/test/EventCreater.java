package com.example.test;

import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class EventCreater extends AppCompatActivity {
    // Define UI components
    private EditText titleEditText, amountEditText, detailsEditText; // Input fields for event data
    private TextView selectedDateText; // Displays the selected date
    private CalendarView calendarView; // Allows user to pick a date
    private Button saveEventButton; // Button to save the event
    private boolean isEditing; // Flag to determine if the event is being edited
    private int eventIndex = -1; // Index of the event being edited

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addevent); // Set the content view to the add event layout

        initializeViews(); // Initialize UI components
        handleIntent(); // Handle the intent to determine if we are adding or editing an event

        // Set a listener on the CalendarView to update the selectedDateText TextView when a date is chosen
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Format the selected date and display it
            String selectedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
            selectedDateText.setText(selectedDate);
        });

        // Set a listener on the save button to trigger event saving
        saveEventButton.setOnClickListener(view -> saveEvent());
    }

    private void initializeViews() {
        // Find and initialize UI components
        selectedDateText = findViewById(R.id.currentDateTextView);
        titleEditText = findViewById(R.id.eventTitleEditText);
        amountEditText = findViewById(R.id.eventAmountEditText);
        detailsEditText = findViewById(R.id.eventDetailsEditText);
        calendarView = findViewById(R.id.calendarViewEvent);
        saveEventButton = findViewById(R.id.saveEventButton);
        Button backButton = findViewById(R.id.backButton); // Initialize the back button

        // Set a click listener on the back button to finish the activity
        backButton.setOnClickListener(view -> finish());
    }

    private void handleIntent() {
        // Determine if we are editing an event based on the intent extra
        isEditing = getIntent().getBooleanExtra("edit", false);
        if (isEditing) {
            eventIndex = getIntent().getIntExtra("index", -1);
            if (eventIndex != -1) {
                // If editing, load the event's data into the UI components
                Event event = EventManager.getEvents().get(eventIndex);
                selectedDateText.setText(event.getDate());
                titleEditText.setText(event.getTitle());
                amountEditText.setText(String.valueOf(event.getAmount()));
                detailsEditText.setText(event.getDetails());

                // Set the calendar to the date of the event being edited
                try {
                    String[] parts = event.getDate().split("-");
                    int year = Integer.parseInt(parts[0]);
                    int month = Integer.parseInt(parts[1]) - 1; // Calendar month is 0-based
                    int day = Integer.parseInt(parts[2]);
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);
                    calendarView.setDate(calendar.getTimeInMillis(), true, true);
                } catch (Exception e) {
                    Toast.makeText(this, "Error setting date", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void saveEvent() {
        // Get values from UI components
        String date = selectedDateText.getText().toString();
        String title = titleEditText.getText().toString().trim();
        String amountString = amountEditText.getText().toString().trim();
        String details = detailsEditText.getText().toString().trim();

        // Validate input: title and amount must not be empty
        if (title.isEmpty() || amountString.isEmpty()) {
            Toast.makeText(this, "Title and amount must be filled in", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double amount = Double.parseDouble(amountString); // Parse the amount input to a double
            Event event = new Event(date, title, amount, details); // Create a new Event object

            // Additional validation for date selection
            if (date.equals("Selected Date") || date.isEmpty()) {
                Toast.makeText(this, "Please select a date", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if editing or adding a new event and perform the appropriate action
            if (isEditing && eventIndex != -1) {
                EventManager.updateEvent(eventIndex, event);
                Toast.makeText(this, "Event updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                EventManager.addEvent(event);
                Toast.makeText(this, "Event added successfully", Toast.LENGTH_SHORT).show();
            }
            setResult(RESULT_OK); // Set result code
            finish(); // Finish the activity
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show();
        }
    }
}
