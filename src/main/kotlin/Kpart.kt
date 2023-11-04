package com.example

import net.mamoe.mirai.event.EventHandler
import net.mamoe.mirai.event.ListenerHost
import net.mamoe.mirai.event.events.GroupMessageEvent
import net.mamoe.mirai.message.data.content

/** 盒子的模块接入入口(监听) */
class HeziListener:ListenerHost{
    @EventHandler
    suspend fun groupsListen(event: GroupMessageEvent){
        if (event.message.content == "盒子"){
            event.group.sendMessage("hello Members")
        }
    }
}