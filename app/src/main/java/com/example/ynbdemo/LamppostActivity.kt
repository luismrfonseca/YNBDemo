package com.example.ynbdemo

import android.app.ActionBar
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.internal.ContextUtils.getActivity
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode


class LamppostActivity : AppCompatActivity() {

    private val TAG: String = LamppostActivity::class.java.simpleName
    private val MIN_OPENGL_VERSION = 3.0
    var arFragment: ArFragment? = null
    var lampPostRenderable: ModelRenderable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!checkIsSupportedDeviceOrFinish(this)) {
            return
        }
        setContentView(R.layout.activity_lamppost)

        arFragment = supportFragmentManager.findFragmentById(R.id.ux_fragment) as ArFragment?
        ModelRenderable.builder()
            .setSource(this, R.raw.lamppost)
            .build()
            .thenAccept { renderable: ModelRenderable ->
                lampPostRenderable = renderable
            }
            .exceptionally { throwable: Throwable? ->
                val toast =
                    Toast.makeText(this, "Unable to load andy renderable", Toast.LENGTH_LONG)
                toast.setGravity(Gravity.CENTER, 0, 0)
                toast.show()
                null
            }

        arFragment!!.setOnTapArPlaneListener { hitresult: HitResult, plane: Plane?, motionevent: MotionEvent? ->
            if (lampPostRenderable == null) {
                return@setOnTapArPlaneListener
            }
            val anchor = hitresult.createAnchor()
            val anchorNode = AnchorNode(anchor)
            anchorNode.setParent(arFragment!!.arSceneView.scene)
            val lamp =
                TransformableNode(arFragment!!.transformationSystem)
            lamp.setParent(anchorNode)
            lamp.getScaleController().setMaxScale(0.50f);
            lamp.getScaleController().setMinScale(0.04f);
            lamp.renderable = lampPostRenderable
            lamp.select()
        }

        val actionBar: androidx.appcompat.app.ActionBar? = (this as AppCompatActivity).supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
        actionBar?.setHomeButtonEnabled(true)

        (this as LamppostActivity).supportActionBar!!.show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun checkIsSupportedDeviceOrFinish(activity: Activity): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            Log.e(TAG, "Sceneform requires Android N or later")
            Toast.makeText(activity, "Sceneform requires Android N or later", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        val openGlVersionString = (activity?.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager).deviceConfigurationInfo.glEsVersion
        if (openGlVersionString.toDouble() < MIN_OPENGL_VERSION) {
            Log.e(TAG, "Sceneform requires OpenGL ES 3.0 later")
            Toast.makeText(activity, "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG).show()
            activity.finish()
            return false
        }
        return true
    }
}