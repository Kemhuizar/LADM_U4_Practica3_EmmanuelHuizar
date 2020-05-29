package mx.edu.ittepic.ladm_u4_practica3_emmanuelhuizar

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    var baseRemota = FirebaseFirestore.getInstance()
    var hiloControl : HiloControl?=null
    val siPermiso = 1
    val siPermisoReceiver = 2
    val siPermisoLectura = 3
    var TextoM =""
    var NumeroM=""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), siPermiso)
        }

        hiloControl = HiloControl(this)
        hiloControl?.start()
    }

    fun ConsultaSolicitud() {
        baseRemota.collection("Mensajes")
            .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                if (firebaseFirestoreException!=null){
                    return@addSnapshotListener
                }
                for(document in querySnapshot!!){
                    TextoM=document.getString("Texto").toString()
                    NumeroM=document.getString("Telefono").toString()
                }
            }
        if(NumeroM != ""){
            if (TextoM != ""){
                var nom=""
                var ape=""
                var pre=""
                var pre2=""
                var pre3=""
                var pre4=""
                var MensajeE=""
                var ax=2
                var array = TextoM.split(" ")
                if (array[0]=="CONSULTA" && array.size == 3){
                    baseRemota.collection("Clientes").whereEqualTo("IDusuario",array[1])
                        .addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                            if (firebaseFirestoreException!=null){
                                return@addSnapshotListener
                            }
                            for(document in querySnapshot!!){
                                ax=1
                                nom=document.getString("Nombres")!!
                                ape=document.getString("Apellidos")!!
                                pre4=document.get("Prestamos.TipoPago").toString()
                                pre=document.get("Prestamos.Prestamo").toString()!!
                                pre2=document.get("Prestamos.Pagos").toString()
                                pre3=document.get("Prestamos.PagosRealizados").toString()
                                if (array[2]=="SALDO"){
                                    MensajeE=nom+ape+" haz cubierto $${pre2.toInt()*pre3.toInt()}.00 pesos de la deuda total de $${pre}.00 pesos, tu siguiente pago es de $${pre2}.00 pesos en un plazo de pago "+pre4

                                }else{
                                    MensajeE="No escribio alguna consulta valida"
                                }
                                SmsManager.getDefault().sendTextMessage(NumeroM, null, MensajeE, null, null)
                            }
                        }
                }
                else{
                    var Mensaje="No se establecio el formato correcto para la consulta"
                    SmsManager.getDefault().sendTextMessage(NumeroM, null, Mensaje, null, null)
                }
                if(ax==2){
                    MensajeE="No se encontro conincidencia, con el usuario solicitado verifique"
                    SmsManager.getDefault().sendTextMessage(NumeroM, null, MensajeE, null, null)
                }
                baseRemota.collection("Mensajes").document("7YEmUHbHaME8LNdM977N").update( "Telefono","","Texto","" )
            }
            else{
                TextoM="No se pude procesar la solicitud"
                SmsManager.getDefault().sendTextMessage(NumeroM, null, TextoM, null, null)
            }
        }
        TextoM =""
        NumeroM=""
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode==siPermiso){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.RECEIVE_SMS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.RECEIVE_SMS),siPermisoReceiver)
            }
        }
        if(requestCode==siPermisoReceiver){
            if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_SMS)!= PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_SMS),siPermisoLectura)
            }
        }
    }
}
