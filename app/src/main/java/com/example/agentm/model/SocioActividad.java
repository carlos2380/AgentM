package com.example.agentm.model;

/**
 * Created by carlo on 26/04/2017.
 */

public class SocioActividad {

    private String id;
    private boolean pagado;
    private String socio_id;
    private String actividad_id;

    public SocioActividad() {
    }

    public SocioActividad(String id, boolean pagado, String socio_id, String actividad_id) {
        this.id = id;
        this.pagado = pagado;
        this.socio_id = socio_id;
        this.actividad_id = actividad_id;
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
}
