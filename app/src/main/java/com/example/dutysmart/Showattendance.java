package com.example.dutysmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Showattendance extends Fragment {

    View view;
    EditText editText_date;
    Button button_show;
    TextView textView_serial, textView_name, textView_date, textView_attendance;
    Calendar mycalendar = Calendar.getInstance();
    LinearLayout layout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_showattendance, container, false);
        editText_date = (EditText) view.findViewById(R.id.show_datepicker);
        button_show = (Button) view.findViewById(R.id.show);
        textView_serial = (TextView) view.findViewById(R.id.serialnum);
        textView_name = (TextView) view.findViewById(R.id.name);
        textView_serial = (TextView) view.findViewById(R.id.serialnum);
        textView_serial = (TextView) view.findViewById(R.id.serialnum);
        textView_date = (TextView) view.findViewById(R.id.date);
        textView_attendance = (TextView) view.findViewById(R.id.attendance);
        layout = (LinearLayout) view.findViewById(R.id.layout_data);


        editText_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        mycalendar.set(Calendar.YEAR, i);
                        mycalendar.set(Calendar.MONTH, i1);
                        mycalendar.set(Calendar.DAY_OF_MONTH, i2);

                        String myFormat = "dd-MMM-yyyy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(myFormat, Locale.US);
                        editText_date.setText(dateFormat.format(mycalendar.getTime()));
                    }
                }, mycalendar.get(Calendar.YEAR), mycalendar.get(Calendar.MONTH), mycalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        button_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                textView_name.setText("");
                textView_date.setText("");
                textView_attendance.setText("");
                textView_serial.setText("");
                String date = editText_date.getText().toString();
                if (date.isEmpty()) {
                    editText_date.setError("Please Select Date");
                } else {
                    DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AssignDuty").child(date);

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (isAdded()) {
                                if (snapshot.exists()) {
                                    layout.setVisibility(View.VISIBLE);
                                    int i = 1;
                                    for (DataSnapshot dateSnapshot : snapshot.getChildren()) {
                                        String datePicking = dateSnapshot.getKey();
                                        String valuePicking = (String) dateSnapshot.getValue();
                                        textView_serial.append(i + "\n");
                                        textView_name.append(datePicking + "\n");
                                        textView_date.append(date + "\n");
                                        textView_attendance.append(valuePicking + "\n");


                                        if ("Present".equals(valuePicking)) {
                                            // textView_name.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                            textView_attendance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                                            //textView_name.setTextColor(getResources().getColor(android.R.color.holo_green_light));
                                        } else if ("Absent".equalsIgnoreCase(valuePicking)) {
                                           textView_attendance.setTextColor(getResources().getColor(android.R.color.holo_red_light));
                                            // textView_name.setTextColor(getResources().getColor(android.R.color.holo_red_light));

                                        }
                                        //Toast.makeText(getContext(), ""+datePicking, Toast.LENGTH_SHORT).show();
                                        i++;

                                    }


                                } else {
                                    Toast.makeText(getContext(), "Date not Found", Toast.LENGTH_SHORT).show();
                                    layout.setVisibility(View.GONE);
                                }
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "Data Retrieval Failed" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }

            //Toast.makeText(getContext(), " Attendance Data Display", Toast.LENGTH_SHORT).show();
        });

        return view;
    }

    public void handlebackpressed() {
        Intent intent = new Intent(getContext(), Sidenavigatiobbar.class);
        startActivity(intent);
    }
}