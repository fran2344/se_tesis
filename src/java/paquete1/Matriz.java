package paquete1;

import WS.translator;
import java.io.*;
import org.json.JSONObject;
import DB.Conexion;
import static DB.accion_log.*;

public class Matriz implements java.io.Serializable {

    public int id;
    public Nodo[][] matrix = new Nodo[72][4];
    public int bandera = 0;
    public String posicion_g = "";
//bandera nos indica en que dia del mes estamos es decir
//primer lunes del mes o segundo o tercero

    public Matriz() {

    }

    public String consulta(String Hora, String P, int bandera) { //aqui deberia ir tambien el parametro perfil (hora y posición)
        String info = "";
        String[] hora_minuto = Hora.split(":");
        int Hours = Integer.parseInt(hora_minuto[0]);
        int Minute = Integer.parseInt(hora_minuto[1]);
        int H = convertir_minuto(Minute);
        H = Hours + H;

        if (matrix[H][bandera] != null) { //si la posicion consultada esta llena
            if (compararPosiciones(matrix[H][bandera].posicion, P)) { //si la posicion es la misma ya guardada
                info = "$" + matrix[H][bandera].perfil;
            } else { //la posicion si esta ocupada pero la posicion no es la misma
                //4 premisas
                // primera. 2 o 3 posiciones coinciden pero perfil no coincide comparar y reemplazar
                // segunda. Una posicion por lo menos es igual en la misma hora
                // tercera. Una posicion que este muy cerca de la actual
                // cuarta. No hago nada
                Nodo temp1 = null, temp2 = null, temp3 = null;
                // primera premisa
                switch (bandera) { // meto valores a nodos temporales
                    case 0:
                        temp1 = matrix[H][1];
                        temp2 = matrix[H][2];
                        temp3 = matrix[H][3];
                        break;
                    case 1:
                        temp1 = matrix[H][0];
                        temp2 = matrix[H][2];
                        temp3 = matrix[H][3];
                        break;
                    case 2:
                        temp1 = matrix[H][0];
                        temp2 = matrix[H][1];
                        temp3 = matrix[H][3];
                        break;
                    case 3:
                        temp1 = matrix[H][0];
                        temp2 = matrix[H][1];
                        temp3 = matrix[H][2];
                        break;
                }
                posicion_g = P;
                info = analizar(temp1, temp2, temp3);
                //segunda premisa

                //tercera premisa
                //cuarta premisa
            }
        } else {
            //no se hace nada porque no ha configurado un perfil para ese dia
            //podriamos hacer una sugerencia
            new Conexion().insertarBitacora("19", INFO_MATRIZ, "[1]Se opera sobre Fila: "+H+" Pero se encuentra vacia dia: "+bandera);
        }
        if (info.equals("")) {
            info = "null";
        }
        //Esta para revisar el buen funcionamiento de H
        new Conexion().insertarBitacora("19", INFO_MATRIZ, "[2]Se opera sobre Fila: "+H+" y dia del mes: "+bandera);
        return info;
    }

    public int convertir_minuto(int minuto) { //devuelve un minuto como puntero de la matriz
        int minute = 0;
        if (minute <= 20) {
            minute = 1;
        } else if (minute <= 40) {
            minute = 2;
        } else if (minute > 40) {
            minute = 3;
        }
        return minute;
    }

    public void ingresar(String Hora, String P, String perfil) {
        Nodo nuevo = new Nodo(P, perfil);

        String[] hora_minuto = Hora.split(":");
        int Hours = Integer.parseInt(hora_minuto[0]);
        int Minute = Integer.parseInt(hora_minuto[1]);
        int H = convertir_minuto(Minute);
        H = Hours + H;

        for (int i = H; i < H + 1; i++) {
            matrix[i][(bandera <= 3)?bandera:3] = nuevo;
        }
    }

    public String analizar(Nodo temp1, Nodo temp2, Nodo temp3) {
        // analizamos si alguno de los 3 esta vacio se manda a analisis 2
        String info = "";
        translator tr = new translator();

        if (temp1 == null) {
            info = analizar2(temp2, temp3);
        } else if (temp2 == null) {
            info = analizar2(temp1, temp3);
        } else if (temp3 == null) {
            info = analizar2(temp2, temp1);
        } else //analisis si los 3 estan llenos
        if (compararPosiciones(temp1.posicion, temp2.posicion) && compararPosiciones(temp2.posicion, temp3.posicion)) { //La posibilidad que los 3 sean igual
            if (compararPosiciones(temp1.perfil, this.posicion_g)) {
                info = "$" + temp1.perfil;  // $ indica que se devuelve el perfil.
            } else {
                info = "#No deberias estar en " + tr.translate(temp1.posicion); // #indica un mensaje al usuario en este caso.
                // envia un mensaje indicando si no se deberia estan en algun lugar.
            }
        } else {
            String info1 = "", info2 = "", info3 = "";
            info1 = analizar2(temp1, temp2);
            info2 = analizar2(temp1, temp3);
            info3 = analizar2(temp2, temp3);
            if (info1.contains("$")) {
                return info1; // si contiene un perfil se retorna
            } else if (info2.contains("$")) {
                return info2; // si contiene un perfil se retorna
            } else if (info3.contains("$")) {
                return info3; // si contiene un perfil se retorna
            } else if ((info1.equals("")) && (info2.equals("")) && (info3.equals(""))) {
                return info; // si los 3 estan vacios se devuelbe una cadena vacia indica que no habra accion

            } // si alguno tiene un comentario se devuelbe el comentario mas cercano
            else if (info1.equals("#")) {
                return info1;
            } else if (info2.equals("#")) {
                return info2;
            } else if (info3.equals("#")) {
                return info3;
            }
        }
        return info;
    }

