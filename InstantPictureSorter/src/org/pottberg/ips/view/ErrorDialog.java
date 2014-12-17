package org.pottberg.ips.view;

import org.pottberg.ips.controller.Controller;
import org.pottberg.ips.controller.ErrorDialogController;

public class ErrorDialog extends View {
    
    private ErrorDialogController controller;

    @Override
    protected Controller getController() {
	if(controller == null) {
	    controller = new ErrorDialogController();
	}
	return controller;
    }

    public void setErrorMessage(String message) {
	controller.setErrorMessage(message);
    }   

}
