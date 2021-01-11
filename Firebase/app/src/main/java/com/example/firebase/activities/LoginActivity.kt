package com.example.firebase.activities

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.RelativeLayout
import android.widget.Toast
import com.example.firebase.AppUtils.Companion.RC_SIGN_IN
import com.example.firebase.R
import com.example.firebase.delegates.SignInAccountDelegate
import com.example.firebase.model.Model
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : BaseActivity() {
    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }

    private val btnGoogle by lazy {
        findViewById<RelativeLayout>(R.id.btnGoogle)
    }

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mFirebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        if (Model.getInstance().isUserLogin()) {
            toNoteListActivity()
        }

        mAuth = FirebaseAuth.getInstance()
        btnGoogle.setOnClickListener {
            signIn()
        }
        createRequest()
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val result = Auth.GoogleSignInApi.getSignInResultFromIntent(data)
            onProcessSignInAccount(result!!)
            try {
                if (result.isSuccess) {
                    val account = result.signInAccount
                    Toast.makeText(
                        applicationContext, "Google Sign-In success : "
                                + account!!.displayName, Toast.LENGTH_SHORT
                    ).show()
                    toNoteListActivity()
                }

            } catch (e: ApiException) {
                Toast.makeText(this, "Google Sign In failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onProcessSignInAccount(result: GoogleSignInResult) {
        if (result.isSuccess) {
            val account = result.signInAccount
            if (account != null) {
                Model.getInstance().authenticateUserWithGoogleAccount(account,
                    object : SignInAccountDelegate {
                        override fun onSuccessSignIn(account: GoogleSignInAccount) {
                            mAuth = FirebaseAuth.getInstance()
                            mFirebaseUser = mAuth.currentUser!!

                            Model.getInstance().addNewUser(
                                mFirebaseUser.uid,
                                mFirebaseUser.displayName!!, mFirebaseUser.email!!
                            )

                        }

                        override fun onFailureSignIn() {
                            Toast.makeText(applicationContext, "Google Sign In failed", Toast.LENGTH_LONG).show()
                        }

                    })
            }
        }

    }

    override fun onStop() {
        super.onStop()
        finish()
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
