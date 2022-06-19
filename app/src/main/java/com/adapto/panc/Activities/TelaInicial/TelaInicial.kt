package com.adapto.panc.Activities.TelaInicial

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.adapto.panc.Activities.BibliotecaPANC.ListarItensBibliotecaPANCActivity
import com.adapto.panc.Activities.ConvidarActivity
import com.adapto.panc.Activities.ForumDuvida.ListarPostagemForumDuvidaActivity
import com.adapto.panc.Activities.ForumReceita.ListarReceitasActivity
import com.adapto.panc.Activities.ListarEquipeActivity
import com.adapto.panc.Activities.LoginActivity
import com.adapto.panc.Activities.PainelAdministrativoActivity
import com.adapto.panc.Activities.Produto.Produtor_ListarProdutosActivity
import com.adapto.panc.Activities.Restaurante.Restaurante_DetalharRestauranteDONOActivity
import com.adapto.panc.Activities.Restaurante.Restaurante_ListarRestaurantesActivity
import com.adapto.panc.Activities.TelaInicial.Adapters.*
import com.adapto.panc.Activities.Utils.FirestoreReferences
import com.adapto.panc.Activities.Utils.SnackBarPersonalizada
import com.adapto.panc.Activities.Utils.WebViewConfig
import com.adapto.panc.Models.Database.*
import com.adapto.panc.R
import com.adapto.panc.Repository.LoginSharedPreferences
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.*

class TelaInicial : AppCompatActivity() {

