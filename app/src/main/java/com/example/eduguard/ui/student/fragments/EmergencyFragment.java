package com.example.eduguard.ui.student.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.eduguard.R;

public class EmergencyFragment extends Fragment {

    private EditText etEmergencyMessage;
    private Button btnSend;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_emergency, container, false);

        etEmergencyMessage = view.findViewById(R.id.etEmergencyMessage);
        btnSend = view.findViewById(R.id.btnSendEmergency);

        btnSend.setOnClickListener(v -> {
            String msg = etEmergencyMessage.getText().toString().trim();
            if (TextUtils.isEmpty(msg)) {
                Toast.makeText(getContext(), "Enter message", Toast.LENGTH_SHORT).show();
                return;
            }
            // TODO: send to /requests as type=emergency
            Toast.makeText(getContext(), "Emergency sent (dummy)", Toast.LENGTH_SHORT).show();
        });

        return view;
    }
}
