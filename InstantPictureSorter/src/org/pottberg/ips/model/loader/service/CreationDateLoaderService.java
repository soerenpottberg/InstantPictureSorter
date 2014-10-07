package org.pottberg.ips.model.loader.service;

import org.pottberg.ips.model.loader.CreationDateLoader;

import javafx.concurrent.Task;

public class CreationDateLoaderService extends ImageDataLoaderService {

    @Override
    protected Task<Void> createTask() {
	return new CreationDateLoader(imageDataProperty.get());
    }

}
