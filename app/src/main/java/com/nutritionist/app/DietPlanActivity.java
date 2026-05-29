package com.nutritionist.app;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.List;

public class DietPlanActivity extends AppCompatActivity {

    private RecyclerView rvDietPlan;
    private View cardAddMeal, layoutEmpty;
    private TextInputEditText etMealName, etMealTime, etMealCalories, etMealNotes;
    private SwitchMaterial switchReminder;

    private DatabaseHelper db;
    private SessionManager session;
    private DietMealAdapter adapter;
    private List<DietMeal> mealList;

    private int pickedHour = -1;
    private int pickedMinute = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_plan);

        db = new DatabaseHelper(this);
        session = new SessionManager(this);

        initViews();
        loadDietPlan();
    }

    private void initViews() {
        rvDietPlan = findViewById(R.id.rvDietPlan);
        cardAddMeal = findViewById(R.id.cardAddMeal);
        layoutEmpty = findViewById(R.id.layoutEmpty);

        etMealName = findViewById(R.id.etMealName);
        etMealTime = findViewById(R.id.etMealTime);
        etMealCalories = findViewById(R.id.etMealCalories);
        etMealNotes = findViewById(R.id.etMealNotes);

        switchReminder = findViewById(R.id.switchReminder);

        // Time Picker
        etMealTime.setFocusable(false);
        etMealTime.setOnClickListener(v -> showTimePicker());

        findViewById(R.id.ivBack).setOnClickListener(v -> finish());
        findViewById(R.id.ivAddDiet).setOnClickListener(v -> toggleAddForm());

        findViewById(R.id.btnSaveMeal).setOnClickListener(v -> saveMeal());
        findViewById(R.id.btnCancelMeal).setOnClickListener(v -> {
            hideForm();
            clearForm();
        });

        rvDietPlan.setLayoutManager(new LinearLayoutManager(this));
    }

    // 🔥 Smooth Animation
    private void toggleAddForm() {
        if (cardAddMeal.getVisibility() == View.VISIBLE) {
            hideForm();
        } else {
            cardAddMeal.setAlpha(0f);
            cardAddMeal.setVisibility(View.VISIBLE);
            cardAddMeal.animate().alpha(1f).setDuration(300);
        }
    }

    private void hideForm() {
        cardAddMeal.animate().alpha(0f).setDuration(200).withEndAction(() -> {
            cardAddMeal.setVisibility(View.GONE);
        });
    }

    // ⏰ Improved Time Picker
    private void showTimePicker() {
        Calendar cal = Calendar.getInstance();

        int hour = pickedHour >= 0 ? pickedHour : cal.get(Calendar.HOUR_OF_DAY);
        int minute = pickedMinute >= 0 ? pickedMinute : cal.get(Calendar.MINUTE);

        TimePickerDialog dialog = new TimePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, h, m) -> {
                    pickedHour = h;
                    pickedMinute = m;

                    Calendar selected = Calendar.getInstance();
                    selected.set(Calendar.HOUR_OF_DAY, h);
                    selected.set(Calendar.MINUTE, m);

                    String time = android.text.format.DateFormat
                            .format("hh:mm a", selected).toString();

                    etMealTime.setText(time);
                },
                hour, minute, false
        );

        dialog.show();
    }

    private void saveMeal() {
        String name = etMealName.getText().toString().trim();
        String time = etMealTime.getText().toString().trim();
        String calStr = etMealCalories.getText().toString().trim();
        String notes = etMealNotes.getText().toString().trim();
        boolean reminder = switchReminder.isChecked();

        // ✅ Validation
        if (TextUtils.isEmpty(name)) {
            etMealName.requestFocus();
            etMealName.setError("Enter meal name");
            return;
        }

        if (pickedHour < 0) {
            etMealTime.setError("Select time");
            showTimePicker();
            return;
        }

        int calories = 0;
        try {
            calories = TextUtils.isEmpty(calStr) ? 0 : Integer.parseInt(calStr);
        } catch (Exception e) {
            etMealCalories.setError("Invalid number");
            return;
        }

        int userId = session.getUserId();

        long id = db.addMealToDiet(userId, name, time, calories, notes, reminder ? 1 : 0);

        if (id > 0) {
            if (reminder) {
                scheduleReminder(name, pickedHour, pickedMinute, (int) id);
            }

            Toast.makeText(this, "Meal added 🥗", Toast.LENGTH_SHORT).show();

            hideForm();
            clearForm();
            loadDietPlan();
        } else {
            Toast.makeText(this, "Error saving meal", Toast.LENGTH_SHORT).show();
        }
    }

    private void scheduleReminder(String mealName, int hour, int minute, int requestCode) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, ReminderReceiver.class);
        intent.putExtra("meal_name", mealName);

        PendingIntent pi = PendingIntent.getBroadcast(
                this,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, minute);
        cal.set(Calendar.SECOND, 0);

        if (cal.before(Calendar.getInstance())) {
            cal.add(Calendar.DATE, 1);
        }

        if (alarmManager != null) {
            alarmManager.setRepeating(
                    AlarmManager.RTC_WAKEUP,
                    cal.getTimeInMillis(),
                    AlarmManager.INTERVAL_DAY,
                    pi
            );
        }
    }

    private void clearForm() {
        etMealName.setText("");
        etMealTime.setText("");
        etMealCalories.setText("");
        etMealNotes.setText("");
        switchReminder.setChecked(false);

        pickedHour = -1;
        pickedMinute = -1;
    }

    private void loadDietPlan() {
        mealList = db.getDietPlanForUser(session.getUserId());

        adapter = new DietMealAdapter(mealList, (meal, pos) ->
                new AlertDialog.Builder(this)
                        .setTitle("Delete Meal")
                        .setMessage("Remove " + meal.getMealName() + "?")
                        .setPositiveButton("Delete", (d, w) -> {
                            db.deleteMeal(meal.getId());
                            adapter.removeItem(pos);
                            checkEmpty();
                        })
                        .setNegativeButton("Cancel", null)
                        .show());

        rvDietPlan.setAdapter(adapter);
        checkEmpty();
    }

    private void checkEmpty() {
        if (mealList.isEmpty()) {
            rvDietPlan.setVisibility(View.GONE);
            layoutEmpty.setVisibility(View.VISIBLE);
        } else {
            rvDietPlan.setVisibility(View.VISIBLE);
            layoutEmpty.setVisibility(View.GONE);
        }
    }
}