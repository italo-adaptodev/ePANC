package com.adapto.panc.Models.ViewHolder;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.R;

public class FirestoreConviteEquipeAdministrativaViewHolder extends RecyclerView.ViewHolder{

    public TextView conviteNomeConvidado, conviteIndicadoPor, conviteEquipeAdministrativaCargosAdministrativos, conviteJustificativa;
    public ImageButton aceitarconvite, negarconvite;

    public FirestoreConviteEquipeAdministrativaViewHolder(@NonNull View itemView) {
        super(itemView);
        conviteNomeConvidado = itemView.findViewById(R.id.conviteEquipeAdministrativaNomeIntegrante);
        conviteIndicadoPor = itemView.findViewById(R.id.conviteEquipeAdministrativaIndicadoPor);
        conviteEquipeAdministrativaCargosAdministrativos = itemView.findViewById(R.id.conviteEquipeAdministrativaCargosAdministrativos);
        conviteJustificativa = itemView.findViewById(R.id.conviteEquipeAdministrativaJustificativa);
        aceitarconvite = itemView.findViewById(R.id.aceitarconvite);
        negarconvite = itemView.findViewById(R.id.negarconvite);
    }
}
