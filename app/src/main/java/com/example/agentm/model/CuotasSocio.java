package com.example.agentm.model;

/**
 * Created by carlo on 11/05/2017.
 */

public class CuotasSocio {

    private String id;
    private String year;
    private String id_socio;
    private Boolean pagado;

    public CuotasSocio() {
    }

    public CuotasSocio(String id, String year, String id_socio, Boolean pagado) {
        this.id = id;
        this.year = year;
        this.id_socio = id_socio;
        this.pagado = pagado;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getId_socio() {
        return id_socio;
    }

    public void setId_socio(String id_socio) {
        this.id_socio = id_socio;
    }

    public Boolean getPagado() {
        return pagado;
    }

    public void setPagado(Boolean pagado) {
        this.pagado = pagado;
    }
}
