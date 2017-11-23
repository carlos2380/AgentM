package com.example.agentm.Patterns;

import com.example.agentm.model.Socio;
import com.example.agentm.model.SocioActividad;
import com.example.agentm.model.VistaSocioActividad;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlo on 13/05/2017.
 */

public class CustomActividadIntent {

    private List<Socio> noParticipan;
    private List<Socio> participan;
    private List<VistaSocioActividad> socActParticipan;
    private float precioAct;
    private String idAct;

    private static final CustomActividadIntent ourInstance = new CustomActividadIntent();

    public static CustomActividadIntent getInstance() {
        return ourInstance;
    }

    private CustomActividadIntent() {
        noParticipan = new ArrayList<>();
        participan  = new ArrayList<>();
        socActParticipan = new ArrayList<>();
    }

    public List<Socio> getNoParticipan() {
        return noParticipan;
    }

    public void setNoParticipan(List<Socio> noParticipan) {
        this.noParticipan = noParticipan;
    }

    public List<Socio> getParticipan() {
        return participan;
    }

    public void setParticipan(List<Socio> participan) {
        this.participan = participan;
    }

    public List<VistaSocioActividad> getVSocActParticipan() {
        return socActParticipan;
    }

    public void setSocVActParticipan(List<VistaSocioActividad> socActParticipan) {
        this.socActParticipan = socActParticipan;
    }

    public String getIdAct() {
        return idAct;
    }

    public void setIdAct(String idAct) {
        this.idAct = idAct;
    }

    public float getPrecioAct() {
        return precioAct;
    }

    public void setPrecioAct(float precioAct) {
        this.precioAct = precioAct;
    }
}
