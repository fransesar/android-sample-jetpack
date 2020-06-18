package com.example.jetpack.ui.detailjetpack


import android.app.PendingIntent
import android.content.DialogInterface
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.telephony.SmsManager
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.palette.graphics.Palette
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.example.jetpack.MainActivity
import com.example.jetpack.R
import com.example.jetpack.databinding.FragmentDetailBinding
import com.example.jetpack.databinding.SendSmsDialogBinding
import com.example.jetpack.data.JetpackModel
import com.example.jetpack.data.JetpackPalette
import com.example.jetpack.data.SMSInfo

class DetailFragment : Fragment() {

    private lateinit var viewModel: DetailViewModel
    private lateinit var dataBinding: FragmentDetailBinding
    private var jetpackUUID = 0
    private var sendSMSStarted = false
    private var currentData: JetpackModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        dataBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail, container, false)
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            jetpackUUID = DetailFragmentArgs.fromBundle(
                it
            ).jetpackUUID
        }

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)
        viewModel.fetch(jetpackUUID)

        observeViewModel()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.detail_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_send_sms -> {
                sendSMSStarted = true
                (activity as MainActivity).checkSMSPermission()
            }
            R.id.action_share -> {
                val intent = Intent(Intent.ACTION_SEND)
                intent.type = "text/plain"
                intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this jetpack")
                intent.putExtra(Intent.EXTRA_TEXT, "${currentData?.jetpackName}")
                intent.putExtra(Intent.EXTRA_STREAM, "${currentData?.jetpackImage}")
                startActivity(Intent.createChooser(intent, "Share with"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun observeViewModel() {
        viewModel.jetpackLiveData.observe(viewLifecycleOwner, Observer {
            currentData = it
            it?.let {
                dataBinding.detail = it

                it.jetpackImage?.let { imageUrl ->
                    setupBackgroundColor(imageUrl)
                }
            }
        })
    }

    private fun setupBackgroundColor(url: String) {
        Glide.with(this)
            .asBitmap()
            .load(url)
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {}

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    Palette.from(resource)
                        .generate { palette ->
                            val intColor = palette?.vibrantSwatch?.rgb ?: 0
                            val myPalette = JetpackPalette(intColor)
                            dataBinding.palette = myPalette
                        }
                }
            })
    }

    fun onPermissionResult(permissionGranted: Boolean) {
        if (sendSMSStarted && permissionGranted) {
            context?.let {
                val smsInfo =
                    SMSInfo("", "${currentData?.jetpackName}", currentData?.jetpackImage!!)

                val dialogBinding = DataBindingUtil.inflate<SendSmsDialogBinding>(
                    LayoutInflater.from(it),
                    R.layout.send_sms_dialog,
                    null,
                    false
                )

                AlertDialog.Builder(it)
                    .setView(dialogBinding.root)
                    .setPositiveButton("Send SMS") { _: DialogInterface, _: Int ->
                        if (!dialogBinding.smsDestination.text.isNullOrEmpty()) {
                            smsInfo.to = dialogBinding.smsDestination.text.toString()
                            sendSMS(smsInfo)
                        }
                    }
                    .setNegativeButton("Cancel") { _: DialogInterface, _: Int -> }
                    .show()

                dialogBinding.smsInfo = smsInfo
            }
        }
    }

    private fun sendSMS(smsInfo: SMSInfo) {
        val intent = Intent(context, MainActivity::class.java)
        val pi = PendingIntent.getActivity(context, 0, intent, 0)
        val sms = SmsManager.getDefault()
        sms.sendTextMessage(smsInfo.to, null, smsInfo.text, pi, null)
    }
}
