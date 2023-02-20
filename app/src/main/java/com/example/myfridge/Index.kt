package com.example.myfridge


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import com.example.myfridge.adapter.ProductAdapter
import com.example.myfridge.databinding.ActivityIndexBinding
import com.example.myfridge.model.Product
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_index.*


class Index : AppCompatActivity() {

    private lateinit var binding: ActivityIndexBinding


    private val db = FirebaseFirestore.getInstance()
    lateinit var toggle: ActionBarDrawerToggle


    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        binding = ActivityIndexBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // Obtener datos de la sesión
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        // Guardado de datos

        val prefs: SharedPreferences.Editor =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
        drawerLayout()
        setup()
        initRecyclerView()

    }

    //Clase para popular el RecyclerView
    public fun populate(callback: (MutableList<Product>) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val products: MutableList<Product> = ArrayList()

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    products.add(
                        Product(
                            document.get("name") as String,
                            document.get("url") as String
                        )
                    )
                }
                callback(products)
            }
            .addOnFailureListener { exception ->
                Log.d("Error getting documents: ", exception.toString())
            }
    }

    private fun initRecyclerView() {


        binding.recyclerProducts.layoutManager = GridLayoutManager(this, 3)
        populate { productos ->
            binding.recyclerProducts.adapter = ProductAdapter(productsList = productos,
                onClickListener = { product ->
                    onItemSelected(
                        product
                    )
                }, onClickDelete = { position -> onDeletedItem(position) })
        }
    }

    private fun onDeletedItem(position: Int) {
        db.collection("products").document(position.toString()).delete()
    }

    private fun onItemSelected(product: Product) {
        Toast.makeText(this, product.productName, Toast.LENGTH_SHORT).show()
    }


    private fun drawerLayout() {
        val drawerLayout: DrawerLayout = findViewById(R.id.drawerLayout)
        val navView: NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when (it.itemId) {
                R.id.nav_home -> Toast.makeText(
                    applicationContext,
                    "Clicked Home",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_stats -> Toast.makeText(
                    applicationContext,
                    "Clicked Stats",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_login -> Toast.makeText(
                    applicationContext,
                    "Clicked Login",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_settings -> Toast.makeText(
                    applicationContext,
                    "Clicked Settings",
                    Toast.LENGTH_SHORT
                ).show()
                R.id.nav_shopping -> Toast.makeText(
                    applicationContext,
                    "Clicked Shopping List",
                    Toast.LENGTH_SHORT
                ).show()

            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (toggle.onOptionsItemSelected(item)) {
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setup() {
        /**

        imageView3.setOnClickListener{
        showDetails(imageView3.id.toString())
        }
        imageView4.setOnClickListener{
        showDetails(imageView4.id.toString())
        }
        imageView5.setOnClickListener{
        showDetails(imageView5.id.toString())
        }
         **/
        logOutButtonIndex.setOnClickListener {
            // Borrado de datos
            val prefs: SharedPreferences.Editor =
                getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()

        }

        addProduct.setOnClickListener{
            addPicture()
            recreate()
        }



    }

    private fun addPicture() {

        val intent = Intent(this, AddProduct::class.java)
        startActivity(intent)
    }


}


