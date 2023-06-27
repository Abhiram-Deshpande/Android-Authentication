package com.example.biometricauthentication

import android.Manifest
import android.app.KeyguardManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.biometrics.BiometricPrompt
import android.hardware.biometrics.BiometricPrompt.Builder
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat



class MainActivity : AppCompatActivity() {

    private var _authenticationCallback: BiometricPrompt.AuthenticationCallback? = null

     var authenticationCallback :BiometricPrompt.AuthenticationCallback? = null
        @RequiresApi(Build.VERSION_CODES.P)
        get() {
            if (_authenticationCallback == null) {
                _authenticationCallback = object : BiometricPrompt.AuthenticationCallback() {

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence?) {
                        super.onAuthenticationError(errorCode, errString)
                    }

                    override fun onAuthenticationHelp(helpCode: Int, helpString: CharSequence?) {
                        super.onAuthenticationHelp(helpCode, helpString)
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult?) {
                        super.onAuthenticationSucceeded(result)
                    }

                }
            }
            return _authenticationCallback!!
        }

    private var cancellationSignal:CancellationSignal? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    private fun checkBiometricSupport(): Boolean {

        val keyGuardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!keyGuardManager.isDeviceSecure) {
            notifyUser("Lock screen security not enables in Settings")
            return false
        }
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.USE_BIOMETRIC
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notifyUser("Fingerprint Authentication Permission Not enabled")
            return false
        }

        return packageManager.hasSystemFeature(PackageManager.FEATURE_FINGERPRINT)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun authenticateUser(view: View) {

        val biometricPrompt = BiometricPrompt.Builder(this)
            .setTitle("Biometric Demo")
            .setSubtitle("Authentication is required to continue")
            .setDescription("This application uses Biometric authentication to protect your data")
            .setNegativeButton("cancel",this.mainExecutor){
                _,_->notifyUser("Authentication Cancelled")
            }.build()

            if(authenticationCallback!=null)
            {
                biometricPrompt.authenticate(getCancellationSignal(),mainExecutor,authenticationCallback)
            }
        else{
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show();
            }


    }

    fun notifyUser(string: String) {
        Toast.makeText(this, string, Toast.LENGTH_LONG).show()
    }

    private fun getCancellationSignal():CancellationSignal{

        cancellationSignal = CancellationSignal()
            cancellationSignal?.setOnCancelListener {
                notifyUser("Cancelled via signal")
            }


        return cancellationSignal as CancellationSignal
    }



}
