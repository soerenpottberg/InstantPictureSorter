package org.pottberg.ips.model.loader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileTime;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

import org.pottberg.ips.model.ImageData;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Metadata;
import com.drew.metadata.exif.ExifSubIFDDirectory;

public class CreationDateLoader extends Task<Void> {

    private ObservableList<ImageData> imageData;
    private boolean useFxApplicationThread;

    public CreationDateLoader(ObservableList<ImageData> imageData) {
	this(imageData, true);
    }

    public CreationDateLoader(ObservableList<ImageData> imageData,
	boolean useFxApplicationThread) {
	this.imageData = imageData;
	this.useFxApplicationThread = useFxApplicationThread;
    }

    @Override
    protected Void call() throws Exception {
	loadAttributes();
	return null;
    }

    private void loadAttributes() throws IOException, JpegProcessingException {
	int i = 0;
	updateProgress(i, imageData.size());
	for (ImageData data : imageData) {
	    i++;
	    if (data.hasCreationDate()) {
		updateProgress(i, imageData.size());
		continue;
	    }

	    Path filePath = data.getPath();

	    LocalDate creationDate = readCreadionDate(filePath);

	    setCreationDate(data, creationDate);

	    if (isCancelled()) {
		return;
	    }
	    updateProgress(i, imageData.size());
	}
	return;
    }

    private LocalDate readCreadionDate(Path filePath) throws IOException, JpegProcessingException {
	LocalDate creationDate = readExifMetadata(filePath);

	if (creationDate == null) {
	    creationDate = readFileAttributes(filePath);
	}
	return creationDate;
    }

    private void setCreationDate(ImageData data, LocalDate creationDate) {
	final LocalDate date = creationDate;
	if (useFxApplicationThread) {
	    Platform.runLater(() -> {
		data.setCreationDate(date);
	    });
	} else {
	    data.setCreationDate(date);
	}
    }

    private LocalDate readExifMetadata(Path filePath)
	throws JpegProcessingException, IOException {
	Metadata metadata = JpegMetadataReader.readMetadata(filePath.toFile());

	ExifSubIFDDirectory directory = metadata
	    .getDirectory(ExifSubIFDDirectory.class);
	if (directory == null) {
	    return null;
	}
	
	Date creationTime = directory.getDate(ExifSubIFDDirectory.TAG_DATETIME_ORIGINAL);
	if (creationTime == null) {
	    return null;
	}
	
	LocalDate creationDate = toLocaleDate(creationTime);
	return creationDate;
    }

    private LocalDate readFileAttributes(Path filePath) throws IOException {
	BasicFileAttributes attribute = Files.readAttributes(filePath,
	    BasicFileAttributes.class);

	FileTime lastModifiedTime = attribute.lastModifiedTime();
	FileTime creationTime = attribute.creationTime();

	LocalDate lastModifiedDate = toLocaleDate(lastModifiedTime);
	LocalDate creationDate = toLocaleDate(creationTime);

	return getEarliestDate(lastModifiedDate, creationDate);
    }

    private LocalDate getEarliestDate(LocalDate firstDate, LocalDate secondDate) {
	return firstDate.isBefore(secondDate) ? firstDate : secondDate;
    }

    private LocalDate toLocaleDate(FileTime fileTime) {
	return LocalDate.ofEpochDay(fileTime
	    .to(TimeUnit.DAYS));
    }

    private LocalDate toLocaleDate(Date date) {
	return date.toInstant()
	    .atZone(ZoneId.systemDefault())
	    .toLocalDate();
    }

    public static void loadFileAttributes(ObservableList<ImageData> imageData)
	throws IOException, JpegProcessingException {
	new CreationDateLoader(imageData, false).loadAttributes();
    }

}
