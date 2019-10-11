package com.metal.chartapp.ui

import android.os.Bundle
import android.view.View
import com.kaopiz.kprogresshud.KProgressHUD
import com.nes.transfragment.BaseTransFragment
import kotlinx.android.synthetic.main.screen_title.*


open class BaseFragment : BaseTransFragment() {
    val progressHUD by lazy {
        KProgressHUD.create(context)
            .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            .setLabel("Please wait")
            .setDetailsLabel("Preparing data")
            .setCancellable(true)
            .setAnimationSpeed(2)
            .setDimAmount(0.5f)
    }

    override fun getFragmentContainer(): Int {
        return com.metal.chartapp.R.id.fragmentContainer
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        buttonLeft.setOnClickListener {
            performBack()
        }

    }

    override fun showProgress() {
//        progressHUD.show()
    }

    override fun hideProgress() {
//        progressHUD.dismiss()
    }
}