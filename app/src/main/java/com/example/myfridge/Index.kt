package com.example.myfridge


import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle

import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myfridge.adapter.ProductAdapter
import com.example.myfridge.model.Product
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_index.*


import com.google.firebase.firestore.FirebaseFirestore


class Index : AppCompatActivity() {


    private val db = FirebaseFirestore.getInstance()
    lateinit var toggle: ActionBarDrawerToggle
    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_index)



        // Obtener datos de la sesión
        val bundle = intent.extras
        val email = bundle?.getString("email")
        val provider = bundle?.getString("provider")

        // Guardado de datos

        val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
        prefs.putString("email", email)
        prefs.putString("provider", provider)
        prefs.apply()
        drawerLayout()
        loadPictures()
        setup()
        populate { products ->
            initRecyclerView(products)
        }

    }

    public fun populate(callback: (MutableList<Product>) -> Unit) {

        val db = FirebaseFirestore.getInstance()
        val products: MutableList<Product> = ArrayList()

        db.collection("products")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    products.add(
                        Product(document.get("name") as String,
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



    private fun initRecyclerView(products: MutableList<Product>) {

        val recyclerView = findViewById<RecyclerView>(R.id.recyclerProducts)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val adapter = ProductAdapter(products)
        recyclerView.adapter = adapter

        Log.d("Adapter", adapter.toString())
    }





    private fun drawerLayout(){
        val drawerLayout : DrawerLayout = findViewById(R.id.drawerLayout)
        val navView : NavigationView = findViewById(R.id.nav_view)

        toggle = ActionBarDrawerToggle(this, drawerLayout,R.string.open,R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        navView.setNavigationItemSelectedListener {

            when(it.itemId){
                R.id.nav_home -> Toast.makeText(applicationContext,"Clicked Home",Toast.LENGTH_SHORT).show()
                R.id.nav_stats -> Toast.makeText(applicationContext,"Clicked Stats",Toast.LENGTH_SHORT).show()
                R.id.nav_login -> Toast.makeText(applicationContext,"Clicked Login",Toast.LENGTH_SHORT).show()
                R.id.nav_settings -> Toast.makeText(applicationContext,"Clicked Settings",Toast.LENGTH_SHORT).show()
                R.id.nav_shopping -> Toast.makeText(applicationContext,"Clicked Shopping List",Toast.LENGTH_SHORT).show()

            }
            true
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }


    private fun setup(){


        imageView3.setOnClickListener{
            showDetails(imageView3.id.toString())
        }
        imageView4.setOnClickListener{
            showDetails(imageView4.id.toString())
        }
        imageView5.setOnClickListener{
            showDetails(imageView5.id.toString())
        }

        logOutButtonIndex.setOnClickListener {
            // Borrado de datos
            val prefs: SharedPreferences.Editor = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).edit()
            prefs.clear()
            prefs.apply()

            FirebaseAuth.getInstance().signOut()
            onBackPressed()

        }



    }

    private fun showDetails(id:String) {
        val intent = Intent(this, ImageDetails::class.java).apply{
            putExtra("imageId",id)
        }

        startActivity(intent)
    }


    private fun loadPictures(){

        val imageview: ImageView = findViewById(R.id.imageView+2)
        val imageview2: ImageView = findViewById(R.id.imageView4)
        val imageview3: ImageView = findViewById(R.id.imageView5)




        var arrays = arrayOf(arrayOf(imageview,textView8), arrayOf(imageview2,textView5),
            arrayOf(imageview3,textView6))
        for(array in arrays){


            db.collection("products").document(array[0].id.toString()).get().addOnSuccessListener {
                if(it.exists())    {
                    Glide.with(applicationContext).load(it.get("url")).into(array[0] as ImageView)
                    var text = array[1] as TextView
                    text.text = it.get("name") as String?
                }


            }
        }





    }


}


