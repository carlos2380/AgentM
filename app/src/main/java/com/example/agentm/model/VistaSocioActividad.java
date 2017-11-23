package com.example.agentm.model;

/**
 * Created by carlo on 13/05/2017.
 */

public class VistaSocioActividad {
    private String id;
    private boolean pagado;
    private String socio_id;
    private String actividad_id;
    private String nombre;
    private String apellidos;
    private String dni;

    public VistaSocioActividad() {
    }

    public VistaSocioActividad(String id, boolean pagado, String socio_id, String actividad_id, String nombre, String apellidos, String dni) {
        this.id = id;
        this.pagado = pagado;
        this.socio_id = socio_id;
        this.actividad_id = actividad_id;
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.dni = dni;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isPagado() {
        return pagado;
    }

    public void setPagado(boolean pagado) {
        this.pagado = pagado;
    }

    public String getSocio_id() {
        return socio_id;
    }

    public void setSocio_id(String socio_id) {
        this.socio_id = socio_id;
    }

    public String getActividad_id() {
        return actividad_id;
    }

    public void setActividad_id(String actividad_id) {
        this.actividad_id = actividad_id;
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

    public void setSocioActividad(SocioActividad sa) {
        id = sa.getId();
        pagado = sa.isPagado();
        socio_id = sa.getSocio_id();
        actividad_id = sa.getActividad_id();
    }

    public void setSocio(Socio sa) {

        nombre = sa.getNombre();
        apellidos = sa.getApellidos();
        dni = sa.getDni();
    }
}
