package org.pottberg.ips.controller;

import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class ErrorDialogController implements Controller {
    
    @FXML
    protected Text errorMessage;
    
    public void setErrorMessage(String message) {
	errorMessage.setText(message);
    }

}
