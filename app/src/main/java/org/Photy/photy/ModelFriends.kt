package org.Photy.photy

import java.time.LocalDateTime

// 로그인 시 가져올 UserInfo
data class ModelFriends(
    var userId: String?  = null, //로그인한 이메일 계정
    var uid: String? = null,    //데이터의 고유한 아이디
    var beam: Int? = 0,      //빔(like)Count
    var imgCount:Int? = 0,   //이미지Count
    var imageUrl: String? = null)  //이미지 Url
    // var dateAndtime: LocalDateTime? = null)//날짜시간 정보
