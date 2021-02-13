package org.Photy.photy

import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_view.view.*


class DetailActivity():AppCompatActivity() {

    // 파이어 베이스 연동
    var fbFireStore: FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var recieveData1 = intent.getStringExtra("imageUrl")
        var recieveData2 = intent.getStringExtra("contentUid")

        fbFireStore = FirebaseFirestore.getInstance()

        if(recieveData1 != "") {
            Glide.with(this).load(recieveData1).into(detail_img)
        }
        else {
            detail_img.setImageResource(R.mipmap.ic_launcher_round)
        }


        // 빔 버튼을 클릭시
        btn_beam.setOnClickListener {
            favoriteEvent(recieveData2)
            Toast.makeText(this, "포터님의 사진에 빔을 쏘았습니다!", Toast.LENGTH_SHORT).show()
        }

    }


    // 빔 버튼 액션
    fun favoriteEvent(uid : String){
        var tsDoc = fbFireStore?.collection("users")?.document(uid)

        // 해당 사진 업로드 계정의 빔 카운트 증가 후 DB 연동
        fbFireStore?.runTransaction { transaction ->
            var contentDTO = transaction.get(tsDoc!!).toObject(DataVo::class.java)

            if (contentDTO != null) {
                contentDTO.beam= contentDTO?.beam + 1
                transaction.set(tsDoc,contentDTO)
            }
        }
    }
}