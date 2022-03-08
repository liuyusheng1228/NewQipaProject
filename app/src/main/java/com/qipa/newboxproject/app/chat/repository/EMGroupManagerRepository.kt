package com.qipa.newboxproject.app.chat.repository

import android.text.TextUtils
import android.util.Log
import com.hyphenate.EMCallBack

import androidx.lifecycle.LiveData

import com.qipa.newboxproject.app.chat.interfaceOrImplement.ResultCallBack

import com.hyphenate.chat.EMGroup

import com.hyphenate.EMValueCallBack

import com.hyphenate.chat.EMGroupOptions

import com.hyphenate.chat.EMMucSharedFile

import com.hyphenate.easeui.utils.EaseCommonUtils

import com.hyphenate.easeui.domain.EaseUser

import com.hyphenate.exceptions.HyphenateException

import com.hyphenate.chat.EMCursorResult

import com.hyphenate.easeui.manager.EaseThreadManager

import com.hyphenate.chat.EMGroupInfo

import androidx.lifecycle.MutableLiveData
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.ChatHelper
import com.qipa.newboxproject.app.chat.net.ErrorCode
import com.qipa.newboxproject.app.chat.net.Resource
import java.io.File
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


class EMGroupManagerRepository : BaseEMRepository() {
    /**
     * 获取所有的群组列表
     * @return
     */
    val allGroups: LiveData<Resource<List<EMGroup?>?>>?
        get() = object : NetworkBoundResource<List<EMGroup?>?, List<EMGroup?>?>() {
            protected override fun shouldFetch(data: List<EMGroup?>?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<List<EMGroup?>?>? {
                val allGroups = groupManager!!.allGroups
                return MutableLiveData(allGroups)
            }

            protected override fun createCall(callBack: ResultCallBack<LiveData<List<EMGroup?>?>>?) {
                if (!isLoggedIn) {
                    callBack?.onError(ErrorCode.EM_NOT_LOGIN)
                    return
                }
                groupManager!!.asyncGetJoinedGroupsFromServer(object :
                    EMValueCallBack<List<EMGroup?>> {
                    override fun onSuccess(value: List<EMGroup?>) {
                        callBack?.onSuccess(MutableLiveData(value))
                    }

                    override fun onError(error: Int, errorMsg: String) {
                        callBack?.onError(error, errorMsg)
                    }
                })
            }

            protected override fun saveCallResult(item: List<EMGroup?>?) {}
        }.asLiveData()

    /**
     * 获取所有群组列表
     * @param callBack
     */
    fun getAllGroups(callBack: ResultCallBack<List<EMGroup?>?>?) {
        if (!isLoggedIn) {
            callBack!!.onError(ErrorCode.EM_NOT_LOGIN)
            return
        }
        groupManager!!.asyncGetJoinedGroupsFromServer(object : EMValueCallBack<List<EMGroup?>?> {
            override fun onSuccess(value: List<EMGroup?>?) {
                callBack?.onSuccess(value)
            }

            override fun onError(error: Int, errorMsg: String) {
                callBack?.onError(error, errorMsg)
            }
        })
    }

    /**
     * 从服务器分页获取加入的群组
     * @param pageIndex
     * @param pageSize
     * @return
     */
    fun getGroupListFromServer(pageIndex: Int, pageSize: Int): LiveData<Resource<List<EMGroup?>?>> {
        return object : NetworkOnlyResource<List<EMGroup?>?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<List<EMGroup?>?>>) {
                groupManager!!.asyncGetJoinedGroupsFromServer(
                    pageIndex,
                    pageSize,
                    object : EMValueCallBack<List<EMGroup?>?> {
                        override fun onSuccess(value: List<EMGroup?>?) {
                            callBack.onSuccess(createLiveData(value))
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 获取公开群
     * @param pageSize
     * @param cursor
     * @return
     */
    fun getPublicGroupFromServer(
        pageSize: Int,
        cursor: String?
    ): LiveData<Resource<EMCursorResult<EMGroupInfo?>?>> {
        return object : NetworkOnlyResource<EMCursorResult<EMGroupInfo?>?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<EMCursorResult<EMGroupInfo?>?>>) {
                ChatHelper.instance?.groupManager?.asyncGetPublicGroupsFromServer(
                    pageSize,
                    cursor,
                    object : EMValueCallBack<EMCursorResult<EMGroupInfo?>?> {
                        override fun onSuccess(value: EMCursorResult<EMGroupInfo?>?) {
                            callBack.onSuccess(createLiveData(value))
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 获取群组信息
     * @param groupId
     * @return
     */
    fun getGroupFromServer(groupId: String?): LiveData<Resource<EMGroup?>> {
        return object : NetworkOnlyResource<EMGroup?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<EMGroup?>>) {
                if (!isLoggedIn) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN)
                    return
                }
                ChatHelper.instance?.groupManager?.asyncGetGroupFromServer(groupId, object : EMValueCallBack<EMGroup?> {
                        override fun onSuccess(value: EMGroup?) {
                            callBack.onSuccess(createLiveData(value))
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 加入群组
     * @param group
     * @param reason
     * @return
     */
    fun joinGroup(group: EMGroup, reason: String?): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                if (group.isMemberOnly) {
                    groupManager!!.asyncApplyJoinToGroup(
                        group.groupId,
                        reason,
                        object : EMCallBack {
                            override fun onSuccess() {
                                callBack.onSuccess(createLiveData(true))
                            }

                            override fun onError(code: Int, error: String) {
                                callBack.onError(code, error)
                            }

                            override fun onProgress(progress: Int, status: String) {}
                        })
                } else {
                    groupManager!!.asyncJoinGroup(group.groupId, object : EMCallBack {
                        override fun onSuccess() {
                            callBack.onSuccess(createLiveData(true))
                        }

                        override fun onError(code: Int, error: String) {
                            callBack.onError(code, error)
                        }

                        override fun onProgress(progress: Int, status: String) {}
                    })
                }
            }
        }.asLiveData()
    }

    fun getGroupMembersByName(groupId: String?): LiveData<Resource<List<String?>?>> {
        return object : NetworkOnlyResource<List<String?>?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<List<String?>?>>) {
                if (!isLoggedIn) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN)
                    return
                }
                ChatHelper.instance?.groupManager
                    ?.asyncGetGroupFromServer(groupId, object : EMValueCallBack<EMGroup> {
                        override fun onSuccess(value: EMGroup) {
                            var members = value.members
                            if (members.size < value.memberCount - value.adminList.size - 1) {
                                members = getAllGroupMemberByServer(groupId)
                            }
                            members.addAll(value.adminList)
                            members.add(value.owner)
                            if (!members.isEmpty()) {
                                callBack.onSuccess(createLiveData(members))
                            } else {
                                callBack.onError(ErrorCode.EM_ERR_GROUP_NO_MEMBERS)
                            }
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 获取群组成员列表(包含管理员和群主)
     * @param groupId
     * @return
     */
    fun getGroupAllMembers(groupId: String?): LiveData<Resource<List<EaseUser>>> {
        return object : NetworkOnlyResource<List<EaseUser>>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<List<EaseUser>>>) {
                if (!isLoggedIn) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN)
                    return
                }
                ChatHelper.instance?.groupManager?.asyncGetGroupFromServer(groupId, object : EMValueCallBack<EMGroup> {
                        override fun onSuccess(value: EMGroup) {
                            var members = value.members
                            if (members.size < value.memberCount - value.adminList.size - 1) {
                                members = getAllGroupMemberByServer(groupId)
                            }
                            members.addAll(value.adminList)
                            members.add(value.owner)
                            if (!members.isEmpty()) {
                                val users: List<EaseUser> =EaseUser.parse(members)
                                sortUserData(users)
                                callBack.onSuccess(createLiveData(users))
                            } else {
                                callBack.onError(ErrorCode.EM_ERR_GROUP_NO_MEMBERS)
                            }
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 获取群组成员列表(不包含管理员和群主)
     * @param groupId
     * @return
     */
    fun getGroupMembers(groupId: String?): LiveData<Resource<List<EaseUser?>?>> {
        return object : NetworkOnlyResource<List<EaseUser?>?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<List<EaseUser?>?>>) {
                if (!isLoggedIn) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN)
                    return
                }
                runOnIOThread {
                    val members: List<String?> =
                        getAllGroupMemberByServer(groupId)
                    val users: MutableList<EaseUser> = ArrayList()
                    if (members != null && !members.isEmpty()) {
                        for (i in members.indices) {
                            val user: EaseUser? =
                                members[i]?.let { ChatHelper.instance?.getUserInfo(it) }
                            if (user != null) {
                                users.add(user)
                            } else {
                                val m_user = EaseUser(members[i]!!)
                                users.add(m_user)
                            }
                        }
                    }
                    sortUserData(users)
                    callBack.onSuccess(createLiveData(users))
                }
            }
        }.asLiveData()
    }

    /**
     * 获取禁言列表
     * @param groupId
     * @return
     */
    fun getGroupMuteMap(groupId: String?): LiveData<Resource<Map<String?, Long?>?>?>? {
        return object : NetworkOnlyResource<Map<String?, Long?>?>() {

            override fun createCall(callBack: ResultCallBack<LiveData<Map<String?, Long?>?>>) {
                EaseThreadManager.getInstance().runOnIOThread {
                    var map: Map<String, Long>? = null
                    val result: MutableMap<String, Long> = HashMap()
                    val pageSize = 200
                    do {
                        map = try {
                            groupManager!!.fetchGroupMuteList(groupId, 0, pageSize)
                        } catch (e: HyphenateException) {
                            e.printStackTrace()
                            callBack.onError(e.errorCode, e.message)
                            break
                        }
                        if (map != null) {
                            result.putAll(map)
                        }
                    } while (map != null && map.size >= 200)
//                    callBack.onSuccess(createLiveData(result))

                }
            }
        }.asLiveData()
    }
    /**
     * 获取群组黑名单列表
     * @param groupId
     * @return
     */
    fun getGroupBlackList(groupId: String): LiveData<Resource<List<String?>?>> {
        return object : NetworkOnlyResource<List<String?>?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<List<String?>?>>) {
                EaseThreadManager.getInstance().runOnIOThread {
                    var list: List<String?>? = null
                    list = try {
                        fetchGroupBlacklistFromServer(groupId)
                    } catch (e: HyphenateException) {
                        e.printStackTrace()
                        callBack.onError(e.errorCode, e.message)
                        return@runOnIOThread
                    }
                    if (list == null) {
                        list = ArrayList()
                    }
                    callBack.onSuccess(createLiveData(list))
                }
            }
        }.asLiveData()
    }

    @Throws(HyphenateException::class)
    private fun fetchGroupBlacklistFromServer(groupId: String): List<String?> {
        val pageSize = 200
        var list: List<String?>? = null
        val result: MutableList<String?> = ArrayList()
        do {
            list = groupManager!!.fetchGroupBlackList(groupId, 0, pageSize)
            if (list != null) {
                result.addAll(list)
            }
        } while (list != null && list.size >= pageSize)
        return result
    }

    /**
     * 获取群公告
     * @param groupId
     * @return
     */
    fun getGroupAnnouncement(groupId: String?): LiveData<Resource<String?>> {
        return object : NetworkBoundResource<String?, String?>() {
            protected override fun shouldFetch(data: String?): Boolean {
                return true
            }

            override fun loadFromDb(): LiveData<String?>? {
                val announcement: String? =
                    ChatHelper.instance?.groupManager?.getGroup(groupId)?.getAnnouncement()
                return createLiveData(announcement.toString())
            }

            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>?) {
                groupManager!!.asyncFetchGroupAnnouncement(
                    groupId,
                    object : EMValueCallBack<String?> {
                        override fun onSuccess(value: String?) {
                            callBack?.onSuccess(createLiveData(value))
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack?.onError(error, errorMsg)
                        }
                    })
            }

            protected override fun saveCallResult(item: String?) {}
        }.asLiveData()
    }

    /**
     * 获取所有成员
     * @param groupId
     * @return
     */
    fun getAllGroupMemberByServer(groupId: String?): MutableList<String?> {
        // 根据groupId获取群组中所有成员
        val contactList: MutableList<String?> = ArrayList()
        var result: EMCursorResult<String?>? = null
        do {
            try {
                result = groupManager!!.fetchGroupMembers(
                    groupId,
                    if (result != null) result.cursor else "",
                    20
                )
            } catch (e: HyphenateException) {
                e.printStackTrace()
            }
            if (result != null) {
                contactList.addAll(result.data)
            }
        } while (result != null && !TextUtils.isEmpty(result.cursor))
        return contactList
    }

    private fun sortUserData(users: List<EaseUser>) {
        Collections.sort(users, object : Comparator<EaseUser> {
            override fun compare(lhs: EaseUser, rhs: EaseUser): Int {
                return if (lhs.initialLetter == rhs.initialLetter) {
                    lhs.nickname.compareTo(rhs.nickname)
                } else {
                    if ("#" == lhs.initialLetter) {
                        return 1
                    } else if ("#" == rhs.initialLetter) {
                        return -1
                    }
                    lhs.initialLetter.compareTo(rhs.initialLetter)
                }
            }
        })
    }

    fun getAllManageGroups(allGroups: List<EMGroup>?): List<EMGroup> {
        if (allGroups != null && allGroups.size > 0) {
            val manageGroups: MutableList<EMGroup> = ArrayList()
            for (group in allGroups) {
                if (TextUtils.equals(group.owner, currentUser) || group.adminList.contains(
                        currentUser
                    )
                ) {
                    manageGroups.add(group)
                }
            }
            // 对数据进行排序
            sortData(manageGroups)
            return manageGroups
        }
        return ArrayList()
    }

    /**
     * get all join groups, not contain manage groups
     * @return
     */
    fun getAllJoinGroups(allGroups: List<EMGroup>?): List<EMGroup> {
        if (allGroups != null && allGroups.size > 0) {
            val joinGroups: MutableList<EMGroup> = ArrayList()
            for (group in allGroups) {
                if (!TextUtils.equals(group.owner, currentUser) && !group.adminList.contains(
                        currentUser
                    )
                ) {
                    joinGroups.add(group)
                }
            }
            // 对数据进行排序
            sortData(joinGroups)
            return joinGroups
        }
        return ArrayList()
    }

    /**
     * 对数据进行排序
     * @param groups
     */
    private fun sortData(groups: List<EMGroup>) {
        Collections.sort(groups, object : Comparator<EMGroup> {
            override fun compare(o1: EMGroup, o2: EMGroup): Int {
                val name1 = EaseCommonUtils.getLetter(o1.groupName)
                val name2 = EaseCommonUtils.getLetter(o2.groupName)
                return if (name1 == name2) {
                    o1.groupId.compareTo(o2.groupId)
                } else {
                    if ("#" == name1) {
                        return 1
                    } else if ("#" == name2) {
                        return -1
                    }
                    name1.compareTo(name2)
                }
            }
        })
    }


    /**
     * 设置群组名称
     * @param groupId
     * @param groupName
     * @return
     */
    fun setGroupName(groupId: String?, groupName: String?): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncChangeGroupName(groupId, groupName, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(createLiveData(groupName))
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * 设置群公告
     * @param groupId
     * @param announcement
     * @return
     */
    fun setGroupAnnouncement(groupId: String?, announcement: String?): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncUpdateGroupAnnouncement(
                    groupId,
                    announcement,
                    object : EMCallBack {
                        override fun onSuccess() {
                            callBack.onSuccess(createLiveData(announcement))
                        }

                        override fun onError(code: Int, error: String) {
                            callBack.onError(code, error)
                        }

                        override fun onProgress(progress: Int, status: String) {}
                    })
            }
        }.asLiveData()
    }

    /**
     * 设置群描述
     * @param groupId
     * @param description
     * @return
     */
    fun setGroupDescription(groupId: String?, description: String?): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncChangeGroupDescription(
                    groupId,
                    description,
                    object : EMCallBack {
                        override fun onSuccess() {
                            callBack.onSuccess(createLiveData(description))
                        }

                        override fun onError(code: Int, error: String) {
                            callBack.onError(code, error)
                        }

                        override fun onProgress(progress: Int, status: String) {}
                    })
            }
        }.asLiveData()
    }

