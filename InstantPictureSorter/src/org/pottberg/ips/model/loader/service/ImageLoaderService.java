package org.pottberg.ips.model.loader.service;

import org.pottberg.ips.model.loader.ImageLoader;

import javafx.concurrent.Task;

public class ImageLoaderService extends ImageDataLoaderService {

    @Override
    protected Task<Void> createTask() {
	return new ImageLoader(imageDataProperty.get());
    }

}
