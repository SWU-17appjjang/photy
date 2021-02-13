package org.Photy.photy

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_intro_slider.*

class IntroSliderActivity : AppCompatActivity() {

    private val fragmentList = ArrayList<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 상태 표시줄 없애기
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        }

        setContentView(R.layout.activity_intro_slider)

        val adapter = IntroSliderAdapter(this)
        vpIntroSlider.adapter = adapter

        // 세 개의 튜토리얼 슬라이더 리스트
        fragmentList.addAll(listOf(
            Intro1Fragment(), Intro2Fragment(), Intro3Fragment()
        ))
        adapter.setFragmentList(fragmentList)

        indicatorLayout.setIndicatorCount(adapter.itemCount)
        indicatorLayout.selectCurrentPosition(0)

        registerListeners()
    }

    private fun registerListeners() {
        vpIntroSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {

            override fun onPageSelected(position: Int) {
                indicatorLayout.selectCurrentPosition(position)

                if (position < fragmentList.lastIndex) {
                    tvSkip.visibility = View.VISIBLE
                    tvNext.text = "Next"
                }
                // 슬라이더의 마지막 페이지에는 SKIP 버튼 은 사라지고, NEXT 버튼의 text 값 변경
                else {
                    tvSkip.visibility = View.GONE
                    tvNext.text = "포터로 시작하기"
                }
            }
        })

        // Skip 버튼 클릭 시 바로 로그인 화면으로 이동
        tvSkip.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Next 버튼은 현재 슬라이더의 위치에 따라 페이지 이동
        tvNext.setOnClickListener {
            val position = vpIntroSlider.currentItem

            // 다음 인덱스 페이지로 이동
            if (position < fragmentList.lastIndex) {
                vpIntroSlider.currentItem = position + 1
            }
            // 슬라이더의 마지막 페이지 도달한 경우 로그인 화면으로 이동
            else {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }
}