    /**
     * 获取共享文件
     * @param groupId
     * @param pageNum
     * @param pageSize
     * @return
     */
    fun getSharedFiles(
        groupId: String?,
        pageNum: Int,
        pageSize: Int
    ): LiveData<Resource<List<EMMucSharedFile?>?>> {
        return object : NetworkOnlyResource<List<EMMucSharedFile?>?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<List<EMMucSharedFile?>?>>) {
                groupManager!!.asyncFetchGroupSharedFileList(
                    groupId,
                    pageNum,
                    pageSize,
                    object : EMValueCallBack<List<EMMucSharedFile?>?> {
                        override fun onSuccess(value: List<EMMucSharedFile?>?) {
                            callBack.onSuccess(createLiveData(value))
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }


    /**
     * 下载共享文件
     * @param groupId
     * @param fileId
     * @param localFile
     * @return
     */
    fun downloadFile(
        groupId: String?,
        fileId: String?,
        localFile: File
    ): LiveData<Resource<File?>?>? {
        return object : NetworkOnlyResource<File?>() {

            override fun createCall(callBack: ResultCallBack<LiveData<File?>>) {
                groupManager!!.asyncDownloadGroupSharedFile(
                    groupId,
                    fileId,
                    localFile.absolutePath,
                    object : EMCallBack {
                        override fun onSuccess() {
                            callBack.onSuccess(createLiveData(localFile))
                        }

                        override fun onError(code: Int, error: String) {
                            callBack.onError(code, error)
                        }

                        override fun onProgress(progress: Int, status: String) {}
                    })
            }
        }.asLiveData()
    }
    /**
     * 删除服务器端的文件
     * @param groupId
     * @param fileId
     * @return
     */
    fun deleteFile(groupId: String?, fileId: String?): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                groupManager!!.asyncDeleteGroupSharedFile(groupId, fileId, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(createLiveData(true))
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * 上传文件
     * @param groupId
     * @param filePath
     * @return
     */
    fun uploadFile(groupId: String?, filePath: String?): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                groupManager!!.asyncUploadGroupSharedFile(groupId, filePath, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(createLiveData(true))
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * 邀请群成员
     * @param isOwner
     * @param groupId
     * @param members
     * @return
     */
    fun addMembers(
        isOwner: Boolean,
        groupId: String?,
        members: Array<String?>?
    ): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                if (isOwner) {
                    groupManager!!.asyncAddUsersToGroup(groupId, members, object : EMCallBack {
                        override fun onSuccess() {
                            callBack.onSuccess(createLiveData(true))
                        }

                        override fun onError(code: Int, error: String) {
                            callBack.onError(code, error)
                        }

                        override fun onProgress(progress: Int, status: String) {}
                    })
                } else {
                    groupManager!!.asyncInviteUser(groupId, members, null, object : EMCallBack {
                        override fun onSuccess() {
                            callBack.onSuccess(createLiveData(true))
                        }

                        override fun onError(code: Int, error: String) {
                            callBack.onError(code, error)
                        }

                        override fun onProgress(progress: Int, status: String) {}
                    })
                }
            }
        }.asLiveData()
    }

    /**
     * 移交群主权限
     * @param groupId
     * @param username
     * @return
     */
    fun changeOwner(groupId: String?, username: String?): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                groupManager!!.asyncChangeOwner(
                    groupId,
                    username,
                    object : EMValueCallBack<EMGroup?> {
                        override fun onSuccess(value: EMGroup?) {
                            callBack.onSuccess(createLiveData(true))
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 设为群管理员
     * @param groupId
     * @param username
     * @return
     */
    fun addGroupAdmin(groupId: String?, username: String?): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncAddGroupAdmin(
                    groupId,
                    username,
                    object : EMValueCallBack<EMGroup?> {
                        override fun onSuccess(value: EMGroup?) {
                            callBack.onSuccess(
                                createLiveData(
                                    context.getString(
                                        R.string.demo_group_member_add_admin,
                                        username
                                    )
                                )
                            )
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 移除群管理员
     * @param groupId
     * @param username
     * @return
     */
    fun removeGroupAdmin(groupId: String?, username: String?): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncRemoveGroupAdmin(
                    groupId,
                    username,
                    object : EMValueCallBack<EMGroup?> {
                        override fun onSuccess(value: EMGroup?) {
                            callBack.onSuccess(
                                createLiveData(
                                    context.getString(
                                        R.string.demo_group_member_remove_admin,
                                        username
                                    )
                                )
                            )
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 移出群
     * @param groupId
     * @param username
     * @return
     */
    fun removeUserFromGroup(groupId: String?, username: String?): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncRemoveUserFromGroup(groupId, username, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(
                            createLiveData(
                                context.getString(
                                    R.string.demo_group_member_remove,
                                    username
                                )
                            )
                        )
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * 添加到群黑名单
     * @param groupId
     * @param username
     * @return
     */
    fun blockUser(groupId: String?, username: String?): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncBlockUser(groupId, username, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(
                            createLiveData(
                                context.getString(
                                    R.string.demo_group_member_add_black,
                                    username
                                )
                            )
                        )
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * 移出群黑名单
     * @param groupId
     * @param username
     * @return
     */
    fun unblockUser(groupId: String?, username: String?): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncUnblockUser(groupId, username, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(
                            createLiveData(
                                context.getString(
                                    R.string.demo_group_member_remove_black,
                                    username
                                )
                            )
                        )
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * 禁言
     * @param groupId
     * @param usernames
     * @return
     */
    fun muteGroupMembers(
        groupId: String?,
        usernames: List<String?>,
        duration: Long
    ): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.aysncMuteGroupMembers(
                    groupId,
                    usernames,
                    duration,
                    object : EMValueCallBack<EMGroup?> {
                        override fun onSuccess(value: EMGroup?) {
                            callBack.onSuccess(
                                createLiveData(
                                    context.getString(
                                        R.string.demo_group_member_mute,
                                        usernames[0]
                                    )
                                )
                            )
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 禁言
     * @param groupId
     * @param usernames
     * @return
     */
    fun unMuteGroupMembers(
        groupId: String?,
        usernames: List<String?>
    ): LiveData<Resource<String?>> {
        return object : NetworkOnlyResource<String?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<String?>>) {
                groupManager!!.asyncUnMuteGroupMembers(
                    groupId,
                    usernames,
                    object : EMValueCallBack<EMGroup?> {
                        override fun onSuccess(value: EMGroup?) {
                            callBack.onSuccess(
                                createLiveData(
                                    context.getString(
                                        R.string.demo_group_member_remove_mute,
                                        usernames[0]
                                    )
                                )
                            )
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 退群
     * @param groupId
     * @return
     */
    fun leaveGroup(groupId: String?): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                groupManager!!.asyncLeaveGroup(groupId, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(createLiveData(true))
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * 解散群
     * @param groupId
     * @return
     */
    fun destroyGroup(groupId: String?): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                groupManager!!.asyncDestroyGroup(groupId, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(createLiveData(true))
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * create a new group
     * @param groupName
     * @param desc
     * @param allMembers
     * @param reason
     * @param option
     * @return
     */
    fun createGroup(
        groupName: String?,
        desc: String?,
        allMembers: Array<String?>?,
        reason: String?,
        option: EMGroupOptions?
    ): LiveData<Resource<EMGroup>> {
        return object : NetworkOnlyResource<EMGroup>() {
            override fun createCall(callBack: ResultCallBack<LiveData<EMGroup>>) {
                Log.i("Api","group"+groupManager)
                groupManager?.asyncCreateGroup(
                    groupName,
                    desc,
                    allMembers,
                    reason,
                    option,
                    object : EMValueCallBack<EMGroup> {
                        override fun onSuccess(value: EMGroup) {
                            Log.i("Api","group成功"+value.groupId)
                            callBack.onSuccess(createLiveData(value))
                        }

                        override fun onError(error: Int, errorMsg: String) {
                            Log.i("Api","group失败"+error+errorMsg)
                            callBack.onError(error, errorMsg)
                        }
                    })
            }
        }.asLiveData()
    }

    /**
     * 屏蔽群消息
     * @param groupId
     * @return
     */
    fun blockGroupMessage(groupId: String?): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                groupManager!!.asyncBlockGroupMessage(groupId, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(createLiveData(true))
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }

    /**
     * 取消屏蔽群消息
     * @param groupId
     * @return
     */
    fun unblockGroupMessage(groupId: String?): LiveData<Resource<Boolean?>> {
        return object : NetworkOnlyResource<Boolean?>() {
            protected override fun createCall(callBack: ResultCallBack<LiveData<Boolean?>>) {
                groupManager!!.asyncUnblockGroupMessage(groupId, object : EMCallBack {
                    override fun onSuccess() {
                        callBack.onSuccess(createLiveData(true))
                    }

                    override fun onError(code: Int, error: String) {
                        callBack.onError(code, error)
                    }

                    override fun onProgress(progress: Int, status: String) {}
                })
            }
        }.asLiveData()
    }
}

