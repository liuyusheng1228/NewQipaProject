package com.qipa.qipaimbase.session

import com.qipa.qipaimbase.session.isession.ISessionModel
import com.qipa.qipaimbase.session.isession.ISessionPresenter
import com.qipa.qipaimbase.session.isession.ISessionView
import com.qipa.qipaimbase.utils.http.jsons.JsonRequestResult
import com.qipa.qipaimbase.utils.http.jsons.JsonResult
import java.util.HashSet

class SessionPresenter(iView: ISessionView?) :
    ISessionPresenter<ISessionView?, ISessionModel?>(iView) {
    private var othersInfoSet: MutableSet<String>? = null
    override fun loadHistoryData() {
        getiModel()?.loadLocalHostoryMsg(object : ISessionModel.OnLoadHistoryListener{
            override fun onLoadHistory(sessionData: List<SessionData?>?) {
                getIView()?.onLoadHistory(sessionData as List<SessionData>?)
            }

        })
    }

    override fun getOthersInfo(sessionData: SessionData?) {
        if (othersInfoSet == null) {
            othersInfoSet = HashSet()
        }
        if (othersInfoSet!!.contains(sessionData?.chatWith)) {
            return
        }
        sessionData?.chatWith?.let { othersInfoSet?.add(it) }
        getiModel()?.getOtherInfo(sessionData,object : ISessionModel.OnGetOtherInfoListener{
            override fun onGetOtherInfo(result: JsonResult<JsonRequestResult>) {
                othersInfoSet?.remove(sessionData?.chatWith)
                getIView()?.onGetOtherInfoResult(result, sessionData)
            }

        } )
    }

    override fun saveSession(sessionData: SessionData?) {
        getiModel()!!.saveSession(sessionData)
    }

    override fun upDateSessions() {
        getiModel()?.loadLocalHostoryMsg(object : ISessionModel.OnLoadHistoryListener{
            override fun onLoadHistory(sessionData: List<SessionData?>?) {
                getIView()?.onLoadHistory(sessionData as List<SessionData>?)
            }

        })
    }

    override fun deleteSession(data: SessionData?) {
        getiModel()?.deleteSession(data,object : ISessionModel.OnDeleteSessionListener{
            override fun onDeleteSession() {
                getIView()?.onDeleteSession(data)
            }

        })
    }

    override fun clearSession(data: SessionData?) {
        getiModel()?.clearSession(data,object : ISessionModel.OnClearSessionListener{
            override fun onClearSession() {
                getIView()?.onClearSession(data)
            }

        })

    }

    override fun updateUnRead(chatWith: String?) {
//        iSessionModel.updateUnRead(chatWith);
    }

    override fun getNewSession(chatType: Int, chatWith: String?) {
        getiModel()?.getNewSession(chatType, chatWith, object : ISessionModel.OnGetSessionListener {
            override fun onGetSession(sessionData: SessionData?) {
                getIView()?.onNewSession(sessionData)
            }
        })
    }

    override fun getSessionUnRead(chatType: Int, chatWith: String?) {
//        iSessionModel.getUnReadAndAllUnRead();
    }

    override val allUnReadCount: Unit
        get() {
            getiModel()?.getAllUnReadCount(object : ISessionModel.OnGetAllUnReadCount {
                override fun onGetAllUnReadCount(result: Int) {
                    getIView()?.onGetAllUnReadCount(result)
                }
            })
        }

    override fun clearSesionUnReadCount(chatType: Int, chatWith: String?) {
        getiModel()?.updateSessionUnreadCount(chatType, chatWith, 0)
    }

    override fun resendSendingStatusMsgs() {
        getiModel()?.resendSendingStatusMsgs()
    }

    override fun updateSessionAtType(sessionData: SessionData?) {
        getiModel()?.updateSessionAtType(sessionData)
    }

    override fun loadHistoryFromRemote() {
        getiModel()?.loadHistoryFromRemote(object : ISessionModel.OnLoadHistoryFromRemoteListener {
            override fun onLoadHistoryFromRemote(sessionData: List<SessionData?>?) {
                getIView()?.onLoadHistoryFromRemote(sessionData as List<SessionData>?)
            }
        })
    }

    override fun generateIModel(): ISessionModel {
        return SessionModel()
    }
}

