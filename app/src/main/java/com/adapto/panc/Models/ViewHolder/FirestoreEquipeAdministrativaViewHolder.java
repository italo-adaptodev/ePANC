package com.adapto.panc.Models.ViewHolder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.R;

public class FirestoreEquipeAdministrativaViewHolder extends RecyclerView.ViewHolder{

    public TextView equipeAdministrativaNomeIntegrante, equipeAdministrativaIndicadoPor, equipeAdministrativaCargosAdministrativos;

    public FirestoreEquipeAdministrativaViewHolder(@NonNull View itemView) {
        super(itemView);
        equipeAdministrativaNomeIntegrante = itemView.findViewById(R.id.equipeAdministrativaNomeIntegrante);
        equipeAdministrativaIndicadoPor = itemView.findViewById(R.id.equipeAdministrativaIndicadoPor);
        equipeAdministrativaCargosAdministrativos = itemView.findViewById(R.id.equipeAdministrativaCargosAdministrativos);

    }
}
