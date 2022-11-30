package net.cassite.hottapcassistant.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import net.cassite.hottapcassistant.component.HPadding;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.hottapcassistant.util.FontManager;
import net.cassite.hottapcassistant.util.GlobalValues;
import net.cassite.hottapcassistant.util.StackTraceAlert;

import java.util.Optional;

public abstract class WithConfirmPane extends BorderPane implements EnterCheck, ExitCheck {
    protected final Pane content = new Pane();
    private boolean isModified = false;

    private final Button okBtn;
    private final HBox bottomButtons;

    protected WithConfirmPane() {
        setCenter(content);

        bottomButtons = new HBox();
        bottomButtons.setAlignment(Pos.BOTTOM_RIGHT);
        setBottom(bottomButtons);

        Button resetBtn = new Button(I18n.get().resetButton()) {{
            FontManager.setFont(this);
        }};
        resetBtn.setPrefWidth(120);
        resetBtn.setOnAction(e -> {
            if (isModified) {
                Optional<ButtonType> opt = new Alert(Alert.AlertType.CONFIRMATION, I18n.get().discardChangesConfirm()).showAndWait();
                if (opt.isEmpty()) {
                    return;
                }
                if (opt.get() != ButtonType.OK) {
                    return;
                }
            }
            try {
                reset();
                unsetModified();
            } catch (Exception ex) {
                new StackTraceAlert(ex).show();
            }
        });
        okBtn = new Button(I18n.get().applyButton()) {{
            FontManager.setFont(this);
        }};
        okBtn.setPrefWidth(120);
        okBtn.setOnAction(e -> {
            if (!isModified) {
                return;
            }
            try {
                confirm();
                unsetModified();
            } catch (Exception ex) {
                new StackTraceAlert(ex).show();
            }
        });
        okBtn.setDisable(true);
        bottomButtons.getChildren().addAll(resetBtn, new HPadding(4), okBtn);
        bottomButtons.setPadding(new Insets(0, 5, 2, 0));
    }

    protected void setModified() {
        this.isModified = true;
        okBtn.setDisable(false);
    }

    private void unsetModified() {
        this.isModified = false;
        okBtn.setDisable(true);
    }

    protected void insertElementToBottom(Node node) {
        bottomButtons.getChildren().add(0, new HPadding(4));
        bottomButtons.getChildren().add(0, node);
    }

    @Override
    public boolean enterCheck() {
        if (!GlobalValues.checkGamePath()) return false;
        try {
            reset();
            unsetModified();
        } catch (Exception e) {
            new StackTraceAlert(e).show();
            return false;
        }
        return true;
    }

    @Override
    public boolean exitCheck() {
        if (!isModified) {
            return true;
        }
        Optional<ButtonType> opt = new Alert(Alert.AlertType.CONFIRMATION, I18n.get().exitCheckMessage()).showAndWait();
        return opt.isPresent() && opt.get() == ButtonType.OK;
    }

    abstract protected void confirm() throws Exception;

    abstract protected void reset() throws Exception;
}
