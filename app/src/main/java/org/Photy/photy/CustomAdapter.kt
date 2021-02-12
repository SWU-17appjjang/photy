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


class CustomAdapter(private val context: Context, private val dataList: ArrayList<DataVo>) :RecyclerView.Adapter<CustomAdapter.ItemViewHolder>(){


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

        /*
        private val userPhoto = itemView.findViewById<ImageView>(R.id.userImg)
        //private val userName = itemView.findViewById<ImageView>(R.id..userNameTxt) //우린 계정명 안나타냄

        fun bind(dataVo: DataVo,context:Context) {
            if(dataVo.photo != "") {
                val resoueceid =
                    context.resources.getIdentifier(dataVo.photo, "drawabel", context.packageName)

                if(resoueceid >0) {
                    userPhoto.setImageResource(resoueceid)
                }
                else userPhoto.setImageResource(R.mipmap.ic_launcher_round)
            }
            else{
                userPhoto.setImageResource(R.mipmap.ic_launcher_round)
            }
        }
         */
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType:Int) : ItemViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.activity_view,parent,false)
        return ItemViewHolder(view)
    }


    // onCreateViewHolder에서 만든 view와 실제 데이터를 연결
    override fun onBindViewHolder(holder: ItemViewHolder,position: Int) {
        var viewHolder = (holder as ItemViewHolder).itemView

        // Task2 : 각 배열에 있는 데이터를 view 에 있는 요소에 연결하라.
        Glide.with(viewHolder).load(dataList[position].imageUrl).into(viewHolder.imgUrl)

        holder.itemView.setOnClickListener {
            view->setPosition(position)

            //open another activity on item click
            val intent = Intent(context,DetailActivity::class.java )
            intent.putExtra("imageUrl", dataList[position].imageUrl)
            context.startActivity(intent)

        }

        /*
        holder.bind(dataList[position], context)


        holder.itemView.setOnClickListener {
            view->setPosition(position)

            //open another activity on item click
            val intent = Intent(context,DetailActivity::class.java )
            intent.putExtra("image_name", dataList[position].photo)
            context.startActivity(intent)

        }

        holder.itemView.setOnLongClickListener { view ->
            setPosition(position)
            Toast.makeText(view.context,"아이템 롱 클릭",Toast.LENGTH_SHORT).show()
            return@setOnLongClickListener true
        }
         */
    }



    override fun getItemCount() :Int{
        return dataList.size
    }

}