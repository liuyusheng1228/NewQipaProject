package com.qipa.qipaimbase.utils.mvpbase

import com.qipa.qipaimbase.utils.LogUtils
import java.lang.IllegalStateException
import java.lang.ref.WeakReference

abstract class IPresenter<V : IView?, M : IModel?>(iView: V) {
    protected var iView: WeakReference<V?>? = null
    private var iModel: M? = null
    private val emptyView: V?

    //    @Override
    protected fun init(iView: V, iModel: M) {
        this.iView = WeakReference(iView)
        this.iModel = iModel
    }

    //    @Override
    fun getIView(): V? {
        return if (iView!!.get() == null) emptyView else iView!!.get()
    }

    //    @Override
    fun getiModel(): M? {
        return iModel
    }

    //    protected abstract void init(V iView, M iModel);
    //
    abstract fun generateIModel(): M

    //    public abstract V getIView();
    abstract fun getEmptyView(): V

    companion object {
        private const val TAG = "IPresenter"
    }

    init {
        val iModel: M = generateIModel() ?: throw IllegalStateException("iMode is null")
        init(iView, iModel)
        emptyView = getEmptyView()
        if (emptyView == null) {
            LogUtils.log(TAG, "may cause null pointer!!")
        }
    }
}
