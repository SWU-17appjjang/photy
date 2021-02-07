package org.Photy.photy

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_toolbar.*
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.DatagramChannel.open


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth
    lateinit var btn_logout:Button
    lateinit var btn_revoke: Button





    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu_white) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        main_navigationView.setNavigationItemSelectedListener(this) //navigation 리스너

        /*
        btn_logout = findViewById(R.id.btn_logout)
        btn_revoke = findViewById(R.id.btn_revoke)

        //firebaseAuth = FirebaseAuth.getInstance()

        // 로그아웃 버튼
        btn_logout.setOnClickListener{
            signOut()
        }

        // 회원탈퇴 버튼
        btn_revoke.setOnClickListener {
            revokeAccess()
        }
         */



    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{ // 메뉴 버튼
                main_drawer_layout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.account-> Toast.makeText(this,"account clicked",Toast.LENGTH_SHORT).show()
            R.id.signout-> Toast.makeText(this,"기능 구현 예정",Toast.LENGTH_SHORT).show()
            R.id.logout-> Toast.makeText(this,"기능 구현 예정",Toast.LENGTH_SHORT).show()
            R.id.maker-> Toast.makeText(this,"음하하 우리가 만들었지",Toast.LENGTH_SHORT).show()
        }
        return false
    }

    override fun onBackPressed() { //뒤로가기 처리
        if(main_drawer_layout.isDrawerOpen(GravityCompat.START)){
            main_drawer_layout.closeDrawers()
            // 테스트를 위해 뒤로가기 버튼시 Toast 메시지
            Toast.makeText(this,"back btn clicked",Toast.LENGTH_SHORT).show()
        } else{
            super.onBackPressed()
        }
    }

    private fun signOut(){//로그아웃
        firebaseAuth.signOut()
    }

    private fun revokeAccess() { //회원탈퇴
        firebaseAuth.currentUser?.delete()
    }



}