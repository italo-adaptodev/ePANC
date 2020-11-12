package com.adapto.panc.Models.Database;

public class Usuario {
    private String usuarioKeyAuth;
    private boolean isProdutor, isConsumidor;

    public Usuario(String usuarioKeyAuth, boolean isProdutor, boolean isConsumidor) {
        this.usuarioKeyAuth = usuarioKeyAuth;
        this.isProdutor = isProdutor;
        this.isConsumidor = isConsumidor;
    }

    public String getUsuarioKeyAuth() {
        return usuarioKeyAuth;
    }

    public boolean isProdutor() {
        return isProdutor;
    }

    public void setProdutor(boolean produtor) {
        isProdutor = produtor;
    }

    public boolean isConsumidor() {
        return isConsumidor;
    }

    public void setConsumidor(boolean consumidor) {
        isConsumidor = consumidor;
    }
}
