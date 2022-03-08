package com.qipa.newboxproject.app.manager

import android.text.TextUtils
import com.hyphenate.chat.EMGroup

import com.hyphenate.chat.EMClient

import com.hyphenate.chat.EMChatRoom
import com.qipa.newboxproject.app.ChatHelper


object GroupHelper {
    /**
     * 是否是群主
     * @return
     */
    fun isOwner(group: EMGroup?): Boolean {
        return if (group == null ||
            TextUtils.isEmpty(group.owner)
        ) {
            false
        } else TextUtils.equals(group.owner, ChatHelper.instance?.currentUser)
    }

    /**
     * 是否是聊天室创建者
     * @return
     */
    fun isOwner(room: EMChatRoom?): Boolean {
        return if (room == null ||
            TextUtils.isEmpty(room.owner)
        ) {
            false
        } else TextUtils.equals(room.owner,ChatHelper.instance?.currentUser)
    }

    /**
     * 是否是管理员
     * @return
     */
    @Synchronized
    fun isAdmin(group: EMGroup): Boolean {
        val adminList = group.adminList
        return if (adminList != null && !adminList.isEmpty()) {
            adminList.contains(ChatHelper.instance?.currentUser)
        } else false
    }

    /**
     * 是否是管理员
     * @return
     */
    @Synchronized
    fun isAdmin(group: EMChatRoom): Boolean {
        val adminList = group.adminList
        return if (adminList != null && !adminList.isEmpty()) {
            adminList.contains(ChatHelper.instance?.currentUser)
        } else false
    }

    /**
     * 是否有邀请权限
     * @return
     */
    fun isCanInvite(group: EMGroup?): Boolean {
        return group != null && (group.isMemberAllowToInvite || isOwner(group) || isAdmin(group))
    }

    /**
     * 在黑名单中
     * @param username
     * @return
     */
    fun isInAdminList(username: String?, adminList: List<String?>?): Boolean {
        return isInList(username, adminList)
    }

    /**
     * 在黑名单中
     * @param username
     * @return
     */
    fun isInBlackList(username: String?, blackMembers: List<String?>?): Boolean {
        return isInList(username, blackMembers)
    }

    /**
     * 在禁言名单中
     * @param username
     * @return
     */
    fun isInMuteList(username: String?, muteMembers: List<String?>?): Boolean {
        return isInList(username, muteMembers)
    }

    /**
     * 是否在列表中
     * @param name
     * @return
     */
    fun isInList(name: String?, list: List<String?>?): Boolean {
        if (list == null) {
            return false
        }
        synchronized(GroupHelper::class.java) {
            for (item in list) {
                if (TextUtils.equals(name, item)) {
                    return true
                }
            }
        }
        return false
    }

    /**
     * 获取群名称
     * @param groupId
     * @return
     */
    fun getGroupName(groupId: String): String {
        val group = EMClient.getInstance().groupManager().getGroup(groupId) ?: return groupId
        return if (TextUtils.isEmpty(group.groupName)) groupId else group.groupName
    }

    /**
     * 判断是否加入了群组
     * @param allJoinGroups 所有加入的群组
     * @param groupId
     * @return
     */
    fun isJoinedGroup(allJoinGroups: List<EMGroup>?, groupId: String?): Boolean {
        if (allJoinGroups == null || allJoinGroups.isEmpty()) {
            return false
        }
        for (group in allJoinGroups) {
            if (TextUtils.equals(group.groupId, groupId)) {
                return true
            }
        }
        return false
    }
}
