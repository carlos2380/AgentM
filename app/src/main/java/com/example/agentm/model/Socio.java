package com.example.agentm.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by carlo on 26/04/2017.
 */

public class Socio {
    private String id;
    private String nombre;
    private String apellidos;
    private String dni;
    private int telefono;
    private String email;
    private boolean esUPC;

    public Socio() {
    }

    public Socio(boolean esUPC, String id, String nombre, String apellidos, String dni, int telefono, String email) {
        this.esUPC = esUPC;
        this.id = id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
        this.telefono = telefono;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public int getTelefono() {
        return telefono;
    }

    public void setTelefono(int telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public boolean isEsUPC() {
        return esUPC;
    }

    public void setEsUPC(boolean esUPC) {
        this.esUPC = esUPC;
    }

    public static boolean validateNombreOrApellido(String str) {
        if(!str.equals("")){
            return true;
        }else return false;
    }
    public static boolean validateEmail(String str) {
        if(!str.equals("")) {
            String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                    + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

            Pattern pattern = Pattern.compile(EMAIL_PATTERN);
            Matcher matcher = pattern.matcher(str);
            return matcher.matches();
        }else return false;
    }

    public static boolean validateDni(String str) {
        if(!str.equals("")) {
            Pattern pattern = Pattern.compile("(\\d{1,8})([TRWAGMYFPDXBNJZSQVHLCKEtrwagmyfpdxbnjzsqvhlcke])");
            Matcher matcher = pattern.matcher(str);
            if (matcher.matches()) {
                String letra = matcher.group(2);
                String letras = "TRWAGMYFPDXBNJZSQVHLCKE";
                int index = Integer.parseInt(matcher.group(1));
                index = index % 23;
                String reference = letras.substring(index, index + 1);
                if (reference.equalsIgnoreCase(letra)) {
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
            }else return false;
    }

    public static boolean validateTel(String str) {
        String regexStr = "^[0-9]{9}$";
        if(!str.equals("")) {
            if (str.matches(regexStr)) return true;
            else return false;
        }else return false;
    }
}
