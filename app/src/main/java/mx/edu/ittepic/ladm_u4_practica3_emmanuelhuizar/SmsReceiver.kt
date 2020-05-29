package mx.edu.ittepic.ladm_u4_practica3_emmanuelhuizar

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import com.google.firebase.firestore.FirebaseFirestore

class SmsReceiver: BroadcastReceiver(){
    var baseRemota = FirebaseFirestore.getInstance()

    override fun onReceive(context: Context, intent: Intent) {
        val extras = intent.extras
        if (extras!=null){
            var sms = extras.get("pdus") as Array<Any>

            for(indice in sms.indices){
                var formato = extras.getString("format")

                var smsMensaje = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    SmsMessage.createFromPdu(sms[indice] as ByteArray,formato)
                }else{
                    SmsMessage.createFromPdu(sms[indice] as ByteArray)
                }
                var celularOrigen = smsMensaje.originatingAddress
                var contenidoSMS = smsMensaje.messageBody.toString()

                baseRemota.collection("Mensajes").document("7YEmUHbHaME8LNdM977N")
                    .update(
                        "Telefono",celularOrigen,
                        "Texto",contenidoSMS
                    )
                /*baseRemota.collection("Mensajes")
                    .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                        if (firebaseFirestoreException!=null){
                            var data = hashMapOf(
                                "Telefono" to celularOrigen,
                                "Texto" to contenidoSMS
                            )
                            baseRemota.collection("Mensajes").add(data)
                                .addOnSuccessListener {
                                    Toast.makeText(context,"Se capturo",Toast.LENGTH_LONG).show()
                                }
                                .addOnFailureListener {
                                    Toast.makeText(context,"ERROR no se capturo",Toast.LENGTH_LONG).show()
                                }
                        }
                        for(document in querySnapshot!!){
                            baseRemota.collection("Mensaje").document(document.id)
                                .update(
                                    "Telefono",celularOrigen,
                                    "Texto",contenidoSMS
                                )
                        }
                    }*/
            }
        }
    }

}