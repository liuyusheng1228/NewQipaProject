package com.qipa.newboxproject.ui.fragment.me.set

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import com.cy.translucentparent.StatusNavigationUtils
import com.giftedcat.picture.lib.photoview.GlideImageLoader
import com.giftedcat.picture.lib.photoview.style.index.NumberIndexIndicator
import com.giftedcat.picture.lib.photoview.style.progress.ProgressBarIndicator
import com.giftedcat.picture.lib.photoview.transfer.TransferConfig
import com.giftedcat.picture.lib.photoview.transfer.Transferee
import com.giftedcat.picture.lib.selector.MultiImageSelector
import com.qipa.jetpackmvvm.ext.nav
import com.qipa.jetpackmvvm.ext.navigateAction
import com.qipa.newboxproject.R
import com.qipa.newboxproject.app.base.BaseFragment
import com.qipa.newboxproject.app.weight.dialog.CommonDialog
import com.qipa.newboxproject.app.weight.dialog.ShowBottomDialog
import com.qipa.newboxproject.databinding.FragmentPersonalInformationBinding
import com.qipa.newboxproject.viewmodel.state.PersonalInformationModel
import kotlinx.android.synthetic.main.fragment_personal_information.*
import kotlinx.android.synthetic.main.include_back.*
import com.github.gzuliyujiang.wheelpicker.DatePicker
import com.github.gzuliyujiang.wheelpicker.contract.OnDatePickedListener
import com.qipa.newboxproject.app.util.DpPxUtils

