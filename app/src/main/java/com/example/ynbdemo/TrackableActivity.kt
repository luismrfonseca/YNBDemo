package com.example.ynbdemo

import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.ar.core.*
import com.google.ar.core.exceptions.*
import com.google.ar.sceneform.ArSceneView
import com.google.ar.sceneform.FrameTime
import com.google.ar.sceneform.Scene
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException
import java.io.InputStream


class TrackableActivity : AppCompatActivity(), Scene.OnUpdateListener {

    private var arView: ArSceneView? = null;
    private var session: Session? = null;
    private var shouldConfigureSession: Boolean = false;


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tackable)

        // View
        arView = findViewById(R.id.arView) as ArSceneView?

        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        setupSession();
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(this@TrackableActivity, "Permission need to display camera", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) { /* ... */
                    }
                }).check()

        initSceneView()
    }

    private fun initSceneView() {
        arView?.scene?.addOnUpdateListener(this)
    }

    private fun setupSession() {
        if(session == null){
            try {
                session = Session(this)
            } catch (e: UnavailableArcoreNotInstalledException) {
                e.printStackTrace()
            } catch (e: UnavailableApkTooOldException) {
                e.printStackTrace()
            } catch (e: UnavailableSdkTooOldException) {
                e.printStackTrace()
            } catch (e: UnavailableDeviceNotCompatibleException) {
                e.printStackTrace()
            }
            shouldConfigureSession = true
        }
        if(shouldConfigureSession) {
            configSession();
            shouldConfigureSession = false
            arView?.setupSession(session)
        }

        try {
            session?.resume()
            arView?.resume()
        } catch (e: CameraNotAvailableException) {
            e.printStackTrace()
            session = null
            return
        }
    }

    private fun configSession() {
        var config: Config = Config(session);
        if(!buildDatabase(config)){
            Toast.makeText(this@TrackableActivity, "Error database", Toast.LENGTH_SHORT).show()
        }
        config.setUpdateMode(Config.UpdateMode.LATEST_CAMERA_IMAGE)
        session?.configure(config)
    }

    private fun buildDatabase(config: Config): Boolean {
        var augmentedImageDatabase: AugmentedImageDatabase
        var bitmap: Bitmap? = loadImage()
        if(bitmap == null) {
            return false;
        }
        augmentedImageDatabase = AugmentedImageDatabase(session)
        augmentedImageDatabase.addImage("logo", bitmap)
        config.setAugmentedImageDatabase(augmentedImageDatabase)
        return true
    }

    private fun loadImage(): Bitmap? {
        try{
            var inputstream: InputStream = this.assets.open("logo.jpg")
            return BitmapFactory.decodeStream(inputstream)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    override fun onUpdate(frameTime: FrameTime?) {
        var frame: Frame? = arView?.arFrame
        var updateAugmentedImg = frame?.getUpdatedTrackables(AugmentedImage::class.java) as Collection<AugmentedImage>

        for(image:AugmentedImage in updateAugmentedImg) {
            if(image.trackingState == TrackingState.TRACKING){
                if(image.name.equals("logo")){
                    var node:MyARNode = MyARNode(this, R.raw.toworkfor)
                    node.image = image
                    arView?.scene?.addChild(node)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()

        Dexter.withContext(this)
                .withPermission(Manifest.permission.CAMERA)
                .withListener(object : PermissionListener {
                    override fun onPermissionGranted(response: PermissionGrantedResponse) {
                        setupSession();
                    }

                    override fun onPermissionDenied(response: PermissionDeniedResponse) {
                        Toast.makeText(this@TrackableActivity, "Permission need to display camera", Toast.LENGTH_SHORT).show()
                    }

                    override fun onPermissionRationaleShouldBeShown(permission: PermissionRequest, token: PermissionToken) { /* ... */
                    }
                }).check()
    }

    override fun onPause() {
        super.onPause()
        if(session!=null){
            arView?.pause()
            session?.pause()
        }
    }
}
