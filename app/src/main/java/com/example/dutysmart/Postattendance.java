package com.example.dutysmart;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


public class Postattendance extends Fragment {
    View view;

    EditText editText_date;
    TextView textView;
    Button button_present;
    LinearLayout checkboxContainer;
    Calendar mycalendar = Calendar.getInstance();
    List<CheckBox> checkBoxes = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_postattendance, container, false);
        editText_date = view.findViewById(R.id.post_datepicker);
        textView = view.findViewById(R.id.post_displaytext);
        button_present = view.findViewById(R.id.present);
        checkboxContainer = view.findViewById(R.id.checkbox_container);


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
                        loadTeamList();
                    }
                }, mycalendar.get(Calendar.YEAR), mycalendar.get(Calendar.MONTH), mycalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        button_present.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postAttendance();
            }
        });
        return view;
    }

    private void loadTeamList() {
        checkboxContainer.removeAllViews();
        checkBoxes.clear();


        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AssignDuty");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded() && snapshot.exists()) {
                    String selectedDate = editText_date.getText().toString();
                    if (snapshot.hasChild(selectedDate)) {
                        DataSnapshot dateSnapshot = snapshot.child(selectedDate);
                        boolean allPresent=true;
                        for (DataSnapshot nameSnapshot : dateSnapshot.getChildren()) {
                            String nameAssign = nameSnapshot.getKey();
                            if (!Objects.equals(nameSnapshot.getValue(String.class), "Present")) {
                                allPresent=false;
                                CheckBox checkBox = new CheckBox(requireContext());
                                checkBox.setText(nameAssign);
                                checkBoxes.add(checkBox);
                                checkboxContainer.addView(checkBox);
                            }
                        }
                        if (allPresent){
                           // Toast.makeText(getContext(), "Attendance for all members is complete for selected date", Toast.LENGTH_SHORT).show();
                        }else {
                            checkboxContainer.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Data loading failed" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void postAttendance() {
        String date = editText_date.getText().toString();
        if (date.isEmpty()) {
            editText_date.setError("Please Select Date");
            return;
        }

        if (checkBoxes.isEmpty()) {
            Toast.makeText(getContext(), "No Data found for selected date", Toast.LENGTH_SHORT).show();
            return;
        }


        List<String> selectedNames = new ArrayList<>();
        for (CheckBox checkBox : checkBoxes) {
            if (checkBox.isChecked()) {
                selectedNames.add(checkBox.getText().toString());
            }
        }

        if (selectedNames.isEmpty()) {
            Toast.makeText(getContext(), "Please select at least one name", Toast.LENGTH_SHORT).show();
            return;
        }
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("AssignDuty").child(date);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (String name : selectedNames) {
                    if (snapshot.child(name).exists() && Objects.equals(snapshot.child(name).getValue(String.class), "Present")) {
                        Toast.makeText(getContext(), "Attendance already posted for " + name, Toast.LENGTH_SHORT).show();
                    } else {
                        reference.child(name).setValue("Present");

                        // Remove the CheckBox of the person marked as present
                        for (CheckBox checkBox : checkBoxes) {
                            if (checkBox.getText().toString().equals(name)) {
                                checkboxContainer.removeView(checkBox);
                                checkBoxes.remove(checkBox);
                                break;

                            }
                        }

                        Toast.makeText(getContext(), "Attendance Posted Successfully", Toast.LENGTH_SHORT).show();

                        

                        reference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                boolean allPresent = true;
                                for (DataSnapshot nameSnapshot : snapshot.getChildren()) {
                                    if (!Objects.equals(nameSnapshot.getValue(String.class), "Present")) {
                                        allPresent = false;
                                        break;
                                    }
                                    

                                if (allPresent) {
                                   // Toast.makeText(getContext(), "Attendance for all members is complete for the selected date", Toast.LENGTH_SHORT).show();
                                }
                            }


                        }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                if (isAdded()) {
                                    Toast.makeText(getContext(), "Data Loading Failed: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
                    }
                }
            }




            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Data Loading Failed" + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void clearSelections() {
        for (CheckBox checkBox : checkBoxes) {
            checkBox.setChecked(false);
        }
        editText_date.getText().clear();
        checkboxContainer.setVisibility(View.GONE);
    }
    public void handlebackpressed(){
        Intent intent=new Intent(getContext(),Sidenavigatiobbar.class);
        startActivity(intent);
    }
}

