package com.bci.javaevaluation.model;

import java.util.Date;

public class ErrorDTO {
    private Date timestamp;
    private int codigo;
    private String detail;

    public ErrorDTO(String mensaje, int codigo) {
        this.timestamp = new Date();
        this.codigo = codigo;
        this.detail = mensaje;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }
}
