package org.pottberg.ips.model.command;

import java.util.List;

import org.pottberg.ips.model.Category;
import org.pottberg.ips.model.ImageData;
import org.pottberg.ips.model.ImageGroup;

public class MoveImagesCommand extends ComplexCommand {

    private List<ImageData> imageDataList;
    private Category target;

    public MoveImagesCommand(Command previousCommand,
	List<ImageData> imageDataList, ImageGroup source, Category target) {
	this.imageDataList = imageDataList;
	this.target = target;
	for (ImageData imageData : imageDataList) {
	    addCommand(new MoveImageCommand(imageData, source, target));
	}
    }

    @Override
    public String getName() {
	return String.format("Move \"%i\" images to \"%s\"",
	    imageDataList.size(), target.getDirectory());
    }

}