import com.github.gzuliyujiang.wheelpicker.SexPicker
import com.github.gzuliyujiang.wheelpicker.annotation.DateMode
import com.github.gzuliyujiang.wheelpicker.contract.OnOptionPickedListener
import com.github.gzuliyujiang.wheelpicker.entity.DateEntity
import com.github.gzuliyujiang.wheelpicker.impl.UnitDateFormatter
import com.qipa.jetpackmvvm.callback.databind.StringObservableField
import com.qipa.newboxproject.app.appViewModel
import com.qipa.newboxproject.app.ext.showCustomDialog
import com.qipa.newboxproject.app.network.NetworkApi
import com.qipa.newboxproject.app.upload.bean.UserInfoUploadBean
import com.qipa.newboxproject.app.util.CacheUtil
import com.qipa.newboxproject.app.util.ColorUtil
import com.qipa.newboxproject.app.weight.dialog.RealNameAuthenticationDialog
import com.qipa.newboxproject.viewmodel.request.RequestLoginRegisterViewModel
import com.qipa.qipaimbase.chat.ChatBaseActivity
import com.qipa.qipaimbase.chat.ChatData
import com.qipa.qipaimbase.session.PhotonIMMessage
import com.qipa.qipaimbase.utils.FileUtils
import com.qipa.qipaimbase.utils.ToastUtils
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class PersonalInformationFragment : BaseFragment<PersonalInformationModel,FragmentPersonalInformationBinding>(),
    ShowBottomDialog.OnClickBottomListener , OnDatePickedListener, OnOptionPickedListener {
    override fun layoutId() = R.layout.fragment_personal_information
    private val REQUEST_IMAGE = 2
    private var mSelectList: MutableList<String>? = arrayListOf()
    private val showBottomDialog : ShowBottomDialog = ShowBottomDialog()
    protected var transferee: Transferee? = null
    protected var config: TransferConfig? = null
    private val requestLoginRegisterViewModel : RequestLoginRegisterViewModel by viewModels()
    private var mImageUri: Uri? = null
    private var imageFile: File? = null
    override fun initView(savedInstanceState: Bundle?) {
        mDatabind.vm = mViewModel
        mDatabind.click = ProClick()
        rel_detail_back.setOnClickListener {
            nav().navigateUp()
        }
        StatusNavigationUtils.setStatusBarColor(mActivity,getResources().getColor(R.color.white_f9))
        initTransfer(null)
        showBottomDialog.setOnClickBottomListener(this)
        toolbar_titletv.text = resources.getString(R.string.personal_center)
    }

    override fun onBackPressed(): Boolean {
        return false
    }

    override fun initData() {
        super.initData()
        refreshUserinfo()
    }

    override fun createObserver() {
        super.createObserver()

        requestLoginRegisterViewModel.run {
            uploadPicResult.observe(this@PersonalInformationFragment,{
                var userInfoUploadBean = UserInfoUploadBean(CacheUtil.getUser()?.userId,it.url,null,null,null,null,null)
                requestLoginRegisterViewModel.updateUserInfo(userInfoUploadBean)
            })

            uploadUserResult.observe(this@PersonalInformationFragment,{
                CacheUtil.getUser()?.userId?.let { it1 ->
                    requestLoginRegisterViewModel.getUserInfo(
                        it1
                    )
                }
            })

            wxUserInfoBean.observe(this@PersonalInformationFragment,{
                CacheUtil.setUser(it)
                appViewModel.userInfo.value = it
                refreshUserinfo()
            })


        }
    }

    private fun refreshUserinfo(){
        account_number.info = CacheUtil.getUser()?.userId
        user_nickname.info = CacheUtil.getUser()?.nickname
        mobile_phone.info = CacheUtil.getUser()?.userMobile
        mViewModel.imageUrl?.set(CacheUtil.getUser()?.headPic)
        if(CacheUtil.getUser()?.identityCard != null){
            real_name_authentication.info = getString(R.string.has_real_name_authentication)
        }else{
            real_name_authentication.info = getString(R.string.not_real_name_authentication)
        }
        if(CacheUtil.getUser()?.birthdayTime !=  null){
            birthday_date.info = CacheUtil.getUser()?.birthdayTime
        }
        when(CacheUtil.getUser()?.gender){
            0 -> gender.info = resources.getString(R.string.male)
            1 -> gender.info = getString(R.string.female)
        }
    }


    inner class ProClick{
        /**
         * ??????
         */
        fun head_portrait(){
            showBottomDialog.BottomDialog(mActivity)
        }

        /**
         * ??????
         */
        fun setNickName(){
            val dialog = CommonDialog(context)
            CacheUtil.getUser()?.nickname?.let {
                dialog.setTitle(resources.getString(R.string.modify_nickname))
                    .setInputMessage(it)
                    .setSingle(false).setShowInput(true).setOnClickBottomListener(object : CommonDialog.OnClickBottomListener {

                        override fun onPositiveClick(inputmsg: String) {
                            var userInfoUploadBean = UserInfoUploadBean(CacheUtil.getUser()?.userId,null,inputmsg,null,null,null,null)
                            requestLoginRegisterViewModel.updateUserInfo(userInfoUploadBean)
                            dialog.dismiss()
                        }

                        override fun onNegtiveClick() {
                            dialog.dismiss()
                        }
                    }).show()
            }
        }

        /**
         * ????????????
         */

        fun unBindPhone(){
            if(CacheUtil.getIsBindPhone()){
                nav().navigateAction(R.id.action_personinfor_to_unbandPhoneFragment,
                    Bundle().apply {
                        putString("type","" )
                    })
            }else{
                nav().navigateAction(R.id.action_personinfor_to_BandPhoneFragment)
            }

        }

        /**
         * ????????????
         */

        fun updatapwd(){
            nav().navigateAction(R.id.action_personinfor_to_unbandPhoneFragment,
                    Bundle().apply {
                        putString("type","" )
                    })
        }

        /**
         * ????????????
         */
        fun realNameAuthentication(){

            if(CacheUtil.getUser()?.identityCard != null){
                nav().navigateAction(R.id.action_personalInformationFragment_to_realNameAuthenticationFragment)
            }else{
                val realNameAuthenticationDialog = RealNameAuthenticationDialog(context)
                realNameAuthenticationDialog.setOnClickAuthentication(object :
                    RealNameAuthenticationDialog.OnClickAuthentication{
                    override fun onClickAuthentication(
                        real_name: String,
                        id_card_name: String,
                        code: String
                    ) {
                        var userId = CacheUtil.getUser()?.userId
                        var phone = CacheUtil.getUser()?.userMobile
                        userId?.let {
                            if (phone != null) {
                                requestLoginRegisterViewModel.userRealName(it,phone,id_card_name,real_name,code)
                                realNameAuthenticationDialog.dismiss()
                            }
                        }
                    }

                    override fun onClickCode() {
                        CacheUtil.getUser()?.userMobile?.let {
                            requestLoginRegisterViewModel.getCode(
                                it
                            )
                        }
                    }

                })
                realNameAuthenticationDialog.show()
            }
        }

        fun logout(){
            showCustomDialog(resources.getString(R.string.exit_current_account),resources.getString(R.string.reminder),
                "",0,false,false,"","",{
                CacheUtil.setUser(null)
                NetworkApi.INSTANCE.cookieJar.clear()
                appViewModel.userInfo.value = null
            })
        }

        fun brithDaySelect(){
            val picker = DatePicker(mActivity)
//            val picker = BirthdayPicker(mActivity)
//            picker.setDefaultValue(1999, 11, 11)
            picker.wheelLayout.setDateMode(DateMode.YEAR_MONTH_DAY);
            picker.wheelLayout.setRange(DateEntity.target(1980, 1, 1), DateEntity.target(2050, 12, 31), DateEntity.today())
            picker.wheelLayout.setDateLabel(resources.getString(R.string.year), resources.getString(R.string.month), resources.getString(R.string.day))
            picker.wheelLayout.setDateFormatter(UnitDateFormatter())
            picker.wheelLayout.setResetWhenLinkage(false)

            picker.okView.textSize = DpPxUtils.px2Dp(mActivity,30).toFloat()
            picker.cancelView.textSize = DpPxUtils.px2Dp(mActivity,30).toFloat()
            picker.wheelLayout.setStyle(R.style.WheelDefault)
            picker.setOnDatePickedListener(this@PersonalInformationFragment)
            picker.show()
        }

        fun onSelectSex(){
            val picker = SexPicker(mActivity)
            picker.setBodyWidth(140)
            picker.setIncludeSecrecy(false)
            picker.setDefaultValue(getString(R.string.female))
            picker.wheelLayout.setStyle(R.style.WheelDefault)
            picker.setOnOptionPickedListener(this@PersonalInformationFragment)
            picker.wheelLayout.setOnOptionSelectedListener { position, item ->
                picker.titleView.text = picker.wheelView.formatItem(position)
            }
            picker.okView.textSize = DpPxUtils.px2Dp(mActivity,30).toFloat()
            picker.cancelView.textSize = DpPxUtils.px2Dp(mActivity,30).toFloat()
            picker.show()
        }


    }

    override fun onTakePhotoClick() {
        onTakePic()
    }
    fun onTakePic() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) //???????????????Intent
        if (takePhotoIntent.resolveActivity(activity?.getPackageManager()!!) != null) { //???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            imageFile = FileUtils.createImageFile(requireActivity()) //?????????????????????????????????
            if (imageFile != null) {
                mImageUri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    /*7.0???????????????FileProvider???File?????????Uri*/
                    FileProvider.getUriForFile(
                        requireActivity(),
                        FILE_PROVIDER_AUTHORITY, imageFile!!)
                } else {
                    /*7.0?????????????????????Uri???fromFile?????????File?????????Uri*/
                    Uri.fromFile(imageFile)
                }
                takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri) //????????????????????????Uri???????????????
                startActivityForResult(takePhotoIntent, REQUEST_CAMERA) //????????????
            }
        }
    }
    override fun onViewBigPicture() {
        if(mSelectList?.size!! < 2){
            mSelectList?.add("")
        }
        config?.setNowThumbnailIndex(0)
        config?.setSourceImageList(mSelectList)
        transferee?.apply(config)?.show()
    }

    override fun onTakePicClick() {
         pickImage()
    }

    /**
     * ????????????????????????
     */
    private fun pickImage() {
        val selector = MultiImageSelector.create(context)
        selector.showCamera(true)
        selector.count(1)
        selector.multi()
        selector.origin(mSelectList)
        selector.start(this, REQUEST_IMAGE)
    }

    /**
     * ???????????????????????????
     */
    private fun initTransfer(rvImages: RecyclerView?) {
        CacheUtil.getUser()?.headPic?.let { mSelectList?.add(it) }

        transferee = Transferee.getDefault(mActivity)
        config = TransferConfig.build()
            .setSourceImageList(mSelectList)
            .setProgressIndicator(ProgressBarIndicator())
            .setIndexIndicator(NumberIndexIndicator())
            .setImageLoader(GlideImageLoader.with(mActivity.applicationContext))
            .setJustLoadHitImage(true)
            .bindImageView(user_img,CacheUtil.getUser()?.headPic)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                val select: ArrayList<String>? =
                    data?.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT)
                mSelectList?.clear()
                if (select != null) {
                    mSelectList?.addAll(select)
                }

                mSelectList?.get(0)?.let { requestLoginRegisterViewModel.reUpLoadFile(it) }
            }
        }else if (requestCode == REQUEST_CAMERA && resultCode == RESULT_OK) {
            requestLoginRegisterViewModel.reUpLoadFile(imageFile!!.absolutePath)
        }
    }

    override fun onDatePicked(year: Int, month: Int, day: Int) {
        val date: String = ""+year+"-"+month+"-"+day
        var userInfoUploadBean = UserInfoUploadBean(CacheUtil.getUser()?.userId,null,null,null,null,null,date)
        requestLoginRegisterViewModel.updateUserInfo(userInfoUploadBean)

    }

    override fun onOptionPicked(position: Int, item: Any?) {
        var gender : Int? = 0
        gender = position
        var userInfoUploadBean = UserInfoUploadBean(CacheUtil.getUser()?.userId,null,null,gender,null,null,null)
        requestLoginRegisterViewModel.updateUserInfo(userInfoUploadBean)

    }

    companion object{
        private const val FILE_PROVIDER_AUTHORITY = "com.qipa.newboxproject.fileprovider"
        private const val REQUEST_CAMERA = 1000
    }


}