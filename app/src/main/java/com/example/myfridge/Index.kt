package com.example.myfridge


import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myfridge.adapter.ProductAdapter
import com.example.myfridge.databinding.ActivityIndexBinding
import com.example.myfridge.model.Product
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import kotlinx.android.synthetic.main.activity_index.*



class Index : AppCompatActivity() {

    private lateinit var binding: ActivityIndexBinding


    private val db = FirebaseFirestore.getInstance()
    lateinit var toggle: ActionBarDrawerToggle

    override fun onResume() {
        super.onResume()
        initRecyclerView()
    }
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
                            document.id as String,
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

        binding.recyclerProducts.adapter = ProductAdapter(ArrayList<Product>(), null, null)
        binding.recyclerProducts.layoutManager = GridLayoutManager(this, 3)
        populate { productos ->
            binding.recyclerProducts.adapter = ProductAdapter(
                productsList = productos,
                onClickListener = { product ->
                    onItemSelected(
                        product
                    )
                }) { position ->
                onDeletedItem(
                    position,
                    binding.recyclerProducts.adapter, productos
                )
            }
        }
    }

    private fun onDeletedItem(
        position: Int,
        adapter: RecyclerView.Adapter<RecyclerView.ViewHolder>?,
        productos: MutableList<Product>
    ) {

        val id = productos[position].Id
        db.collection("products").document(id).delete()

        adapter?.notifyDataSetChanged()
        initRecyclerView()

    }

    private fun onItemSelected(product: Product) {
        Toast.makeText(this, product.productName, Toast.LENGTH_SHORT).show()
        showDetails(product.Id.toString())

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
                R.id.nav_logOut -> logOut()
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

    private fun logOut() {
        val prefs: SharedPreferences.Editor =
            getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.clear()
        prefs.apply()

        FirebaseAuth.getInstance().signOut()
        finish()
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
        camScannerButton.setOnClickListener {
            scanCode()
        }



        addProduct.setOnClickListener{
            addPicture()
            recreate()

        }



    }

    private fun scanCode() {
        val options = ScanOptions()
        options.setBeepEnabled(true)
        options.setOrientationLocked(true)
        options.captureActivity = BarCodeScanner::class.java
        barLauncher.launch(options)
    }

    private val barLauncher: ActivityResultLauncher<ScanOptions> = registerForActivityResult(ScanContract()) { result ->
        println("CODIGO DE BARRAS: " + result.contents)
        if (result.contents != null) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(this)
            builder.setTitle("Result")
            builder.setMessage(result.contents)
        }
    }

    private fun addPicture() {

        val intent = Intent(this, AddProduct::class.java)
        startActivity(intent)
        initRecyclerView()
    }

    private fun showDetails(productId: String) {

        val intent = Intent(this, ProductDetails::class.java).apply {
            putExtra("productId", productId)
        }
        startActivity(intent)
        initRecyclerView()
    }


}


