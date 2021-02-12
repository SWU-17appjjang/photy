package org.Photy.photy

import java.util.*
import kotlin.collections.HashMap

class DataVo(var beam:Int, val imageUrl:String, val imageCount:Int, val uid:String, val userId:String) {
    constructor() : this (0,"",0,"","")
}