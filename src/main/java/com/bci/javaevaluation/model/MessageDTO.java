package com.bci.javaevaluation.model;

import java.util.List;

public class MessageDTO {
    public List<ErrorDTO> getError() {
        return error;
    }

    public void setError(List<ErrorDTO> error) {
        this.error = error;
    }

    private List<ErrorDTO> error;

    public MessageDTO(String mensaje, int codigo) {
        this.error = List.of(new ErrorDTO(mensaje, codigo));
    }
}
