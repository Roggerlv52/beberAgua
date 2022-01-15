package com.example.beberagua;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity  implements TimePickerDialog.OnTimeSetListener{
    private TextView txtminutos;
    private TimePicker timePicker;
    private int Hora, Minuto, intervalo;
    private boolean ativado = false;
    private SharedPreferences preferencia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnotifica = findViewById(R.id.btn_notifica);
        txtminutos = findViewById(R.id.editText_minu);
        timePicker = findViewById(R.id.timer_picker);

        timePicker.setIs24HourView(true);
        preferencia = getSharedPreferences("db", Context.MODE_PRIVATE);
        ativado = preferencia.getBoolean("activated", false);

            if (ativado) {
                btnotifica.setText(R.string.msg_texto);
                int color = ContextCompat.getColor(this, R.color.Minha_cor_roger);
                btnotifica.setBackgroundColor(color);

                int intervalo = preferencia.getInt("intervalo", 0);
                int hora = preferencia.getInt("hora", timePicker.getCurrentHour());
                int minuto = preferencia.getInt("minuto", timePicker.getCurrentMinute());

                txtminutos.setText(String.valueOf(intervalo));
                timePicker.setCurrentHour(hora);
                timePicker.setMinute(minuto);
                startAlarm(Calendar.getInstance());

            }

            btnotifica.setOnClickListener(v -> {

                String Strintervalo = txtminutos.getText().toString();

                if (Strintervalo.isEmpty()) {
                    Toast.makeText(this, R.string.erro_msg, Toast.LENGTH_SHORT).show();
                    return;
                }
                Hora = timePicker.getCurrentHour();
                Minuto = timePicker.getCurrentMinute();
                intervalo = Integer.parseInt(Strintervalo);

                if (!ativado) {

                    btnotifica.setText(R.string.msg_texto);
                    int color = ContextCompat.getColor(this, R.color.Minha_cor_roger);
                    btnotifica.setBackgroundColor(color);
                    ativado = true;

                    SharedPreferences.Editor editor = preferencia.edit();
                    editor.putBoolean("activated", true);
                    editor.putInt("intervalo", intervalo);
                    editor.putInt("hora", Hora);
                    editor.putInt("minuto", Minuto);
                    editor.apply();

                } else {

                    btnotifica.setText(R.string.btn_enviar);
                    int color = ContextCompat.getColor(this, R.color.purple_500);
                    btnotifica.setBackgroundColor(color);
                    ativado = false;

                    SharedPreferences.Editor editor = preferencia.edit();
                    editor.putBoolean("activated", false);
                    editor.remove("intervalo");
                    editor.remove("hora");
                    editor.remove("minuto");
                    editor.apply();
                    canselarAlarme();

                }

            });

    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        c.set(Calendar.SECOND, 0);

    }

    private void startAlarm(Calendar c) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertaReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);
    }

    private void canselarAlarme() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertaReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);

        alarmManager.cancel(pendingIntent);
    }
    private void vibrar() {
        Vibrator rr = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        rr.vibrate(5000);
    }

}