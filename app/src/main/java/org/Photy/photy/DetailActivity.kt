package org.Photy.photy

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity:AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        var recieveData1 = intent.getStringExtra("image_name")

        if(recieveData1 != "") {
            val resoueceId = resources.getIdentifier(recieveData1,"drawable",packageName)

            if(resoueceId >0) {
                detail_img.setImageResource(resoueceId)
            }else {
                detail_img.setImageResource(R.mipmap.ic_launcher_round)
            }
        }
        else {
            detail_img.setImageResource(R.mipmap.ic_launcher_round)
        }

    }
}