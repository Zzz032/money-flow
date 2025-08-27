package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class EventEditer extends AppCompatActivity {
    // UI components
    private EditText titleEditText, amountEditText, detailsEditText;

    private String selectedDate = "";
    private TextView selectedDateText;
    private CalendarView calendarView;
    private Button saveButton, deleteButton, backButton;
    private int eventIndex = -1; // Index of the event being edited
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event); // Make sure this is your layout file name

        initializeViews(); // Initialize UI components
        populateFieldsFromIntent(); // Populate fields if editing an existing event
    }

    private void initializeViews() {
        // Find UI components by their ID
        titleEditText = findViewById(R.id.editTextEventTitle);
        amountEditText = findViewById(R.id.editTextEventAmount);
        detailsEditText = findViewById(R.id.editTextEventDetails);
        selectedDateText = findViewById(R.id.textViewCurrentDate);
        calendarView = findViewById(R.id.calendarViewEvent);
        saveButton = findViewById(R.id.buttonSaveEvent);
        deleteButton = findViewById(R.id.buttonDeleteEvent);
        backButton = findViewById(R.id.buttonBackEvent);

        // Set a date change listener on the calendar view
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            // Format the selected date and display it
            String selectedDate = String.format(Locale.getDefault(), "%d-%02d-%02d", year, month + 1, dayOfMonth);
            selectedDateText.setText(selectedDate);
        });

        // Set onClick listeners for buttons
        saveButton.setOnClickListener(v -> saveEvent());
        deleteButton.setOnClickListener(v -> deleteEvent());
        backButton.setOnClickListener(v -> finish()); // Simply finish the activity
    }

    private void populateFieldsFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            eventIndex = intent.getIntExtra("eventIndex", -1); // This is the index of the event to be edited
            String date = intent.getStringExtra("eventDate"); // This is the date of the event itself
            String selectedDate = intent.getStringExtra("selectedDate"); // This is the date selected by the user or the current viewing date

            // Use the received selectedDate
            if (selectedDate != null && !selectedDate.isEmpty()) {
                this.selectedDateText.setText(selectedDate); // Assume you have a TextView to display/edit the date
            } else {
                this.selectedDateText.setText(date); // Use the event's date if no selectedDate is passed
            }

            // Populate other UI components
            titleEditText.setText(intent.getStringExtra("eventTitle"));
            amountEditText.setText(String.valueOf(intent.getDoubleExtra("eventAmount", 0)));
            detailsEditText.setText(intent.getStringExtra("eventDetails"));

            // Attempt to set the calendar view to the selectedDate
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(dateFormat.parse(selectedDate != null ? selectedDate : date));
                calendarView.setDate(calendar.getTimeInMillis(), false, true);
            } catch (ParseException e) {
                Toast.makeText(this, "Error parsing date", Toast.LENGTH_SHORT).show();
            }
        }
    }



    private void saveEvent() {
        // Collect data from UI components
        String date = selectedDateText.getText().toString();
        String title = titleEditText.getText().toString().trim();
        String amountStr = amountEditText.getText().toString().trim();
        double amount = amountStr.isEmpty() ? 0 : Double.parseDouble(amountStr);
        String details = detailsEditText.getText().toString().trim();

        // Validate required fields
        if (title.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(this, "Title and amount must be filled in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create or update the event
        Event updatedEvent = new Event(date, title, amount, details);
        if (eventIndex >= 0) {
            // Update the event if editing
            EventManager.updateEvent(eventIndex, updatedEvent);
        } else {
            // Otherwise, add a new event
            EventManager.addEvent(updatedEvent);
        }
        Toast.makeText(this, "Event saved successfully", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK); // Set result OK to indicate success
        finish(); // Finish the activity
    }

    private void deleteEvent() {
        // Delete the event if valid index
        if (eventIndex >= 0) {
            EventManager.deleteEvent(eventIndex);
            Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        } else {
            Toast.makeText(this, "Error deleting event", Toast.LENGTH_SHORT).show();
        }
    }
}
