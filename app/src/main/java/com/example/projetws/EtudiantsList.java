package com.example.projetws;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.projetws.adapter.EtudiantAdapter;
import com.example.projetws.beans.Etudiant;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EtudiantsList extends AppCompatActivity {
    private ListView studentListView;
    private EtudiantAdapter studentAdapter;
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etudiants_list);

        studentListView = findViewById(R.id.studentListView);

        fetchStudentsFromServer();
    }

    private void fetchStudentsFromServer() {
        String fetchUrl = "http://10.0.2.2/projet/ws/loadEtudiant.php";
        requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, fetchUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                List<Etudiant> students = parseStudentData(response);

                studentAdapter = new EtudiantAdapter(EtudiantsList.this, students);
                studentListView.setAdapter(studentAdapter);

                studentListView.setOnItemClickListener((parent, view, position, id) -> {
                    showActionDialog(students.get(position));
                });
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Handle error here
            }
        });

        requestQueue.add(request);
    }

    private List<Etudiant> parseStudentData(String response) {
        List<Etudiant> students = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(response);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int id = jsonObject.getInt("id");
                String nom = jsonObject.getString("nom");
                String prenom = jsonObject.getString("prenom");
                String ville = jsonObject.getString("ville");
                String sexe = jsonObject.getString("sexe");

                Etudiant student = new Etudiant(id, nom, prenom, ville, sexe);
                students.add(student);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return students;
    }

    private void showActionDialog(final Etudiant student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose an action")
                .setItems(new CharSequence[]{"Update", "Delete"}, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            showUpdateDialog(student);
                        } else {
                            showDeleteConfirmationDialog(student.getId());
                        }
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showDeleteConfirmationDialog(final int studentId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you want to delete this student?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    deleteStudent(studentId);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                    dialog.dismiss();
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteStudent(int studentId) {
        String deleteUrl = "http://10.0.2.2/projet/controller/deleteEtudiant.php?id=" + studentId;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.GET, deleteUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(EtudiantsList.this, "Student deleted successfully", Toast.LENGTH_SHORT).show();

                fetchStudentsFromServer();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EtudiantsList.this, "Error deleting student", Toast.LENGTH_SHORT).show();
            }
        });

        requestQueue.add(request);
    }

    private void showUpdateDialog(final Etudiant student) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Update Student Information");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        final EditText nomInput = new EditText(this);
        nomInput.setHint("Last Name");
        nomInput.setText(student.getNom());
        layout.addView(nomInput);

        final EditText prenomInput = new EditText(this);
        prenomInput.setHint("First Name");
        prenomInput.setText(student.getPrenom());
        layout.addView(prenomInput);

        final Spinner villeSpinner = new Spinner(this);
        ArrayAdapter<CharSequence> adapterV = ArrayAdapter.createFromResource(this, R.array.villes, android.R.layout.simple_spinner_item);
        adapterV.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        villeSpinner.setAdapter(adapterV);
        villeSpinner.setSelection(adapterV.getPosition(student.getVille()));
        layout.addView(villeSpinner);

        final RadioGroup sexeRadioGroup = new RadioGroup(this);

        RadioButton hommeRadio = new RadioButton(this);
        hommeRadio.setText("Male");
        hommeRadio.setId(View.generateViewId());
        sexeRadioGroup.addView(hommeRadio);

        RadioButton femmeRadio = new RadioButton(this);
        femmeRadio.setText("Female");
        femmeRadio.setId(View.generateViewId());
        sexeRadioGroup.addView(femmeRadio);

        if (student.getSexe().equals("Male")) {
            hommeRadio.setChecked(true);
        } else {
            femmeRadio.setChecked(true);
        }

        layout.addView(sexeRadioGroup);

        builder.setView(layout);

        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newNom = nomInput.getText().toString();
                String newPrenom = prenomInput.getText().toString();
                String newVille = villeSpinner.getSelectedItem().toString();
                String newSexe = (hommeRadio.isChecked()) ? "Male" : "Female";

                student.setNom(newNom);
                student.setPrenom(newPrenom);
                student.setVille(newVille);
                student.setSexe(newSexe);

                sendUpdateRequest(student);

                studentAdapter.notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void sendUpdateRequest(final Etudiant student) {
        String updateUrl = "http://10.0.2.2/projet/controller/updateEtudiant.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, updateUrl, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(EtudiantsList.this, "Student updated successfully", Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(EtudiantsList.this, "Error updating student", Toast.LENGTH_SHORT).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("id", String.valueOf(student.getId()));
                params.put("nom", student.getNom());
                params.put("prenom", student.getPrenom());
                params.put("ville", student.getVille());
                params.put("sexe", student.getSexe());
                return params;
            }
        };

        requestQueue.add(request);
    }


}