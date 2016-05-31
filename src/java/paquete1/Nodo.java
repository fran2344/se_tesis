package paquete1;

import java.io.*;

public class Nodo implements java.io.Serializable{
    String posicion; //hacemos que la posicion del usuario sea no sensible
    String perfil; //perfil de telefono
    
    public Nodo(String Posicion,String Perfil){
        posicion=Posicion;
        perfil=Perfil;
    }
}