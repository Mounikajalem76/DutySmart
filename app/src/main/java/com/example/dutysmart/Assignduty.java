package com.example.dutysmart;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class Assignduty extends Fragment {
    View view;
    EditText editText_date;
    TextView textView;
    Button button_assignDuty;
    Calendar mycalendar=Calendar.getInstance();
    AutoCompleteTextView autoCompleteTextView;
    String name;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view= inflater.inflate(R.layout.fragment_assignduty, container, false);
        autoCompleteTextView= (AutoCompleteTextView) view.findViewById(R.id.namelist);
        editText_date= (EditText) view.findViewById(R.id.datepicker);
        textView= (TextView) view.findViewById(R.id.displaytext);
        button_assignDuty= (Button) view.findViewById(R.id.assign);

        List<String> teamMembers=new ArrayList<>();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Team members");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isAdded() && snapshot.exists()){
                    for (DataSnapshot degSnapshot : snapshot.getChildren()){
                        String designation=degSnapshot.getKey();
                        //Toast.makeText(getContext(), ""+designation, Toast.LENGTH_SHORT).show();
                        for (DataSnapshot namesnapshot: degSnapshot.getChildren()){
                            String name=namesnapshot.getValue(String.class);
                            //Toast.makeText(getContext(), "Name :"+name, Toast.LENGTH_SHORT).show();
                            teamMembers.add(name);
                            // Toast.makeText(getContext(), "List"+teamMembers, Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                StringBuilder builder=new StringBuilder();
                for (String names:teamMembers){
                    builder.append(names).append("\n");

                }
                String names=builder.toString().trim();

                String[] finalList=names.split("\n");

                ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1,finalList);
                autoCompleteTextView.setAdapter(arrayAdapter);
                autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        name = adapterView.getItemAtPosition(i).toString();

                    }
                });

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "Data Storing Failed", Toast.LENGTH_SHORT).show();

            }
        });
        editText_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        mycalendar.set(Calendar.YEAR,i);
                        mycalendar.set(Calendar.MONTH,i1);
                        mycalendar.set(Calendar.DAY_OF_MONTH,i2);

                        String myFormat="dd-MMM-yyyy";
                        SimpleDateFormat dateFormat=new SimpleDateFormat(myFormat, Locale.US);
                        editText_date.setText(dateFormat.format(mycalendar.getTime()));



                    }
                }, mycalendar.get(Calendar.YEAR), mycalendar.get(Calendar.MONTH), mycalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        button_assignDuty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name=autoCompleteTextView.getText().toString();
                String date=editText_date.getText().toString();
                if (name.isEmpty()){
                    autoCompleteTextView.setError("Please Select Name");
                } else if (date.isEmpty()) {
                    editText_date.setError("Please Select Date");

                }else {
                    // Toast.makeText(getContext(), "Select :"+name, Toast.LENGTH_SHORT).show();
                    // Toast.makeText(getContext(), "Select :"+date, Toast.LENGTH_SHORT).show();

                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("AssignDuty").child(date);
                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.child(name).exists()) {
                                autoCompleteTextView.getText().clear();
                                editText_date.getText().clear();

                                Toast.makeText(getContext(), "Already AssignDuty for this person on this date", Toast.LENGTH_SHORT).show();

                            }else{
                                reference.child(name).setValue("Absent");
                                autoCompleteTextView.getText().clear();
                                editText_date.getText().clear();
                                Toast.makeText(getContext(), "Duty Assign Successfully", Toast.LENGTH_SHORT).show();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            if (isAdded()) {
                                Toast.makeText(getContext(), "DataStoring Failed" + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

                }
            }

        });


        return view;
    }

}