package com.example.projetws.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.projetws.R;
import com.example.projetws.beans.Etudiant;
import java.util.List;

public class EtudiantAdapter extends ArrayAdapter<Etudiant> {
    private Context context;
    private List<Etudiant> etudiants;

    public EtudiantAdapter(Context context, List<Etudiant> etudiants) {
        super(context, 0, etudiants);
        this.context = context;
        this.etudiants = etudiants;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_etudiant, parent, false);
        }

        // Get the Etudiant object at the current position
        Etudiant etudiant = etudiants.get(position);

        // Find the TextViews in your list item layout and set their values
        TextView nomTextView = convertView.findViewById(R.id.nomTextView);
        TextView prenomTextView = convertView.findViewById(R.id.prenomTextView);
        TextView villeTextView = convertView.findViewById(R.id.villeTextView);

        nomTextView.setText(etudiant.getNom());
        prenomTextView.setText(etudiant.getPrenom());
        villeTextView.setText(etudiant.getVille());

        return convertView;
    }
}