package org.Photy.photy

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_upload.*

class UploadActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload)

        /*아무래도 사진을 여기서 구현하는 방식으로 코드를 짜야할 듯..ㅠ*/

        /* 툴바로 구현
        //Toolbar
        setSupportActionBar(upload_layout_toolbar)

        getSupportActionBar()?.setLogo(R.drawable.ic_home);
        getSupportActionBar()?.setDisplayUseLogoEnabled(true);
        */


        // 버튼으로 구현
        // Home 버튼 클릭시, 메인 UI로 회귀
        btn_home.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }

        // Upload 버튼 클릭시, 업로드 완료 토스트 메시지 출력 후 메인 UI로 회귀
        btn_upload.setOnClickListener {
            Toast.makeText(this, "포터님의 사진이 업로드가 되었습니다!", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }



    }

    /*
    //ToolBar에 새로 만든 menu.xml을 인플레이트함
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuInflater = menuInflater
        menuInflater.inflate(R.menu.menu_upload, menu)
        return true
    }

    //Toolbar에 있는 메뉴 클릭시 동작
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.btn_upload -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                //onBackPressed()
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }
    */

}