package com.adapto.panc.Models.Database;

import java.util.List;

public class ConviteEquipeAdministrativa {

    String identificador, justificativa, indicadoPor, id;
    List<String> cargos;

    public ConviteEquipeAdministrativa(String identificador, String justificativa, List<String> cargos, String indicadoPor) {
        this.identificador = identificador;
        this.justificativa = justificativa;
        this.cargos = cargos;
        this.indicadoPor = indicadoPor;
    }
    public ConviteEquipeAdministrativa() {
    }

    public String getIdentificador() {
        return identificador;
    }

    public void setIdentificador(String identificador) {
        this.identificador = identificador;
    }

    public String getJustificativa() {
        return justificativa;
    }

    public void setJustificativa(String justificativa) {
        this.justificativa = justificativa;
    }

    public List<String> getCargos() {
        return cargos;
    }

    public void setCargos(List<String> cargos) {
        this.cargos = cargos;
    }

    public String getIndicadoPor() {
        return indicadoPor;
    }

    public void setIndicadoPor(String indicadoPor) {
        this.indicadoPor = indicadoPor;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
