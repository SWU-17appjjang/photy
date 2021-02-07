package org.Photy.photy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var btn_logout:Button
    lateinit var btn_revoke: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btn_logout = findViewById(R.id.btn_logout)
        btn_revoke = findViewById(R.id.btn_revoke)

        firebaseAuth = FirebaseAuth.getInstance()

        // 로그아웃 버튼
        btn_logout.setOnClickListener{
            signOut()
        }

        // 회원탈퇴 버튼
        btn_revoke.setOnClickListener {
            revokeAccess()
        }
    }

    private fun signOut(){//로그아웃
        firebaseAuth.signOut()
    }

    private fun revokeAccess() { //회원탈퇴
        firebaseAuth.currentUser?.delete()
    }

}