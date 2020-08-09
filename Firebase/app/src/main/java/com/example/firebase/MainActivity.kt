package com.example.firebase

import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    companion object{
        private const val RC_SIGN_IN  : Int = 123
    }
    private val btnSignIn by lazy {
        findViewById<Button>(R.id.btnGoogleSignIn)
    }

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var mAuth : FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        mAuth = FirebaseAuth.getInstance()

        createRequest()
        btnSignIn.setOnClickListener {
            signIn()
        }

        fab.setOnClickListener { view ->
            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
    }

    private fun createRequest() {
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
      mGoogleSignInClient = GoogleSignIn.getClient(this,googleSignInOption)
    }
    private fun signIn(){
        val intent  = mGoogleSignInClient.signInIntent
        startActivityForResult(intent,RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode== RC_SIGN_IN){
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.result
                if (account != null) {
                    firebaseAuthWithGoogle(account)
                }
            }
            catch (e: ApiException){
                Toast.makeText(this,"Google Sign In failed",Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(account: GoogleSignInAccount){
        val credential = GoogleAuthProvider.getCredential(account.idToken,null)
         mAuth.signInWithCredential(credential)
             .addOnCompleteListener(this){task ->
                 if(task.isSuccessful){
                     val user = mAuth.currentUser
                     updateUI()
                 }
             }
    }
    override fun onStart() {
        super.onStart()
        val user = mAuth.currentUser
        if(user!=null) {
           updateUI()
            finish()
        }
    }

    fun updateUI(){
        val intent =  Intent(applicationContext,LoginInSuccessActivity::class.java)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
