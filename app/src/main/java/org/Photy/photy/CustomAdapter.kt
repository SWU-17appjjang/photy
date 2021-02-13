package org.Photy.photy

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_view.view.*



class CustomAdapter(private val context: Context, private val dataList: ArrayList<DataVo>,private val uidList: ArrayList<String>) :RecyclerView.Adapter<CustomAdapter.ItemViewHolder>(){

    var mPosition = 0

    fun getPosition():Int {
        return mPosition
    }

    private fun setPosition(position: Int) {
        mPosition = position
    }

    fun addItem(dataVo: DataVo) {
        dataList.add(dataVo)
        // 갱신처리
        notifyDataSetChanged()
    }

    fun removeItem (position: Int) {
        if (position >0) {
            dataList.removeAt(position)
            // 갱신처리
            notifyDataSetChanged()
        }
    }

    inner class ItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int) : ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_view,parent,false)
        return ItemViewHolder(view)
    }


    // onCreateViewHolder에서 만든 view와 실제 데이터를 연결
    override fun onBindViewHolder(holder: ItemViewHolder,position: Int) {
        var viewHolder = (holder as ItemViewHolder).itemView

        // 각 배열에 있는 데이터를 view 에 있는 요소에 연결
        Glide.with(viewHolder).load(dataList[position].imageUrl).into(viewHolder.imgUrl)

        // recyclable 뷰에 있는 아이템 클릭시
        holder.itemView.setOnClickListener {
            view->setPosition(position)

            // 사진 상세 페이지로 이동
            val intent = Intent(context,DetailActivity::class.java )

            // 값 전달 - imageUrl 과 uidList
            intent.putExtra("imageUrl", dataList[position].imageUrl)
            intent.putExtra("contentUid",uidList[position])
            context.startActivity(intent)
        }

    }



    override fun getItemCount() :Int{
        return dataList.size
    }

}