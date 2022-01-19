package com.example.beberagua;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private static final String KEY_NOTIFY = "activated";
    private static final String KEY_INTERVAL = "intervalo";
    private static final String KEY_HOUR = "hora";
    private static final String KEY_MINUTE = "minuto";

    private TextView txtminutos;
    private TimePicker timePicker;
    private boolean ativado = false;
    private Button btnotifica;
    private SharedPreferences storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        storage = getSharedPreferences("storage", Context.MODE_PRIVATE);

        btnotifica = findViewById(R.id.btn_notifica);
        txtminutos = findViewById(R.id.editText_minu);
        timePicker = findViewById(R.id.timer_picker);

        timePicker.setIs24HourView(true);
        ativado = storage.getBoolean(KEY_NOTIFY, false);

        setupUI(ativado, storage);

        btnotifica.setOnClickListener(notifyListener);

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);
    }

    private void alert(int resid) {
        Toast.makeText(MainActivity.this, resid, Toast.LENGTH_SHORT).show();
    }

    private boolean intervalisValid() {
        String Strintervalo = txtminutos.getText().toString();
        if (Strintervalo.isEmpty()) {
            alert(R.string.erro_msg);
            return false;
        }
        if (Strintervalo.equals("0")) {
            alert(R.string.Zero_zero);
            return false;
        }
        return true;
    }

    private void setupUI(boolean ativado, SharedPreferences storage) {
        if (!ativado) {
            btnotifica.setText(R.string.btn_enviar);
            btnotifica.setBackgroundResource(R.drawable.bg_button_background);
            txtminutos.setText(String.valueOf(storage.getInt(KEY_INTERVAL, 0)));
            timePicker.setCurrentHour(storage.getInt(KEY_HOUR, timePicker.getCurrentHour()));
            timePicker.setCurrentMinute(storage.getInt(KEY_MINUTE, timePicker.getCurrentMinute()));

        } else {
            btnotifica.setText(R.string.msg_texto);
            btnotifica.setBackgroundResource(R.drawable.bg_button_accent);

        }
    }

    private void updateStorage(boolean added, int intervalo, int hour, int minute) {
        SharedPreferences.Editor editor = storage.edit();
        editor.putBoolean(KEY_NOTIFY, added);

        if (added) {
            editor.putInt(KEY_INTERVAL, intervalo);
            editor.putInt(KEY_HOUR, hour);
            editor.putInt(KEY_MINUTE, minute);
        } else {
            editor.remove(KEY_INTERVAL);
            editor.remove(KEY_HOUR);
            editor.remove(KEY_MINUTE);
        }
        editor.apply();
    }

    private void setupNotification(boolean added, int intervalo, int hour, int minute) {

        Intent notificationIntent = new Intent(MainActivity.this, NotificationPublish.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        if (added) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            notificationIntent.putExtra(NotificationPublish.KEY_NOTIFICATION_ID, 1);
            notificationIntent.putExtra(NotificationPublish.KEY_NOTIFICATION, "Hora de beber agua");

            @SuppressLint("UnspecifiedImmutableFlag")
            PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0, notificationIntent,
                    PendingIntent.FLAG_CANCEL_CURRENT);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), (long) intervalo * 60 * 1000, broadcast);

        } else {

            @SuppressLint("UnspecifiedImmutableFlag")
            PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0, notificationIntent, 0);
            alarmManager.cancel(broadcast);

        }
    }

    OnClickListener notifyListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!ativado) {
                if (!intervalisValid()) return;

                int hora = timePicker.getCurrentHour();
                int minuto = timePicker.getCurrentMinute();
                int intervalo = Integer.parseInt(txtminutos.getText().toString());

                updateStorage(true, intervalo, hora, minuto);
                setupUI(true, storage);
                setupNotification(true, intervalo, hora, minuto);
                alert(R.string.notifildy);
                ativado = true;

            } else {
                updateStorage(false, 0, 0, 0);
                setupUI(false, storage);
                setupNotification(false, 0, 0, 0);
                alert(R.string.Notifica);
                ativado= false;

            }

        }
    };

}