    private var isUsuarioRestaurante: Boolean = false
    private var isUsuarioAdminstrador: Boolean = false
    private var usuarioID: String = ""
    private lateinit var recyclerViewDuvida: RecyclerView
    private lateinit var recyclerViewProdutos: RecyclerView
    private lateinit var recyclerViewReceitas: RecyclerView
    private lateinit var recyclerViewBiblioteca: RecyclerView
    private lateinit var recyclerViewRestaurantes: RecyclerView
    private lateinit var postagensForumList: ArrayList<PostagemForumDuvidas>
    private lateinit var produtosList: ArrayList<Produtor_Produto>
    private lateinit var receitasList:ArrayList<Receita>
    private lateinit var bibliotecaItemList: ArrayList<ItemBiblioteca>
    private lateinit var restaurantesList:ArrayList<Restaurante>
    private lateinit var postagensDuvidaAdapter: PostagensDuvidaAdapter
    private lateinit var produtosAdapter: ProdutosAdapter
    private lateinit var receitaAdapter: ReceitaAdapter
    private lateinit var bibliotecaAdapter: BibliotecaAdapter
    private lateinit var restauranteAdapter: RestauranteAdapter
    private lateinit var txtEmptyDuvida : TextView
    private lateinit var txtEmptyProduto : TextView
    private lateinit var txtEmptyRestaurante : TextView
    private lateinit var txtEmptyReceita : TextView
    private lateinit var txtEmptybiblioteca : TextView
    private var db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private lateinit var v : View
    private lateinit var textViewForumDuvida: TextView; lateinit var textViewProdutos: TextView;lateinit var textViewReceita:TextView
    private lateinit var textViewRestaurante:TextView;lateinit var textViewBiblioteca:TextView
    private var firestoreReferences = FirestoreReferences()
    private lateinit var toolbar: Toolbar
    private lateinit var bottom_app_bar_telainicial: Toolbar
    private val URL : String = "https://firebasestorage.googleapis.com/v0/b/panc-b9f7b.appspot.com/o/TCLE%20atualizado%20mar%C3%A7o%202022.pdf?alt=media&token=26be00fb-bbd2-47bb-aae5-8ee8e7181162"
    private lateinit var alertDialog: AlertDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tela_inicial2)
        v = findViewById(android.R.id.content)
        bottom_app_bar_telainicial = findViewById(R.id.bottom_app_bar_telainicial)
        setSupportActionBar(bottom_app_bar_telainicial)
        usuarioID = LoginSharedPreferences(this).identifier
        if (usuarioID.isEmpty()) {
            SnackBarPersonalizada().showMensagemLonga(v,
                "Não foi possível encontrar os dados do usuário. Você será redirecionado a tela de login")
            LoginSharedPreferences(baseContext).logoutUser()
        }
        isUsuarioAtivo()
        //region TextViews
        textViewReceita = findViewById(R.id.textViewReceitas)
        textViewReceita.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ListarReceitasActivity::class.java))

        })

        textViewBiblioteca = findViewById(R.id.textViewBiblioteca)
        textViewBiblioteca.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ListarItensBibliotecaPANCActivity::class.java))
        })

        textViewProdutos = findViewById(R.id.textViewProdutos)
        textViewProdutos.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Produtor_ListarProdutosActivity::class.java))
        })

        textViewRestaurante = findViewById(R.id.textViewRestaurantes)
        textViewRestaurante.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, Restaurante_ListarRestaurantesActivity::class.java))
        })

        textViewForumDuvida = findViewById(R.id.textViewForum)
        textViewForumDuvida.setOnClickListener(View.OnClickListener {
            startActivity(Intent(this, ListarPostagemForumDuvidaActivity::class.java))
        })
        //endregion

        //region Toolbar
        toolbar = findViewById(R.id.toolbarTelaInicial)
        toolbar.title = "Tela Inicial"
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.colorAccent))
        //endregion

        //region RecyclerViews
        recyclerViewDuvida = findViewById(R.id.telainicialRVDuvidas)
        recyclerViewDuvida.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewDuvida.setHasFixedSize(true)

        recyclerViewProdutos = findViewById(R.id.telainicialRVProdutos)
        recyclerViewProdutos.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewProdutos.setHasFixedSize(true)

        recyclerViewReceitas = findViewById(R.id.telainicialRVReceita)
        recyclerViewReceitas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewReceitas.setHasFixedSize(true)

        recyclerViewBiblioteca = findViewById(R.id.telainicialRVBiblioteca)
        recyclerViewBiblioteca.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewBiblioteca.setHasFixedSize(true)

        recyclerViewRestaurantes = findViewById(R.id.telainicialRVRestaurante)
        recyclerViewRestaurantes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewRestaurantes.setHasFixedSize(true)

        postagensForumList = arrayListOf()
        produtosList = arrayListOf()
        receitasList = arrayListOf()
        bibliotecaItemList = arrayListOf()
        restaurantesList = arrayListOf()

        EventChangeListener()

        postagensDuvidaAdapter = PostagensDuvidaAdapter(postagensForumList, this)
        produtosAdapter = ProdutosAdapter(produtosList, this)
        receitaAdapter= ReceitaAdapter(receitasList, this)
        bibliotecaAdapter = BibliotecaAdapter(bibliotecaItemList, this)
        restauranteAdapter= RestauranteAdapter(restaurantesList, this)

        recyclerViewDuvida.adapter = postagensDuvidaAdapter
        recyclerViewProdutos.adapter = produtosAdapter
        recyclerViewReceitas.adapter = receitaAdapter
        recyclerViewBiblioteca.adapter = bibliotecaAdapter
        recyclerViewRestaurantes.adapter = restauranteAdapter


        txtEmptyDuvida = findViewById(R.id.emptyDuvida)
        txtEmptyRestaurante = findViewById(R.id.emptyRestaurante)
        txtEmptyProduto = findViewById(R.id.emptyProduto)
        txtEmptybiblioteca = findViewById(R.id.emptyBiblioteca)
        txtEmptyReceita = findViewById(R.id.emptyReceita)
        //endregion

    }

    //region BottomAppBar

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId){
            R.id.equipe -> startActivity(Intent(this, ListarEquipeActivity::class.java))
            R.id.exit -> {
                LoginSharedPreferences(this).logoutUser()
                startActivity(Intent(this, LoginActivity::class.java))
            }
            R.id.listarprodutos -> startActivity(Intent(this, Produtor_ListarProdutosActivity::class.java))
            R.id.convite -> startActivity(Intent(this, ConvidarActivity::class.java))
            R.id.meurestaurante -> startActivity(Intent(this, Restaurante_DetalharRestauranteDONOActivity::class.java))
            R.id.listarestaurantes -> startActivity(Intent(this, Restaurante_ListarRestaurantesActivity::class.java))
            R.id.listareceitas -> startActivity(Intent(this, ListarReceitasActivity::class.java))
            R.id.paineladministrativo -> startActivity(Intent(this, PainelAdministrativoActivity::class.java))
            R.id.biblioteca -> startActivity(Intent(this, ListarItensBibliotecaPANCActivity::class.java))
            R.id.home -> startActivity(Intent(this, TelaInicial::class.java))
        }
        return super.onOptionsItemSelected(item)
    }

    private fun getPermissaoRestaurante(): kotlin.Boolean{
        db.collection(firestoreReferences.restauranteCOLLECTION)
            .whereEqualTo("usuarioID", LoginSharedPreferences(this).identifier)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                if (queryDocumentSnapshots.size() > 0) {
                    isUsuarioRestaurante = true
                }
            }
        return isUsuarioRestaurante
    }

    private fun getCargosUsuarioLogado(): Boolean {
        db.collection(firestoreReferences.equipeCOLLECTION)
            .whereEqualTo("usuarioID", usuarioID)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                if (queryDocumentSnapshots.size() > 0) {
                    for (snap in queryDocumentSnapshots) {
                        val cargos = snap["cargosAdministrativos"].toString()
                        if (cargos.contains("ADMINISTRADOR")) {
                            isUsuarioAdminstrador = true
                        }
                    }
                }
            }
        return isUsuarioAdminstrador
    }

    private fun isUsuarioAtivo() {
        db.collection(firestoreReferences.usuariosCOLLECTION)
            .whereEqualTo("identificador", usuarioID)
            .get()
            .addOnSuccessListener { queryDocumentSnapshots ->
                val size = queryDocumentSnapshots.size()
                if (size <= 0) {
                    SnackBarPersonalizada()
                        .showMensagemLongaClose(v,
                            "Não foi possível encontrar os dados do usuário.",
                            applicationContext)
                    LoginSharedPreferences(baseContext).logoutUser()

                } else {
                    checkLeituraDocs(queryDocumentSnapshots.documents[0].toObject(Usuario::class.java))
                    getCargosUsuarioLogado()
                    getPermissaoRestaurante()
                }
            }
    }

    private fun checkLeituraDocs(usuario: Usuario?) {
        if(!usuario?.isDocLido!!)
            showAlerta()
    }

    private fun showAlerta() {
        alertDialog = MaterialAlertDialogBuilder(this)
            .setTitle("ATENÇÃO!")
            .setMessage("Você agora será redirecionado para a leitura do TERMO DE CONSENTIMENTO LIVRE E ESCLARECIDO. " +
                    "Por favor, leia com atenção. " +
                    "\n\nPARA TER ACESSO AO APLICATIVO, É NECESSÁRIO ACEITAR O TCLE. \n\n" +
                    "Ao realizar a leitura do termo de consentimento livre e esclarecido(TCLE), " +
                    "você concorda com os termos apresentados no documento automaticamente. " +
                    "\n\nPara ler, clique em 'Aceito', caso contrário, você será redirecionado(a) para a tela de login ")
            .setCancelable(false)
            .setPositiveButton("Aceito"){dialogInterface, which ->
                SnackBarPersonalizada().showMensagemLonga(v, "Aceito")
                updateLeitura(usuarioID, true)
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(URL))
                startActivity(browserIntent)
            }
            .setNegativeButton("Não Aceito"){dialogInterface, which ->
                SnackBarPersonalizada().showMensagemLonga(v, " Não Aceito")
                updateLeitura(usuarioID, false)
                LoginSharedPreferences(this).logoutUser()
            }
            .show()
    }

    private fun updateLeitura(usuarioID: String, status:Boolean) {
        db.collection(firestoreReferences.usuariosCOLLECTION)
            .whereEqualTo("identificador", usuarioID)
            .get()
            .addOnSuccessListener(OnSuccessListener { tResult ->
                tResult.documents[0].reference.update("docLido", status)
            })
    }

    override fun onMenuOpened(featureId: Int, menu: Menu): Boolean {
        if (isUsuarioAdminstrador) {
            menu.getItem(7).isVisible = true
        }
        if (isUsuarioRestaurante) menu.getItem(4).isVisible = true

        return super.onMenuOpened(featureId, menu)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    //endregion

   /* override fun onStart() {
        clearAllLists()
        EventChangeListener()
        super.onStart()
    }*/

    private fun clearAllLists() {
        postagensForumList.clear()
        produtosList.clear()
        restaurantesList.clear()
        bibliotecaItemList.clear()
        receitasList.clear()
    }

    private fun EventChangeListener() {
        db.collection(firestoreReferences.postagensForumPANCCOLLECTION)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null) run {
                        val snackBarPersonalizada: SnackBarPersonalizada? = null
                        snackBarPersonalizada?.showMensagemLonga(v,  "Não foi possível carregar as informações")
                        return
                    }

                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED)
                            postagensForumList.add(dc.document.toObject(PostagemForumDuvidas::class.java))
                    }
                    if(postagensForumList.isEmpty())
                        txtEmptyDuvida.visibility = View.VISIBLE
                    postagensDuvidaAdapter.notifyDataSetChanged()
                }

            })

        db.collection(firestoreReferences.vitrineProdutosCOLLECTION)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null) run {
                        val snackBarPersonalizada: SnackBarPersonalizada? = null
                        snackBarPersonalizada?.showMensagemLonga(v,  "Não foi possível carregar as informações")
                        return
                    }

                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED)
                            produtosList.add(dc.document.toObject(Produtor_Produto::class.java))
                    }
                    if(produtosList.isEmpty())
                        txtEmptyProduto.visibility = View.VISIBLE
                    produtosAdapter.notifyDataSetChanged()
                }

            })

        db.collection(firestoreReferences.receitaCOLLECTION)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null) run {
                        val snackBarPersonalizada: SnackBarPersonalizada? = null
                        snackBarPersonalizada?.showMensagemLonga(v,  "Não foi possível carregar as informações")
                        return
                    }

                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED)
                            receitasList.add(dc.document.toObject(Receita::class.java))
                    }
                    if(receitasList.isEmpty())
                        txtEmptyReceita.visibility = View.VISIBLE
                    receitaAdapter.notifyDataSetChanged()
                }

            })

        db.collection(firestoreReferences.bibliotecaCOLLECTION)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null) run {
                        val snackBarPersonalizada: SnackBarPersonalizada? = null
                        snackBarPersonalizada?.showMensagemLonga(v,  "Não foi possível carregar as informações")
                        return
                    }

                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED)
                            bibliotecaItemList.add(dc.document.toObject(ItemBiblioteca::class.java))
                    }
                    if(bibliotecaItemList.isEmpty())
                        txtEmptybiblioteca.visibility = View.VISIBLE
                    bibliotecaAdapter.notifyDataSetChanged()
                }

            })

        db.collection(firestoreReferences.restauranteCOLLECTION)
            .addSnapshotListener(object : EventListener<QuerySnapshot>{
                override fun onEvent(value: QuerySnapshot?, error: FirebaseFirestoreException?) {
                    if(error != null) run {
                        val snackBarPersonalizada: SnackBarPersonalizada? = null
                        snackBarPersonalizada?.showMensagemLonga(v,  "Não foi possível carregar as informações")
                        return
                    }

                    for(dc : DocumentChange in value?.documentChanges!!){
                        if(dc.type == DocumentChange.Type.ADDED)
                            restaurantesList.add(dc.document.toObject(Restaurante::class.java))
                    }
                    if(restaurantesList.isEmpty())
                        txtEmptyRestaurante.visibility = View.VISIBLE
                    restauranteAdapter.notifyDataSetChanged()
                }

            })
    }
}