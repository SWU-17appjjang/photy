package org.Photy.photy

import java.util.*
import kotlin.collections.HashMap

// FireBase에서 데이터 역 직렬화를 위한 클래스
class DataVo(var beam:Int, val imageUrl:String, val imageCount:Int, val uid:String, val userId:String) {
    constructor() : this (0,"",0,"","")
}