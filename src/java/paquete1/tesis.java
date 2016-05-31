package paquete1;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.jws.WebService;
import javax.jws.WebMethod;
import javax.jws.WebParam;


@WebService(serviceName = "tesis")
public class tesis {


    @WebMethod(operationName = "consultar")
    public String consultar(String id_phone,String hora,String posicion,int dia_del_mes,int dia) throws IOException, ClassNotFoundException {
        String sFichero=id_phone+"_"+dia+".dat"; //se busca el fichero del usuario
        String prueba="no se creo fichero";
        if(sFichero.equals(""))
            return "nulo";
        File fichero = new File(sFichero);
        Matriz mat=new Matriz();
        if (fichero.exists()){ // si el fichero existe se lee
            mat.leer(sFichero);
            prueba="fichero ya existe";
        }else{ // de lo contrario se crea y se lee
            mat.escribir(sFichero);
            prueba="fichero no existía";
        }
        //ya tengo un documento estable por cada telefono
        mat.bandera=dia_del_mes;
        prueba=mat.consulta(hora, posicion, dia_del_mes);
        mat.escribir(sFichero);
        return prueba;
    }
    
    @WebMethod(operationName = "ingresar_perfil")
    public String ingresar_perfil(String id_phone,String hora,String posicion,int dia_del_mes,String perfil,int dia) throws IOException, ClassNotFoundException {
        String sFichero=id_phone+"_"+dia+".dat"; //se busca el fichero del usuario
        String prueba="no se creo fichero";
        if(sFichero.equals(""))
            return "nulo";
        File fichero = new File(sFichero);
        Matriz mat=new Matriz();
        if (fichero.exists()){ // si el fichero existe se lee
            mat.leer(sFichero);
            prueba="fichero ya existe";
        }else{ // de lo contrario se crea y se lee
            mat.escribir(sFichero);
            prueba="fichero no existía";
        }
        //ya tengo un documento estable por cada telefono
        
        
        ///aqui registramos el perfil en la bd
        prueba=set_perfil(id_phone, perfil);
        ////
        
        mat.bandera=dia_del_mes;
        mat.ingresar(hora, posicion, perfil);
        mat.escribir(sFichero);
        return prueba;
    }
    
    @WebMethod(operationName = "registro")
    public String registro(String correo, String pass, String nombre) {
        /////////////// 
        Connection c = null;
        Statement s = null;
        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/profile","postgres","admin");
//            c = DriverManager.getConnection("jdbc:postgresql://ec2-54-191-108-207.us-west-2.compute.amazonaws.com:5432/profile","postgres","admin");
            c.setAutoCommit(false);
            s = c.createStatement();
            String consulta="insert into usuario(mail,pass,nombre)values ('"+correo+"','"+pass+"','"+nombre+"');";
            s.executeUpdate(consulta);
            s.close();
            c.commit();
            c.close();
            String resultado = get_id(correo, pass);
            return resultado;
            
        }catch(Exception e){
            System.out.println(e);
            return "#Error";
        }        
        //////////////        
    }
    
    
    
    @WebMethod(operationName = "get_id")
    public String get_id(String correo, String pass) {
        /////////////// 
        Connection c = null;
        Statement s = null;
        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/profile","postgres","admin");
//            c = DriverManager.getConnection("jdbc:postgresql://ec2-54-191-108-207.us-west-2.compute.amazonaws.com:5432/profile","postgres","admin");
            c.setAutoCommit(false);
            s = c.createStatement();
            ResultSet rs = s.executeQuery("select id_user from usuario where mail='"+correo+"' and pass='"+pass+"';");
            String resultado = "";
            while (rs.next()){
                resultado += rs.getString("id_user");
            }
            if(resultado.equals(""))
                resultado="#Error";
            s.close();
            c.close();
            return resultado;
            
        }catch(Exception e){
            return "error";
        }        
        //////////////        
    }
    
    
    @WebMethod(operationName = "set_perfil")
    public String set_perfil(String id_usuario,String perfil) {
        /////////////// 
        Connection c = null;
        Statement s = null;
        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/profile","postgres","admin");
//            c = DriverManager.getConnection("jdbc:postgresql://ec2-54-191-108-207.us-west-2.compute.amazonaws.com:5432/profile","postgres","admin");
            c.setAutoCommit(false);
            s = c.createStatement();
            s.executeUpdate("insert into perfil(id_usuario,perfil)"
                    + "values("+id_usuario+",'"+perfil+"');");
            String resultado = "true";
            s.close();
            c.commit();
            c.close();
            return resultado;
            
        }catch(Exception e){
            return "false";
        }        
        //////////////        
    }
    
    
    @WebMethod(operationName = "get_perfiles")
    public String get_perfiles(String id_usuario) {
        /////////////// 
        Connection c = null;
        Statement s = null;
        try{
            Class.forName("org.postgresql.Driver");
            c = DriverManager.getConnection("jdbc:postgresql://localhost:5432/profile","postgres","admin");
//            c = DriverManager.getConnection("jdbc:postgresql://ec2-54-191-108-207.us-west-2.compute.amazonaws.com:5432/profile","postgres","admin");
            c.setAutoCommit(false);
            s = c.createStatement();
            ResultSet rs = s.executeQuery("select distinct perfil from perfil where id_usuario="+id_usuario+";");
            String resultado = "";
            while (rs.next()){
                resultado += rs.getString("perfil")+";\n";
            }
            if(resultado.equals(""))
                resultado="###";
            s.close();
            c.close();
            return resultado;
            
        }catch(Exception e){
            return "#Error";
        }        
        //////////////        
    }
    
}
