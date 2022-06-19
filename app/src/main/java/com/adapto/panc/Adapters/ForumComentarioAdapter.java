package com.adapto.panc.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Activities.ForumDuvida.DetalharPostagemForumActivity;
import com.adapto.panc.Activities.Restaurante.Restaurante_DetalharRestauranteDONOActivity;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.adapto.panc.Models.Database.FirestoreForumComentario;
import com.adapto.panc.Models.Database.Prato;
import com.adapto.panc.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class ForumComentarioAdapter extends RecyclerView.Adapter<ForumComentarioAdapter.FirestoreForumComentarioHolder> {
    private LayoutInflater inflater;
    private List<FirestoreForumComentario> comentarios;
    private Context context;
    private boolean adm = false;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private Activity activity;
    private FirebaseFirestore db;
    private String postagemkey;
    private Handler handler = new Handler();

    public ForumComentarioAdapter(LayoutInflater inflater, List<FirestoreForumComentario> comentarios, Context context, boolean adm, Activity activity, String postagemkey) {
        this.inflater = inflater;
        this.comentarios = comentarios;
        this.context = context;
        this.adm = adm;
        db = FirebaseFirestore.getInstance();
        this.activity = activity;
        this.postagemkey = postagemkey;
    }

    @Override
    public FirestoreForumComentarioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.cardview_resposta_forum, parent, false);
        FirestoreForumComentarioHolder firestoreForumComentarioHolder = new FirestoreForumComentarioHolder(itemMessage);
        firestoreForumComentarioHolder.view = itemMessage;
        firestoreForumComentarioHolder.comentarioForumTexto = (TextView) itemMessage.findViewById(R.id.comentarioForumTexto);
        firestoreForumComentarioHolder.comentarioForumNome = (TextView) itemMessage.findViewById(R.id.comentarioForumNome);
        return firestoreForumComentarioHolder;
    }

    @Override
    public void onBindViewHolder(final FirestoreForumComentarioHolder holder, final int position) {
        FirestoreForumComentario comentario = comentarios.get(position);
        holder.comentarioForumNome.setText(comentario.getComentarioNomeUsuario());
        holder.comentarioForumTexto.setText(comentario.getComentarioTexto());
        holder.position = position;
        if (adm) {
            holder.forumComentarioLinearLayout.setVisibility(View.VISIBLE);
            holder.btnExcluirPrato.setVisibility(View.VISIBLE);
            holder.forumComentarioLinearLayoutCard.setVisibility(View.VISIBLE);
            holder.btnExcluirPrato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    excluirComentario(position, holder.view, activity);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return comentarios.size();
    }

    public static class FirestoreForumComentarioHolder extends RecyclerView.ViewHolder {
        public TextView comentarioForumNome, comentarioForumTexto;
        public LinearLayout forumComentarioLinearLayout, forumComentarioLinearLayoutCard;
        public ImageButton btnExcluirPrato;
        View view;
        int position;

        public FirestoreForumComentarioHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            comentarioForumNome = itemView.findViewById(R.id.comentarioForumNome);
            comentarioForumTexto = itemView.findViewById(R.id.comentarioForumTexto);
            forumComentarioLinearLayout = itemView.findViewById(R.id.forumComentarioLinearLayout);
            forumComentarioLinearLayoutCard = itemView.findViewById(R.id.forumComentarioLinearLayoutCard);
            btnExcluirPrato = itemView.findViewById(R.id.forumComentarioLinearLayoutDeleteBtn);
        }
    }

    private void excluirComentario(final int index, final View view, final Activity activity) {
        final DocumentReference docRef = db.collection(firestoreReferences.getPostagensForumPANCCOLLECTION()).document(postagemkey);
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        docRef.update("comentarios", FieldValue.arrayRemove(((List<FirestoreForumComentario>) documentSnapshot.get("comentarios")).get(index)))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        handler.postDelayed(task, 10);

                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                new SnackBarPersonalizada().showMensagemLonga(view, "Não foi possível remover o prato. Tenteo novamente mais tarde.");
                            }
                        });
                    }
                });
    }

    private Runnable task = new Runnable() {
        public void run() {
            activity.finish();
            activity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            Intent intent = new Intent(context, DetalharPostagemForumActivity.class);
            intent.putExtra("postagemIDDetalhe", postagemkey);
            activity.startActivity(intent);
        }
    };
}