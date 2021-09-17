/*
 * Copyright by the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bitcoinj.walletfx;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import app.supernaut.fx.FxmlLoaderFactory;

import javax.annotation.Nullable;
import java.io.IOException;
import java.net.URL;

import static org.bitcoinj.walletfx.utils.GuiUtils.blurIn;
import static org.bitcoinj.walletfx.utils.GuiUtils.blurOut;
import static org.bitcoinj.walletfx.utils.GuiUtils.checkGuiThread;
import static org.bitcoinj.walletfx.utils.GuiUtils.explodeOut;
import static org.bitcoinj.walletfx.utils.GuiUtils.fadeIn;
import static org.bitcoinj.walletfx.utils.GuiUtils.zoomIn;
import static org.bitcoinj.walletfx.utils.GuiUtils.fadeOutAndRemove;

/**
 * A window that can have another Pane displayed on top of it as a modal
 */
public abstract class OverlayableWindowController {

    protected StackPane uiStack;
    protected Pane mainUI;
    @Nullable
    OverlayableWindowController.OverlayUI currentOverlay;

    private Node stopClickPane = new Pane();


    /**
     * TODO: Explain how and why to implement this abstract method!
     * @return an FxmlLoaderFactory
     */
    abstract FxmlLoaderFactory getFxmlLoaderFactory();

    /**
     * Creates an OverlayUI from the given node and controller, blurs out the main UI and puts this one on top.
     * @param node JavaFX Node with our UI component
     * @param controller Controller for node
     * @param <T> OverlayWindowController subclass being used
     * @return the OverlayUI
     */
    public <T extends OverlayWindowController> OverlayableWindowController.OverlayUI<T> overlayUI(Node node, T controller) {
        checkGuiThread();
        OverlayableWindowController.OverlayUI<T> pair = new OverlayableWindowController.OverlayUI<T>(this, node, controller);
        if (controller != null) {
            controller.setOverlayUI(pair);
        }
        pair.show();
        return pair;
    }

    /**
     * Loads the FXML resource with the given name, blurs out the main UI and puts this one on top.
     * @param name Name of .fxml resource to load
     * @param <T> OverlayWindowController subclass being used
     * @return the OverlayUI
     */
    public <T extends OverlayWindowController> OverlayableWindowController.OverlayUI<T> overlayUI(String name) {
        checkGuiThread();
        // Load the UI from disk.
        // Note that the location URL returned from getResource() will be in the package of the concrete subclass
        URL location = OverlayableWindowController.class.getResource(name);
        return this.overlayUI(location);
    }

    /**
     * Loads the FXML file with the given location, blurs out the main UI and puts this one on top.
     *
     * @param location A location URL for a .fxml resource
     * @param <T> OverlayWindowController subclass being used
     * @return the OverlayUI
     */
    public <T extends OverlayWindowController> OverlayableWindowController.OverlayUI<T> overlayUI(URL location) {
        checkGuiThread();
        try {
            FXMLLoader loader = getFxmlLoaderFactory().get(location);
            Pane ui = loader.load();
            T controller = loader.getController();
            return this.overlayUI(ui, controller);
        } catch (IOException e) {
            throw new RuntimeException(e);  // Should never happen.
        }
    }

    public class OverlayUI<T> {
        private OverlayableWindowController parentWindow;
        public Node ui;
        public T controller;

        public OverlayUI(OverlayableWindowController parentWindow, Node ui, T controller) {
            this.ui = ui;
            this.controller = controller;
        }

        public void show() {
            checkGuiThread();
            if (currentOverlay == null) {
                uiStack.getChildren().add(stopClickPane);
                uiStack.getChildren().add(ui);
                blurOut(mainUI);
                //darken(mainUI);
                fadeIn(ui);
                zoomIn(ui);
            } else {
                // Do a quick transition between the current overlay and the next.
                // Bug here: we don't pay attention to changes in outsideClickDismisses.
                explodeOut(currentOverlay.ui);
                fadeOutAndRemove(uiStack, currentOverlay.ui);
                uiStack.getChildren().add(ui);
                ui.setOpacity(0.0);
                fadeIn(ui, 100);
                zoomIn(ui, 100);
            }
            currentOverlay = this;
        }

        public void outsideClickDismisses() {
            stopClickPane.setOnMouseClicked((ev) -> done());
        }

        public void done() {
            checkGuiThread();
            if (ui == null) return;  // In the middle of being dismissed and got an extra click.
            explodeOut(ui);
            fadeOutAndRemove(uiStack, ui, stopClickPane);
            blurIn(mainUI);
            //undark(mainUI);
            this.ui = null;
            this.controller = null;
            currentOverlay = null;
        }
    }


}
