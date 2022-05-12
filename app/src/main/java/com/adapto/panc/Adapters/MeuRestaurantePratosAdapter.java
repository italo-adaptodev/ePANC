package com.adapto.panc.Adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.adapto.panc.Activities.Restaurante.EditarPratoActivity;
import com.adapto.panc.Activities.Restaurante.Restaurante_DetalharPratoActivity;
import com.adapto.panc.Activities.Restaurante.Restaurante_DetalharRestauranteDONOActivity;
import com.adapto.panc.Activities.Utils.FirestoreReferences;
import com.adapto.panc.Models.Database.Prato;
import com.adapto.panc.Models.Database.Restaurante;
import com.adapto.panc.R;
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class MeuRestaurantePratosAdapter extends RecyclerView.Adapter<MeuRestaurantePratosAdapter.FirestoreItemPratoHolder> {
    private LayoutInflater inflater;
    Restaurante restaurante;
    private Context context;
    private boolean adm = false;
    private FirestoreReferences firestoreReferences = new FirestoreReferences();
    private FirebaseFirestore db;
    private Activity activity;
    private Handler handler = new Handler();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    int PRIVATE_MODE = 0;
    private static final String PREF_NAME = "com.adapto.panc.Activities.Restaurante.PRATOS";


    public MeuRestaurantePratosAdapter(LayoutInflater inflater, Restaurante restaurante, Context context, boolean adm, Activity activity) {
        this.inflater = inflater;
        this.restaurante = restaurante;
        this.context = context;
        this.adm = adm;
        db = FirebaseFirestore.getInstance();
        this.activity = activity;
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public MeuRestaurantePratosAdapter(LayoutInflater inflater, Restaurante restaurante, Context context) {
        this.inflater = inflater;
        this.restaurante = restaurante;
        this.context = context;
        db = FirebaseFirestore.getInstance();
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    @Override
    public FirestoreItemPratoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemMessage = inflater.inflate(R.layout.cardview_prato_restaurante, parent, false);
        FirestoreItemPratoHolder firestoreItemPratoHolder = new FirestoreItemPratoHolder(itemMessage);
        firestoreItemPratoHolder.view = itemMessage;
        firestoreItemPratoHolder.nomePrato = (TextView) itemMessage.findViewById(R.id.restaurantePratoNome);
        firestoreItemPratoHolder.precoPrato = (TextView) itemMessage.findViewById(R.id.restaurantePratoPreco);
        firestoreItemPratoHolder.imagemPrato = itemMessage.findViewById(R.id.restaurantePratoImagem);
        firestoreItemPratoHolder.ingredientesPANC = (TextView) itemMessage.findViewById(R.id.restaurantePratoIngredientesPANC);
        return firestoreItemPratoHolder;
    }

    @Override
    public void onBindViewHolder(final FirestoreItemPratoHolder holder, final int position) {
        holder.setConfigsView(context);
        Prato prato = restaurante.getPratos().get(holder.getAdapterPosition());
        holder.nomePrato.setText(prato.getNome());
        holder.precoPrato.setText("R$ " + prato.getPreco());
        holder.ingredientesPANC.setText(prato.getIngredientesPANC());
        holder.position = holder.getAdapterPosition();
        String imgID = prato.getImagensID().get(0);
        Glide.with(context)
                .load(imgID)
                .into(holder.imagemPrato);
        if (adm) {
            holder.restaurantePratoLinearLayout.setVisibility(View.VISIBLE);
            holder.btnExcluirPrato.setVisibility(View.VISIBLE);
            holder.linearLayoutCard.setVisibility(View.VISIBLE);
            holder.btnExcluirPrato.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    excluirPostagem(holder.getAdapterPosition(), holder.view, activity);
                }
            });
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, EditarPratoActivity.class);
                    intent.putExtra("restauranteID", restaurante.getId());
                    intent.putExtra("ListaPratoID", holder.getAdapterPosition());
                    activity.startActivity(intent);
                }
            });
        }else{
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, Restaurante_DetalharPratoActivity.class);
                    intent.putExtra("restauranteID", restaurante.getId());
                    editor.putString("last_restauranteID", restaurante.getId());
                    editor.commit();
                    intent.putExtra("ListaPratoID", holder.getAdapterPosition());
                    activity.startActivity(intent);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return restaurante.getPratos().size();
    }

    public static class FirestoreItemPratoHolder extends RecyclerView.ViewHolder {
        public TextView nomePrato, precoPrato, ingredientesPANC;
        public ImageView imagemPrato;
        public LinearLayout restaurantePratoLinearLayout, linearLayoutCard;
        public ImageButton btnExcluirPrato;
        View view;
        int position;

        public FirestoreItemPratoHolder(@NonNull View itemView) {
            super(itemView);
            this.view = itemView;
            nomePrato = itemView.findViewById(R.id.restaurantePratoNome);
            precoPrato = itemView.findViewById(R.id.restaurantePratoPreco);
            imagemPrato = itemView.findViewById(R.id.restaurantePratoImagem);
            ingredientesPANC = itemView.findViewById(R.id.restaurantePratoIngredientesPANC);
            restaurantePratoLinearLayout = itemView.findViewById(R.id.restaurantePratoLinearLayout);
            linearLayoutCard = itemView.findViewById(R.id.restaurantePratoLinearLayoutCard);
            btnExcluirPrato = itemView.findViewById(R.id.restaurantePratoLinearLayoutDeleteBtn);

        }

        public void setConfigsView(Context baseContext) {
            restaurantePratoLinearLayout.setVisibility(View.VISIBLE);
            RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(0, 50, 0, 0);
            linearLayoutCard.setLayoutParams(layoutParams);
        }
    }

    private void excluirPostagem(final int index, final View view, final Activity activity) {
        final DocumentReference docRef = db.collection(firestoreReferences.getRestauranteCOLLECTION()).document(restaurante.getPratos().get(0).getRestauranteID());
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        docRef.update("pratos", FieldValue.arrayRemove(((List<Prato>) documentSnapshot.get("pratos")).get(index)))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        new SnackBarPersonalizada().showMensagemLonga(view, "Prato excluído com sucesso!");
                                        handler.postDelayed(task, 3000);

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
            activity.startActivity(new Intent(context, Restaurante_DetalharRestauranteDONOActivity.class));
        }
    };
}