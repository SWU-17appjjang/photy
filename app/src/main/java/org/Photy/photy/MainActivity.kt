package org.Photy.photy

import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_nevigation_withdrawal.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.main_toolbar.*
import java.io.File
import java.io.IOException
import java.lang.System.currentTimeMillis
import java.nio.channels.AsynchronousFileChannel.open
import java.nio.channels.DatagramChannel.open
import java.text.SimpleDateFormat
import java.util.*
import kotlin.jvm.Throws


class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth

    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath : String
    lateinit var btn_picture : Button
    lateinit var img_picture : ImageView

    lateinit var btn_upload: Button
    var fbStorage: FirebaseStorage? = null

    // 이전 클릭 시간 (뒤로가기를 위한)
    private var lastTimeBackPressed:Long=-1500

    private lateinit var activity: MainActivity
    private lateinit var googleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?)  {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        main_navigationView.setNavigationItemSelectedListener(this) //navigation 리스너

        settingPermission() // 카메라 권한체크 시작

        btn_picture=findViewById(R.id.btn_picture) //사진 찍기 버튼
        btn_picture.setOnClickListener {
            startCapture()
        }
    }

    //카메라 권한 체크
    fun settingPermission(){
        var permis = object  : PermissionListener {
            //            어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "권한 거부", Toast.LENGTH_SHORT).show()
                ActivityCompat.finishAffinity(this@MainActivity) // 권한 거부시 앱 종료
            }
        }
        TedPermission.with(this)
            .setPermissionListener(permis)
            .setRationaleMessage("카메라 사진 권한 필요")
            .setDeniedMessage("카메라 권한 요청 거부")
            .setPermissions(
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA)
            .check()
    }

    // 카메라 호출
    fun startCapture() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                val photoFile: File? = try {
                    createImageFile()
                } catch (ex: IOException) {
                    null
                }
                photoFile?.also {
                    val photoURI: Uri = FileProvider.getUriForFile(
                            this,
                            "org.Photy.photy.fileprovider",
                            it
                    )
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
            //이미지Crop
            intent.putExtra("crop", true)
        }
    }

    // 사진 찍은 후 이미지를 파일로 저장. 파일명은 현재 날짜와 시간
    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
        ).apply {
            currentPhotoPath = absolutePath
        }
    }

    //이미지 Crop 함수
    private fun launchImageCrop(uri: Uri?) {
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        setContentView(R.layout.activity_upload)

        img_picture = findViewById(R.id.img_picture)

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val file = File(currentPhotoPath)
            val selectedUri = Uri.fromFile(file)
            if (Build.VERSION.SDK_INT < 28) {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedUri)
                launchImageCrop(selectedUri)
            } else {
                val decode = ImageDecoder.createSource(
                        this.contentResolver,
                        selectedUri
                )
                val bitmap = ImageDecoder.decodeBitmap(decode)
                launchImageCrop(selectedUri)

            }
        }

        btn_upload =findViewById(R.id.btn_upload)

        // 크롭한 이미지를 이미지 뷰에 나타냄
        if (requestCode === CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode === Activity.RESULT_OK) {
                result.uri?.let {
                    img_picture.setImageBitmap(result.bitmap)
                    img_picture.setImageURI(result.uri)

                    // 업로드 버튼 클릭시 Firebase에 사진 업로드
                    btn_upload.setOnClickListener {
                        funImageUpload(result.uri)

                    }
                    // 홈버튼 클릭시 main 레이아웃으로 회귀
                    btn_home.setOnClickListener{
                        var intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                }
            } else if (resultCode === CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                val error = result.error
            }
        }


    }

    private fun funImageUpload(uriPhoto: Uri){
        fbStorage = FirebaseStorage.getInstance()

        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imgFileName = "JPEG_${timeStamp}_"
        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
            Toast.makeText(img_picture.context, "이미지를 업로드했습니다!", Toast.LENGTH_SHORT).show()
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        storageRef?.putFile(uriPhoto!!)?.addOnFailureListener{
            Toast.makeText(img_picture.context, "이미지를 업로드하지 못했습니다!", Toast.LENGTH_SHORT).show()

        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean { // 상단 메뉴 클릭시
        when(item.itemId){
            android.R.id.home->{ // 메뉴 버튼
                main_drawer_layout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean { // 메뉴 바 버튼 클릭 시

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()

        when(item.itemId){
            R.id.withdrawal-> revokeAccess()
            R.id.logout-> signOut()
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
            // (현재 버튼 누른 시간-이전에 버튼 누른 시간) <=1.5초일 때 동작
            if(currentTimeMillis() -lastTimeBackPressed<=1500)
                finish()
            lastTimeBackPressed= currentTimeMillis()
            Toast.makeText(this,"이전 버튼을 한 번 더 누르면 종료됩니다",Toast.LENGTH_SHORT).show()
        }
    }


    private fun signOut(){ // 로그아웃
        firebaseAuth.signOut()
        Toast.makeText(this,"로그아웃 하셨습니다.",Toast.LENGTH_SHORT).show()
        var intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)

    }

    private fun revokeAccess() { // 회원탈퇴
        setContentView(R.layout.activity_nevigation_withdrawal) //signout 레이아웃으로 변경

        // no 버튼 클릭시 메인 화면으로 회귀
        btn_no.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // 탈퇴 클릭시 탈퇴 처리 후 로그인 화면으로 회귀
        btn_withdrawal.setOnClickListener{
            firebaseAuth.currentUser?.delete()
            Toast.makeText(this,"회원탈퇴 하셨습니다.",Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    /*
    override fun onCreateView(name: String, context: Context, attrs: AttributeSet): View? {
        return super.onCreateView(name, context, attrs)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()
        //재로그인 시 자동로그인 방지
        googleSignInClient.signOut().addOnCompleteListener {
            activity.finish()
        }
    }
     */

}
