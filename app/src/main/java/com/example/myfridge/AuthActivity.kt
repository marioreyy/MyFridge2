package com.example.myfridge

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.android.synthetic.main.activity_auth.*



@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {

    private val GOOGLESIGNIN = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_auth)
        //startActivity(Intent(this, Index::class.java))

        setup()
        session()

    }

    override fun onStart(){
        super.onStart()

        authLayout.visibility = View.VISIBLE
    }

    private fun session(){
        val prefs = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE)
        val email = prefs.getString("email", null)
        val provider = prefs.getString("provider", null)

        if (email != null && provider != null){
            authLayout.visibility = View.INVISIBLE
            showIndex(email, ProviderType.valueOf(provider))
        }

    }



    private fun setup() {
        title = "Autenticación"
        signUpButton.setOnClickListener{
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.isNotEmpty()){
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        //showHome(it.result?.user?.email?: "", ProviderType.BASIC)
                        showIndex(it.result?.user?.email?: "", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
            }
        }
        loginButton.setOnClickListener{
            if (emailEditText.text.isNotEmpty() && passwordEditText.text.toString().isNotEmpty()){
                FirebaseAuth.getInstance().signInWithEmailAndPassword(emailEditText.text.toString(),passwordEditText.text.toString()).addOnCompleteListener{
                    if(it.isSuccessful){
                        //showHome(it.result?.user?.email?: "", ProviderType.BASIC)
                        showIndex(it.result?.user?.email?: "", ProviderType.BASIC)
                    }else{
                        showAlert()
                    }
                }
            }
        }

        googleButton.setOnClickListener {
            // Configuración
            val googleConf = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
            val googleClient = GoogleSignIn.getClient(this, googleConf)
            googleClient.signOut()
            startActivityForResult(googleClient.signInIntent, GOOGLESIGNIN)
        }
    }

    private fun showAlert() {
        val builder = AlertDialog.Builder( this)
        builder.setTitle("Error")
        builder.setMessage("Se ha producido un error")
        builder.setPositiveButton( "Aceptar", null)
        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
    private fun showHome(email:String, provider: ProviderType){
        val homeIntent = Intent(this, HomeActivity::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)

        }
        startActivity(homeIntent)

    }

    private fun showIndex(email:String, provider: ProviderType) {
        val indexIntent = Intent(this, Index::class.java).apply {
            putExtra("email", email)
            putExtra("provider", provider.name)

        }
        startActivity(indexIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == GOOGLESIGNIN){

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try{
                val account = task.getResult(ApiException::class.java)

                if (account != null){
                    val credential = GoogleAuthProvider.getCredential(account.idToken, null)

                    FirebaseAuth.getInstance().signInWithCredential(credential).addOnCompleteListener {
                        if(it.isSuccessful) {
                            //showHome(account.email ?: "",ProviderType.GOOGLE)
                            showIndex(account.email ?: "",ProviderType.GOOGLE)
                        }else{
                            showAlert()

                        }
                    }

                }
            } catch(e: ApiException){
                showAlert()
            }



        }
    }
}