package org.pottberg.ips.view;

import org.pottberg.ips.controller.Controller;
import org.pottberg.ips.controller.MainController;

public class Main extends View {
    
    private MainController controller;

    @Override
    protected Controller getController() {
	if(controller == null) {
	    controller = new MainController(this);
	}
	return controller;
    }   

}
