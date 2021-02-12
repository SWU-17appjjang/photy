package org.Photy.photy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.activity_view.view.*


class DetailActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var recieveData1 = intent.getStringExtra("imageUrl")

        if(recieveData1 != "") {
            Glide.with(this).load(recieveData1).into(detail_img)
        }
        else {
            detail_img.setImageResource(R.mipmap.ic_launcher_round)
        }

    }
}