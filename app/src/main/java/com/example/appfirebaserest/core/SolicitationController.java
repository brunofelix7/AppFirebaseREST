package com.example.appfirebaserest.core;

public class SolicitationController {

    private static SolicitationController instance = null;
    private String id;
    private String urgencia;
    private String consciencia;
    private String respiracao;

    public static SolicitationController getInstance(){
        if(instance == null){
            instance = new SolicitationController();
        }
        return instance;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setUrgencia(String urgencia) {
        this.urgencia = urgencia;
    }

    public String getConsciencia() {
        return consciencia;
    }

    public void setConsciencia(String consciencia) {
        this.consciencia = consciencia;
    }

    public String getRespiracao() {
        return respiracao;
    }

    public void setRespiracao(String respiracao) {
        this.respiracao = respiracao;
    }
}
