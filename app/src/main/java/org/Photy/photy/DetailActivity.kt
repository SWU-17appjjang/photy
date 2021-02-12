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

        btn_beam.setOnClickListener {
            favoriteEvent(recieveData2)
            Toast.makeText(this, "포터님의 사진에 빔을 쏘았습니다!", Toast.LENGTH_SHORT).show()
        }

    }

    fun favoriteEvent(uid : String){
        var tsDoc = fbFireStore?.collection("users")?.document(uid)

        fbFireStore?.runTransaction { transaction ->

            var contentDTO = transaction.get(tsDoc!!).toObject(DataVo::class.java)

            if (contentDTO != null) {
                contentDTO.beam= contentDTO?.beam + 1
                transaction.set(tsDoc,contentDTO)
            }
        }
    }
}