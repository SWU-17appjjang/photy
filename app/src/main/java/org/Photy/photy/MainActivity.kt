/* 어플명 : 즉석 사진 공유 어플, 포티
* Front : 안예린, 정수빈
* back :  조수민, 조희연
* 최종 제출 날짜 : 21-02-13
* */
package org.Photy.photy

import VerticalItemDecorator
import android.app.Activity
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.material.navigation.NavigationView
import com.google.common.collect.ComparisonChain.start
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_nevigation_aboutus.*
import kotlinx.android.synthetic.main.activity_nevigation_withdrawal.*
import kotlinx.android.synthetic.main.activity_upload.*
import kotlinx.android.synthetic.main.easter.*
import kotlinx.android.synthetic.main.main_layout.*
import kotlinx.android.synthetic.main.main_toolbar.*
import java.io.File
import java.io.IOException
import java.lang.System.currentTimeMillis
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.jvm.Throws




class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var firebaseAuth: FirebaseAuth
    
    val REQUEST_IMAGE_CAPTURE = 1
    lateinit var currentPhotoPath : String
    lateinit var btn_picture : Button
    lateinit var img_picture : ImageView

    // 사진 업로드 버튼 초기화
    lateinit var btn_upload: Button

    //계정 보여주는 텍스트 뷰
    lateinit var user_email :TextView

    // 빔 갯수 연동
    lateinit var beamCount : TextView

    // 이미지 업로드한 갯수 연동
    lateinit var imgCount : TextView

    //실시간 데이터베이스
    lateinit var mDatabase: DatabaseReference

    // 이전 클릭 시간 (뒤로가기를 위한)
    private var lastTimeBackPressed:Long=-1500

    private lateinit var activity: MainActivity
    private lateinit var googleSignInClient: GoogleSignInClient

    // 파이어베이스 이미지 다운로드
    var fbAuth : FirebaseAuth? = null
    var fbFireStore : FirebaseFirestore? = null
    var fbStorage: FirebaseStorage? = null


    // 데이터 리스트 - 전체 사진
    var users : ArrayList<DataVo> = arrayListOf()

    // 데이터 리스트 - 좋아요를 위한 uid
    var contentUidList: ArrayList<String> = arrayListOf()

    var countTemp : Int = 0

    var bCount : Boolean = false



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(main_layout_toolbar) // 툴바를 액티비티의 앱바로 지정
        supportActionBar?.setDisplayHomeAsUpEnabled(true) // 드로어를 꺼낼 홈 버튼 활성화
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_menu) // 홈버튼 이미지 변경
        supportActionBar?.setDisplayShowTitleEnabled(false) // 툴바에 타이틀 안보이게

        main_navigationView.setNavigationItemSelectedListener(this) //navigation 리스너

        settingPermission() // 카메라 권한체크 시작

        //이미지 파이어베이스 다운 관련 스토리지 이니셜라이즈
        fbStorage = FirebaseStorage.getInstance()
        fbAuth = FirebaseAuth.getInstance()
        fbFireStore = FirebaseFirestore.getInstance()


        // Adapter 선언
        val mAdapter = CustomAdapter(this, users, contentUidList)
        recycler_view.adapter = mAdapter



        // 로그인
        if (true) {
            // 유저 정보
            var userInfo = ModelFriends()

            // 데이터베이스에서 아이디와 uid를 가져옴
            userInfo.uid = fbAuth?.uid
            userInfo.userId = fbAuth?.currentUser?.email

            fbFireStore?.collection("users")?.document(fbAuth?.uid.toString())?.set(userInfo)

            // 빔카운트 아이디와 연결
            beamCount = findViewById(R.id.beam_counts)

            // 빔카운트 텍스트를 기존 값과 연결
            beamCount.text = userInfo.beam.toString()

        }


        // users의 문서를 불러온 뒤 DataVo으로 변환해 ArrayList에 담음
        fbFireStore?.collection("users")
            ?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                // ArrayList 비워줌
                users.clear()
                contentUidList.clear()

                // 각 uri , userId 를 객체 Arraylist 로 넣음
                for (snapshot in querySnapshot!!.documents) {
                    var item = snapshot.toObject(DataVo::class.java)
                    users.add(item!!)
                    contentUidList.add(snapshot.id)
                }
                mAdapter.notifyDataSetChanged()
            }

        // 3개의 열을 갖는 그리드 레이아웃 매니저를 설정, 기본값은 vertical
        val gridLayoutManager = GridLayoutManager(applicationContext, 3)
        recycler_view.layoutManager = gridLayoutManager

        // 세로 간격 설정
        recycler_view.addItemDecoration(VerticalItemDecorator(20))

        // 사진 찍기 버튼 클릭시, 카메라 구동
        btn_picture = findViewById(R.id.btn_picture) //사진 찍기 버튼
        btn_picture.setOnClickListener {
            startCapture()
        }


    }


    //카메라 권한 체크
    fun settingPermission(){
        var permis = object  : PermissionListener {
            // 어떠한 형식을 상속받는 익명 클래스의 객체를 생성하기 위해 다음과 같이 작성
            override fun onPermissionGranted() {
            }

            override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
                Toast.makeText(this@MainActivity, "권한 거부", Toast.LENGTH_SHORT).show()
                ActivityCompat.finishAffinity(this@MainActivity) // 권한 거부시 앱 종료
            }
        }
        //Ted 라이브러리
        TedPermission.with(this)
                .setPermissionListener(permis)
                .setRationaleMessage("카메라 사진 권한 필요")
                .setDeniedMessage("카메라 권한 요청 거부")
                .setPermissions(
                        android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
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


    @Throws(IOException::class)
    private fun createImageFile(): File { // 사진 찍은 후 이미지를 파일로 저장. 파일명은 현재 날짜와 시간
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


    private fun launchImageCrop(uri: Uri?) { //이미지 Crop 함수
        CropImage.activity(uri).setGuidelines(CropImageView.Guidelines.ON)
                .setCropShape(CropImageView.CropShape.RECTANGLE)
                .setAspectRatio(1, 1)
                .start(this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) { // 촬영된 이미지 handling
        super.onActivityResult(requestCode, resultCode, data)
        setContentView(R.layout.activity_upload)

        // 이미지뷰 초기화
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
                        start()
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

    private fun funImageUpload(uriPhoto: Uri){ // 편집된 이미지 Firebase에 연동
        fbStorage = FirebaseStorage.getInstance()

        // 이미지 이름을 시간으로 정해 겹치지 않게 함
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imgFileName = "JPEG_${timeStamp}_"
        var storageRef = fbStorage?.reference?.child("images")?.child(imgFileName)

        storageRef?.putFile(uriPhoto!!)?.addOnSuccessListener {
            storageRef.downloadUrl.addOnSuccessListener { uri ->
                var userInfo = ModelFriends()

                // url를 가져와 객체에 넣음
                userInfo.imageUrl = uri.toString()
                userInfo.userId = fbAuth?.currentUser?.email

                // 저장된 이미지 카운트를 임시 저장소에 저장
                var tempImgCount = userInfo.imgCount

                // 값을 1 더해줌
                if (tempImgCount != null) {
                    tempImgCount = tempImgCount + 1
                }

                // 파일 저장 경로를 저장함
                fbFireStore?.collection("users")?.document(fbAuth?.uid.toString())?.update("imageUrl", userInfo.imageUrl.toString())

                // 해시맵을 만들어 객체를 만듦
                val result = hashMapOf("userId" to userInfo.userId ,"imgCount" to tempImgCount, "url" to userInfo.imageUrl, "beam" to userInfo.beam)

                // 파이어베이스 실시간 데이터에 경로저장
                mDatabase = FirebaseDatabase.getInstance().getReference()
                mDatabase.child("user_image").push().setValue(result)

            }
            Toast.makeText(img_picture.context, "이미지를 업로드했습니다!", Toast.LENGTH_SHORT).show()
            // 이미지 저장 시 인텐트를 통해 메인 엑티비티로 복귀
            var intent = Intent(this, MainActivity::class.java)
            intent.putExtra("bCount",true)


            startActivity(intent)
        }
        storageRef?.putFile(uriPhoto!!)?.addOnFailureListener{
            Toast.makeText(img_picture.context, "이미지를 업로드하지 못했습니다!", Toast.LENGTH_SHORT).show()

        }
    }



    override fun onOptionsItemSelected(item: MenuItem): Boolean { // 상단 메뉴 클릭시
        var userInfo = ModelFriends()
        userInfo.uid = fbAuth?.uid
        userInfo.userId = fbAuth?.currentUser?.email

        var email = userInfo.userId


        when(item.itemId){
            android.R.id.home->{ // 메뉴 버튼
                main_drawer_layout.openDrawer(GravityCompat.START)    // 네비게이션 드로어 열기
                user_email = findViewById(R.id.user_email)
                user_email.text = email

                // 이미지 카운트를 네비게이션 뷰 텍스트뷰와 연동
                imgCount = findViewById(R.id.photterScore)
                bCount = intent.getBooleanExtra("bCount", false)
                if( bCount == true) {
                    countTemp++
                    bCount = false

                }

                // 텍스트 내용 연결 후 변경
                imgCount.text = countTemp.toString()
                intent.putExtra("countTemp", countTemp)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean { // 네비게이션 메뉴 버튼 클릭 시

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        firebaseAuth = FirebaseAuth.getInstance()

        when(item.itemId){
            R.id.withdrawal-> revokeAccess() // 회원 탈퇴
            R.id.logout-> signOut() // 로그아웃
            R.id.maker-> aboutUs() // 개발자 정보
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

    private fun aboutUs(){ // 개발자 소개
        setContentView(R.layout.activity_nevigation_aboutus) //aboutus 레이아웃으로 변경

        // 돌아가기 버튼 클릭시 메인 화면으로 회귀
        btn_backhome.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 이스터 에그
        easter.setOnClickListener {
            setContentView(R.layout.easter) //aboutus 레이아웃으로 변경
            // 돌아가기 버튼 클릭시 메인 화면으로 회귀
            btn_backhome2.setOnClickListener {
                var intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }


    private fun signOut(){ // 로그아웃
        firebaseAuth.signOut()
        Toast.makeText(this,"로그아웃 하셨습니다.",Toast.LENGTH_SHORT).show()
        var intent = Intent(this, LoginActivity::class.java)
        //재로그인 시 자동로그인 방지
        googleSignInClient.signOut().addOnCompleteListener {
            startActivity(intent)
        }

    }

    private fun revokeAccess() { // 회원탈퇴
        setContentView(R.layout.activity_nevigation_withdrawal) //signout 레이아웃으로 변경

        // no 버튼 클릭시 메인 화면으로 회귀
        btn_no.setOnClickListener {
            var intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
        // 탈퇴 클릭시 탈퇴 처리 후 어플종료
        btn_withdrawal.setOnClickListener{
            firebaseAuth.currentUser?.delete()
            Toast.makeText(this,"회원탈퇴 하셨습니다.포티가 종료됩니다.",Toast.LENGTH_SHORT).show()

            //재로그인 시 자동로그인 방지
            googleSignInClient.revokeAccess().addOnCompleteListener {
                finish()
            }
        }
    }
}