    public String analizar2(Nodo temp1, Nodo temp2) {
        String info = "";
        translator tr = new translator();

        if (temp1 == null) { //si el primer nodo esta vacio revisamos si el segundo tambien
            if (temp2 == null) {
                return info; //si todos los nodos de todos los dias estan vacios se devuelbe cadena de nulo
            } else //el temporal 2 si esta lleno
            if (compararPosiciones(temp2.posicion, this.posicion_g)) { //esta parte cumple la premisa 2
                info = "$" + temp2.perfil; // aqui se entrega el perfil
                return info;
            } else {
                info = "#No deberias estar en " + tr.translate(temp2.posicion);
                return info;
            }
        } else if (temp2 == null) { //si el segundo nodo esta vacio ya sabemos que el primero no esta vacio
            if (compararPosiciones(temp1.posicion, this.posicion_g)) {
                info = "$" + temp1.perfil; // aqui se entrega el perfil
                return info;
            } else {
                info = "#No deberias estar en " + tr.translate(temp1.posicion);
                return info;
            }
        } else //ninguno esta vacio 2 posibilidades
        if (compararPosiciones(temp1.posicion, this.posicion_g) && compararPosiciones(temp2.posicion, this.posicion_g)) {
            //ambos tienen la posicion correcta y perfil igual
            if (compararPerfiles(temp1.perfil, temp2.perfil)) {
                info = "$" + temp1.perfil; // se envia el perfil de cualquiera de los 2
                return info;
                //ambos tienen la posicion correcta y perfil distinto
            } else {
                info = "$" + temp1.perfil; //tomo el perfil del dia mas cercano
                return info;
            }
            //solo 1 tiene la posicion correcta
        } else if (compararPosiciones(temp1.posicion, this.posicion_g)) {
            info = "$" + temp1.perfil;
            return info;
        } else if (compararPosiciones(temp2.posicion, this.posicion_g)) {
            info = "$" + temp2.perfil;
            return info;
        } else {
            info = "#No deberias estar en " + tr.translate(temp1.posicion)+" ?";
        }
        return info;
    }

    public void escribir(String fichero) throws IOException, ClassNotFoundException { //metodo para serializar la matriz
        //
        FileOutputStream fos = new FileOutputStream(fichero);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(matrix);
        fos.close();
        //
    }

    public void leer(String fichero) throws IOException, ClassNotFoundException {
        Nodo[][] matrix2 = new Nodo[72][4];
        FileInputStream fis = new FileInputStream(fichero);
        ObjectInputStream iis = new ObjectInputStream(fis);
        matrix2 = (Nodo[][]) iis.readObject();
        matrix = matrix2;
        fis.close();
    }

    private boolean compararPerfiles(String perfil1, String perfil2) {

        JSONObject json1 = new JSONObject(perfil1);
        JSONObject json2 = new JSONObject(perfil2);

        return json1.getBoolean("vibrate") == json2.getBoolean("vibrate")
                && json1.getInt("volume") == json2.getInt("volume");
    }

    // Ej: posicion1 = "-14.53234432,90.848238995"
    private boolean compararPosiciones(String posicion1, String posicion2) {
        String[] arrp1 = posicion1.split(",");
        String[] arrp2 = posicion2.split(",");

        double lat1 = Double.parseDouble(arrp1[0]);
        double lon1 = Double.parseDouble(arrp1[1]);

        double lat2 = Double.parseDouble(arrp2[0]);
        double lon2 = Double.parseDouble(arrp2[1]);

        lat1 *= Math.PI / 180;
        lon1 *= Math.PI / 180;

        lat2 *= Math.PI / 180;
        lon2 *= Math.PI / 180;

        double deltaLon = lon2 - lon1;
        double deltaLat = lat2 - lat1;

        double R = 6371;
        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1) * Math.cos(lat2) * Math.sin(deltaLon / 2) * Math.sin(deltaLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c * 1000; // *1000 para que sean metros

        return d <= 20; //10m de separacion entre los puntos se considera como igual (se cambio a 20 el 27/11/2016)
    }

    public String llenar_matriz(String posicion, String perfil) {
        String info = "Matriz llena";
        Nodo nuevo = new Nodo(posicion, perfil);
        for (int j = 0; j < 4; j++) {
            for (int i = 0; i < 72; i++) {
                matrix[i][j] = nuevo;
            }
        }
        return info;
    }
}
