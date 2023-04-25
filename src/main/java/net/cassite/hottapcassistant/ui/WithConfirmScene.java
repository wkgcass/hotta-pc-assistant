package net.cassite.hottapcassistant.ui;

import io.vproxy.vfx.control.dialog.VConfirmDialog;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.layout.VPadding;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.GlobalValues;

import java.util.Optional;

public abstract class WithConfirmScene extends AbstractMainScene implements EnterCheck, ExitCheck {
    protected final Pane content = new Pane();
    private boolean isModified = false;

    protected final FusionPane bottomPane;

    private final FusionButton okBtn;
    private final HBox bottomButtons;

    protected WithConfirmScene() {
        enableAutoContentWidthHeight();

        FXUtils.observeWidthHeight(getContentPane(), content, -20, -80);

        bottomButtons = new HBox();
        bottomButtons.setAlignment(Pos.BOTTOM_RIGHT);
        bottomButtons.setSpacing(10);
        bottomButtons.setAlignment(Pos.CENTER_RIGHT);

        bottomPane = new FusionPane(bottomButtons);
        FXUtils.observeWidth(getContentPane(), bottomPane.getNode(), -20);
        bottomPane.getNode().setPrefHeight(60);

        FXUtils.observeWidth(bottomPane.getContentPane(), bottomButtons);

        getContentPane().getChildren().add(new VBox(
            new VPadding(5),
            content,
            new VPadding(10),
            bottomPane.getNode()
        ) {{
            setLayoutX(10);
        }});

        var resetBtn = new FusionButton(I18n.get().resetButton());
        FXUtils.observeHeight(bottomPane.getContentPane(), resetBtn);
        resetBtn.setPrefWidth(120);
        resetBtn.setOnAction(e -> {
            if (isModified) {
                var dialog = new VConfirmDialog();
                dialog.setText(I18n.get().discardChangesConfirm());
                Optional<VConfirmDialog.Result> opt = dialog.showAndWait();
                if (opt.isEmpty()) {
                    return;
                }
                if (opt.get() != VConfirmDialog.Result.YES) {
                    return;
                }
            }
            try {
                reset();
                unsetModified();
            } catch (Exception ex) {
                StackTraceAlert.show(ex);
            }
        });
        okBtn = new FusionButton(I18n.get().applyButton());
        FXUtils.observeHeight(bottomPane.getContentPane(), okBtn);
        okBtn.setPrefWidth(120);
        okBtn.setOnAction(e -> {
            if (!isModified) {
                return;
            }
            try {
                confirm();
                unsetModified();
            } catch (Exception ex) {
                StackTraceAlert.show(ex);
            }
        });
        okBtn.setDisable(true);
        bottomButtons.getChildren().addAll(resetBtn, okBtn);
        bottomButtons.setPadding(new Insets(0, 5, 2, 0));
    }

    protected void setModified() {
        this.isModified = true;
        okBtn.setDisable(false);
        okBtn.requestFocus();
    }

    private void unsetModified() {
        this.isModified = false;
        okBtn.setDisable(true);
    }

    protected void insertElementToBottom(Node node) {
        bottomButtons.getChildren().add(0, node);
    }

    @Override
    public boolean enterCheck(boolean skipGamePathCheck) {
        if (!GlobalValues.checkGamePath()) return false;
        try {
            reset();
            unsetModified();
        } catch (Exception e) {
            StackTraceAlert.show(e);
            return false;
        }
        return true;
    }

    @Override
    public boolean exitCheck() {
        if (!isModified) {
            return true;
        }
        var dialog = new VConfirmDialog();
        dialog.setText(I18n.get().exitCheckMessage());
        Optional<VConfirmDialog.Result> opt = dialog.showAndWait();
        return opt.isPresent() && opt.get() == VConfirmDialog.Result.YES;
    }

    abstract protected void confirm() throws Exception;

    abstract protected void reset() throws Exception;
}
