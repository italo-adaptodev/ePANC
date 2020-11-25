package com.adapto.panc.Models.Database;

import java.util.List;

public class MembroEquipe {

    private String usuarioID, indicadoPor;
    private List<String> cargosAdministrativos;

    public MembroEquipe(String usuarioID, String indicadoPor, List<String> cargos) {
        this.usuarioID = usuarioID;
        this.cargosAdministrativos = cargos;
        this.indicadoPor = indicadoPor;
    }

    public MembroEquipe() {
    }

    public String getUsuarioID() {
        return usuarioID;
    }

    public void setUsuarioID(String usuarioID) {
        this.usuarioID = usuarioID;
    }

    public List<String> getCargosAdministrativos() {
        return cargosAdministrativos;
    }

    public void setCargosAdministrativos(List<String> cargos) {
        this.cargosAdministrativos = cargos;
    }

    public String getIndicadoPor() {
        return indicadoPor;
    }

    public void setIndicadoPor(String indicadoPor) {
        this.indicadoPor = indicadoPor;
    }
}
