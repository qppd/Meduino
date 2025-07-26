package com.qppd.pilldispenserv2;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.qppd.pilldispenserv2.Libs.Firebasez.FirebaseRTDBHelper;
import com.qppd.pilldispenserv2.Classes.Schedule;
import com.qppd.pilldispenserv2.Libs.Firebasez.FirebaseAuthHelper;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashboardActivity extends AppCompatActivity {

    // Firebase helpers for each dispenser
    private FirebaseRTDBHelper<Schedule> dispenser1Helper;
    private FirebaseRTDBHelper<Schedule> dispenser2Helper;
    private FirebaseRTDBHelper<Schedule> dispenser3Helper;
    private FirebaseAuthHelper authHelper;

    // RecyclerView & Adapter for each dispenser
    private RecyclerView rv1, rv2, rv3;
    private ScheduleAdapter adapter1, adapter2, adapter3;
    private List<Schedule> schedules1 = new ArrayList<>();
    private List<Schedule> schedules2 = new ArrayList<>();
    private List<Schedule> schedules3 = new ArrayList<>();

    private TextView empty1, empty2, empty3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        // Init Firebase helpers
        dispenser1Helper = new FirebaseRTDBHelper<>("schedules/dispenser1");
        dispenser2Helper = new FirebaseRTDBHelper<>("schedules/dispenser2");
        dispenser3Helper = new FirebaseRTDBHelper<>("schedules/dispenser3");
        authHelper = new FirebaseAuthHelper();

        // Init views
        rv1 = findViewById(R.id.rvDispenser1Schedules);
        rv2 = findViewById(R.id.rvDispenser2Schedules);
        rv3 = findViewById(R.id.rvDispenser3Schedules);
        empty1 = findViewById(R.id.tvDispenser1Empty);
        empty2 = findViewById(R.id.tvDispenser2Empty);
        empty3 = findViewById(R.id.tvDispenser3Empty);

        adapter1 = new ScheduleAdapter(schedules1, 1);
        adapter2 = new ScheduleAdapter(schedules2, 2);
        adapter3 = new ScheduleAdapter(schedules3, 3);
        rv1.setLayoutManager(new LinearLayoutManager(this));
        rv2.setLayoutManager(new LinearLayoutManager(this));
        rv3.setLayoutManager(new LinearLayoutManager(this));
        rv1.setAdapter(adapter1);
        rv2.setAdapter(adapter2);
        rv3.setAdapter(adapter3);

        findViewById(R.id.btnAddSchedule1).setOnClickListener(v -> showAddScheduleDialog(1));
        findViewById(R.id.btnAddSchedule2).setOnClickListener(v -> showAddScheduleDialog(2));
        findViewById(R.id.btnAddSchedule3).setOnClickListener(v -> showAddScheduleDialog(3));

        // Load schedules for all dispensers when opening dashboard
        loadSchedulesFromFirebase(1);
        loadSchedulesFromFirebase(2);
        loadSchedulesFromFirebase(3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            authHelper.logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_reset_password) {
            if (authHelper.getCurrentUser() != null && authHelper.getCurrentUser().getEmail() != null) {
                String email = authHelper.getCurrentUser().getEmail();
                authHelper.reset(email, new FirebaseAuthHelper.ResetCallback() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Reset email sent to " + email, Toast.LENGTH_LONG).show());
                    }
                    @Override
                    public void onFailure(Exception e) {
                        runOnUiThread(() -> Toast.makeText(DashboardActivity.this, "Reset failed: " + e.getMessage(), Toast.LENGTH_LONG).show());
                    }
                });
            } else {
                Toast.makeText(this, "No user email found", Toast.LENGTH_SHORT).show();
            }
            return true;
        } else if (id == R.id.action_exit) {
            finishAffinity(); // Close all activities
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAddScheduleDialog(int dispenser) {
        // Date and Time picker dialog
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view1, hourOfDay, minute) -> {
                calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                calendar.set(Calendar.MINUTE, minute);
                String date = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);
                String time = String.format("%02d:%02d", hourOfDay, minute);
                addScheduleToFirebase(dispenser, date, time);
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
            timePickerDialog.show();
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void addScheduleToFirebase(int dispenser, String date, String time) {
        Schedule schedule = new Schedule(null, date, time);
        FirebaseRTDBHelper<Schedule> helper = getHelper(dispenser);
        helper.getRef().push().setValue(schedule)
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Schedule added", Toast.LENGTH_SHORT).show();
                loadSchedulesFromFirebase(dispenser);
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void deleteScheduleFromFirebase(int dispenser, String id) {
        FirebaseRTDBHelper<Schedule> helper = getHelper(dispenser);
        helper.getRef().child(id).removeValue()
            .addOnSuccessListener(aVoid -> {
                Toast.makeText(this, "Schedule deleted", Toast.LENGTH_SHORT).show();
                loadSchedulesFromFirebase(dispenser);
            })
            .addOnFailureListener(e -> Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void loadSchedulesFromFirebase(int dispenser) {
        FirebaseRTDBHelper<Schedule> helper = getHelper(dispenser);
        helper.getRef().get().addOnSuccessListener(dataSnapshot -> {
            List<Schedule> list = new ArrayList<>();
            for (com.google.firebase.database.DataSnapshot snap : dataSnapshot.getChildren()) {
                Schedule s = snap.getValue(Schedule.class);
                if (s != null) {
                    s.id = snap.getKey();
                    list.add(s);
                }
            }
            setSchedules(dispenser, list);
        });
    }

    private void setSchedules(int dispenser, List<Schedule> list) {
        if (dispenser == 1) {
            schedules1.clear(); schedules1.addAll(list); adapter1.notifyDataSetChanged();
            empty1.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        } else if (dispenser == 2) {
            schedules2.clear(); schedules2.addAll(list); adapter2.notifyDataSetChanged();
            empty2.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        } else {
            schedules3.clear(); schedules3.addAll(list); adapter3.notifyDataSetChanged();
            empty3.setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }

    private FirebaseRTDBHelper<Schedule> getHelper(int dispenser) {
        if (dispenser == 1) return dispenser1Helper;
        if (dispenser == 2) return dispenser2Helper;
        return dispenser3Helper;
    }

    // Adapter for schedule list
    private class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ScheduleViewHolder> {
        private final List<Schedule> schedules;
        private final int dispenser;
        ScheduleAdapter(List<Schedule> schedules, int dispenser) {
            this.schedules = schedules;
            this.dispenser = dispenser;
        }
        @NonNull
        @Override
        public ScheduleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            return new ScheduleViewHolder(view);
        }
        @Override
        public void onBindViewHolder(@NonNull ScheduleViewHolder holder, int position) {
            Schedule s = schedules.get(position);
            holder.text1.setText(s.date);
            holder.text2.setText(s.time);
            holder.itemView.setOnLongClickListener(v -> {
                deleteScheduleFromFirebase(dispenser, s.id);
                return true;
            });
        }
        @Override
        public int getItemCount() { return schedules.size(); }
        class ScheduleViewHolder extends RecyclerView.ViewHolder {
            TextView text1, text2;
            ScheduleViewHolder(View itemView) {
                super(itemView);
                text1 = itemView.findViewById(android.R.id.text1);
                text2 = itemView.findViewById(android.R.id.text2);
            }
        }
    }
}