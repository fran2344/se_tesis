package paquete1;

import DB.Conexion;
import static DB.accion_log.*;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.jws.WebService;
import javax.jws.WebMethod;

@WebService(serviceName = "tesis")
public class tesis {

    @WebMethod(operationName = "consultar")
    public String consultar(String id_phone, String hora, String posicion, int dia_del_mes, int dia) throws IOException, ClassNotFoundException {
        //if(dia_del_mes==5)dia_del_mes=4;
        String sFichero = id_phone + "_" + dia + ".dat"; //se busca el fichero del usuario
        String prueba = "no se creo fichero";
        if (sFichero.equals("")) {
            return "nulo";
        }
        File fichero = new File(sFichero);
        Matriz mat = new Matriz();
        if (fichero.exists()) { // si el fichero existe se lee
            mat.leer(sFichero);
            prueba = "fichero ya existe";
            new Conexion().insertarBitacora(id_phone, OTROS, "Fichero ya existe ["+sFichero+"] [consultar]");
        } else { // de lo contrario se crea y se lee
            mat.escribir(sFichero);
            prueba = "fichero no existía";
            new Conexion().insertarBitacora(id_phone, OTROS, "Fichero no existia ["+sFichero+"] [consultar]");
        }
        //ya tengo un documento estable por cada telefono
        mat.setBandera(dia_del_mes);
        prueba = mat.consulta(hora, posicion);
        mat.escribir(sFichero); // grabo cambios en el fichero creado
        
        new Conexion().insertarBitacora(id_phone, CONSULTAR_DATOS, "Consulta de datos desde la posicion ["+posicion+"], "
                + "El servidor retorno: ["+prueba+"]");
        
        return prueba;
    }

    @WebMethod(operationName = "ingresar_perfil")
    public String ingresar_perfil(String id_phone, String hora, String posicion, int dia_del_mes, String perfil, int dia) throws IOException, ClassNotFoundException {
        if(dia_del_mes==5)dia_del_mes=4;
        String sFichero = id_phone + "_" + dia + ".dat"; //se busca el fichero del usuario
        String prueba = "no se creo fichero";
        if (sFichero.equals("")) {
            return "nulo";
        }
        File fichero = new File(sFichero);
        Matriz mat = new Matriz();
        Conexion conn = new Conexion();
        if (fichero.exists()) { // si el fichero existe se lee
            mat.leer(sFichero);
            prueba = "fichero ya existe";
            conn.insertarBitacora(id_phone, OTROS, "Fichero ya existe ["+sFichero+"] [ingresar_perfil]");
            
        } else { // de lo contrario se crea y se lee
            mat.escribir(sFichero);
            prueba = "fichero no existía";
            conn.insertarBitacora(id_phone, OTROS, "Fichero no existia ["+sFichero+"] [ingresar_perfil]");
        }
        //ya tengo un documento estable por cada telefono

        ///aqui registramos el perfil en la bd
        //prueba = set_perfil(id_phone, perfil); Mejor ya no lo hacemos :D
        ////

        mat.setBandera(dia_del_mes);
        mat.ingresar(hora, posicion, perfil);
        mat.escribir(sFichero);
        
        new Conexion().insertarBitacora(id_phone, APLICAR_PERFIL, "Se aplico el perfil ["+perfil+"] "
                + "en la posicion: ["+posicion+"]");
        
        return prueba;
    }

    @WebMethod(operationName = "registro")
    public String registro(String correo, String pass, String nombre) {
        /////////////// 
        Conexion conn = new Conexion();

        try {
            conn.ejecutarUpdate("insert into usuario(mail,pass,nombre)values ('" + correo + "',md5('" + pass + "'),'" + nombre + "');");
            
            String user = get_id(correo,pass);
            conn.insertarBitacora(user, REGISTRO, "Registro de nuevo usuario.");
            
            return user;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "#Error";
        }
        //////////////        
    }

    @WebMethod(operationName = "get_id")
    public String get_id(String correo, String pass) {
        /////////////// 
        Conexion conn = new Conexion();
        ResultSet rs;

        try {
            rs = conn.ejecutarQuery("select id_user from usuario where mail='" + correo + "' and pass= md5('" + pass + "');");
            String resultado = "";

            while (rs.next()) {
                resultado += rs.getString("id_user");
            }
            if (resultado.equals("")) {
                resultado = "#Error";
            }
            conn.close();
            
            if(!resultado.equals("#Error")){
                conn.insertarBitacora(resultado, LOGIN, "Usuario autenticado en la app.");
            }
            
            return resultado;
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "#Error";
        }
        //////////////        
    }

    @WebMethod(operationName = "set_perfil")
    public String set_perfil(String id_usuario, String perfil) {
        /////////////// 
        Conexion conn = new Conexion();

        try {
            conn.ejecutarUpdate("insert into perfil(id_usuario,perfil) values(" + id_usuario + ",'" + perfil + "');");
            conn.insertarBitacora(id_usuario, CREAR_PERFIL,"Se creo el perfil ["+perfil+"]");
            return "true";
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "false";
        }
        //////////////        
    }

    @WebMethod(operationName = "get_perfiles")
    public String get_perfiles(String id_usuario) {
        /////////////// 
        Conexion conn = new Conexion();

        try {
            ResultSet rs = conn.ejecutarQuery("select distinct perfil from perfil where id_usuario=" + id_usuario + ";");
            String resultado = "";
            while (rs.next()) {
                resultado += rs.getString("perfil") + ";\n";
            }
            if (resultado.equals("")) {
                resultado = "#Error";
            }
            conn.close();
            
            if(!resultado.equals("#Error")){
                conn.insertarBitacora(id_usuario, CONSULTAR_DATOS, "Obtener lista de perfiles.");
            }
            
            return resultado;

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
            return "#Error";
        }
        //////////////        
    }
    
    
    
    @WebMethod(operationName = "llenar")
    public String llenar(String id_phone,String perfil, int dia, String posicion) throws IOException, ClassNotFoundException {
        String sFichero = id_phone + "_" + dia + ".dat"; //se busca el fichero del usuario
        String prueba = "no se creo fichero";
        if (sFichero.equals("")) {
            return "nulo";
        }
        File fichero = new File(sFichero);
        Matriz mat = new Matriz();
        Conexion conn = new Conexion();
        if (fichero.exists()) { // si el fichero existe se lee
            mat.leer(sFichero);
            prueba = "fichero ya existe";
            conn.insertarBitacora(id_phone, OTROS, "Ya existia el fichero ["+sFichero+"] [llenar]");
        } else { // de lo contrario se crea y se lee
            mat.escribir(sFichero);
            prueba = "fichero no existía";
            conn.insertarBitacora(id_phone, OTROS, "No existia el fichero ["+sFichero+"] [llenar]");
        }
        //ya tengo un documento estable por cada telefono
        prueba = mat.llenar_matriz(posicion, perfil);
        mat.escribir(sFichero);
        
        new Conexion().insertarBitacora(id_phone, CONSULTAR_DATOS, "Se lleno la matriz en ["+posicion+"], "
                + "El servidor retorno: ["+prueba+"]");
        
        return prueba;
    }
    


}
