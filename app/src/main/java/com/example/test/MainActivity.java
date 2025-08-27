package com.example.test;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private ListView eventsListView; // ListView to display events
    private ArrayAdapter<Event> adapter; // Adapter for ListView to display Event objects
    private CalendarView calendarView; // CalendarView for selecting dates
    private TextView currentDateTextView; // TextView to display the current or selected date
    private String selectedDate = ""; // Variable to keep track of the selected date
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()); // Date format for displaying dates

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar); // Set the layout for the activity

        // Initializing views
        eventsListView = findViewById(R.id.eventsListView);
        calendarView = findViewById(R.id.calendarView);
        currentDateTextView = findViewById(R.id.currentdate); // Initialize TextView

        setupListView(); // Setup ListView with events
        setupActivityResultLauncher(); // Setup ActivityResultLauncher for result handling from other activities
        setupCalendarView(); // Setup CalendarView for date selection
        showAllEvents(); // Initially display all events
        updateCurrentDateTextView("All the events"); // Initially set to display all events
    }

    private void setupListView() {
        // Initialize ArrayAdapter with events and set it to the ListView
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, EventManager.getEvents());
        eventsListView.setAdapter(adapter);

        // Set click listeners for editing and long click for deleting events
        eventsListView.setOnItemClickListener((parent, view, position, id) -> editEvent(position));
        eventsListView.setOnItemLongClickListener((parent, view, position, id) -> {
            deleteEvent(position);
            return true; // Indicate that the callback consumed the long click
        });
    }

    private void setupCalendarView() {
        // Listener for date changes in CalendarView
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            String newSelectedDate = dateFormat.format(calendar.getTime());

            // If the same date is selected again, show all events, otherwise show events for the selected date
            if (newSelectedDate.equals(selectedDate)) {
                showAllEvents();
                selectedDate = ""; // Reset selected date
                updateCurrentDateTextView("All the events");
            } else {
                showEventsForSelectedDate(newSelectedDate);
                selectedDate = newSelectedDate; // Update selected date
                updateCurrentDateTextView(newSelectedDate); // Update TextView to display the selected date
            }
        });
    }

    private void showEventsForSelectedDate(String date) {
        // Filter and display events for the selected date
        List<Event> filteredEvents = EventManager.getEventsForDate(date);
        adapter.clear();
        if (filteredEvents.isEmpty()) {
            currentDateTextView.setText("No events");
        } else {
            adapter.addAll(filteredEvents);
            currentDateTextView.setText(date);
        }
        adapter.notifyDataSetChanged(); // Notify adapter to refresh the list view
    }

    private void showAllEvents() {
        // Show all events in ListView
        adapter.clear();
        adapter.addAll(EventManager.getEvents());
        adapter.notifyDataSetChanged();
    }

    private void updateCurrentDateTextView(String text) {
        // Update the TextView to display the current or selected date
        currentDateTextView.setText(text);
    }

    private void setupActivityResultLauncher() {
        // Setup ActivityResultLauncher for starting activities for result
        ActivityResultLauncher<Intent> eventActivityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        // Refresh events list if the result is OK
                        adapter.clear();
                        adapter.addAll(EventManager.getEvents());
                        adapter.notifyDataSetChanged();
                        Toast.makeText(MainActivity.this, "Event list updated", Toast.LENGTH_SHORT).show();
                    }
                });

        // Set OnClickListener for adding new event
        findViewById(R.id.addEventButton).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, EventCreater.class); // Use correct class name
            eventActivityResultLauncher.launch(intent);
        });
    }

    private void editEvent(int index) {
        List<Event> eventsToUse;
        // Check if a date has been selected to determine which list of events to use
        if (!selectedDate.isEmpty()) {
            // If a specific date is selected, use the list of events for that date
            eventsToUse = EventManager.getEventsForDate(selectedDate);
        } else {
            // Otherwise, use the list of all events
            eventsToUse = EventManager.getEvents();
        }

        // Ensure the index is within the correct range

        Event eventToEdit = eventsToUse.get(index); // Retrieve the correct event based on the index

        Intent intent = new Intent(MainActivity.this, EventEditer.class);
        // Put event details into the intent
        intent.putExtra("eventIndex", index); // Passing a unique ID is better if available
        intent.putExtra("eventDate", eventToEdit.getDate());
        intent.putExtra("eventTitle", eventToEdit.getTitle());
        intent.putExtra("eventAmount", eventToEdit.getAmount());
        intent.putExtra("eventDetails", eventToEdit.getDetails());

        startActivity(intent); // Start the EventEditor activity
    }

    private void deleteEvent(int index) {
        // Delete an event and refresh the list view
        EventManager.deleteEvent(index);
        adapter.clear();
        adapter.addAll(EventManager.getEvents());
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Event deleted", Toast.LENGTH_SHORT).show();
    }

}
