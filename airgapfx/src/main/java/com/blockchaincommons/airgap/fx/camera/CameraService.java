package com.blockchaincommons.airgap.fx.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamResolution;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;

/**
 *
 * Based on https://github.com/sarxos/webcam-capture/tree/master/webcam-capture-examples/webcam-capture-javafx-service
 */
public class CameraService extends Service<Image> {
    private static final Logger log = LoggerFactory.getLogger(CameraService.class);

    private final Webcam camera;
    private final WebcamResolution resolution ;

    public CameraService(Webcam camera, WebcamResolution resolution) {
        this.camera = camera;
        this.resolution = resolution;
        camera.setCustomViewSizes(resolution.getSize());
        camera.setViewSize(resolution.getSize());
    }

    public CameraService(Webcam camera) {
        this(camera, WebcamResolution.HVGA);
    }

    @Override
    public Task<Image> createTask() {
        return new Task<Image>() {
            @Override
            protected Image call() throws Exception {

                try {
                    camera.open();
                    while (!isCancelled()) {
                        if (camera.isImageNew()) {
                            BufferedImage bimg = camera.getImage();
                            updateValue(SwingFXUtils.toFXImage(bimg, null));
                        }
                    }
                    log.info("Cancelled, closing camera");
                    camera.close();
                    log.info("Camera closed");
                    return getValue();
                } finally {
                    camera.close();
                }
            }

        };
    }


    public int getCameraWidth() {
        return resolution.getSize().width ;
    }

    public int getCameraHeight() {
        return resolution.getSize().height ;
    }



}
