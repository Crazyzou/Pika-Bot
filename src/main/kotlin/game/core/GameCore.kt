package com.example.game.core

import java.io.File

/** 游戏核心主体主要功能：
 * 识别玩家指令合法性
 * 管理并维护游戏列表
 * 根据指令操作游戏
 * 以字符串文或图片形式输出操作结果(img\String\null)
 * 存储游戏信息
 * 读取自定义地图
 * */
object GameCore {
    private var dataFolder:File? = null
    private var configFolder:File? = null

    /**初始化设置基础数据与配置目录*/
    public fun initBaseFolder(data:File,config:File){
        if (dataFolder==null){
            dataFolder = data
        }
        if (configFolder == null){
            configFolder = config
        }
    }
}

