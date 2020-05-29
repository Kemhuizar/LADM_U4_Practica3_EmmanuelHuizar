package mx.edu.ittepic.ladm_u4_practica3_emmanuelhuizar

class HiloControl (p:MainActivity) : Thread(){
    private var iniciado = false
    private var puntero = p

    override fun run() {
        super.run()
        iniciado = true
        while(iniciado){
            sleep(10000)
                puntero.runOnUiThread {
                    puntero.ConsultaSolicitud()
            }
        }
    }
}