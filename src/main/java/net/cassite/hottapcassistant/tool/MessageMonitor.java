package net.cassite.hottapcassistant.tool;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.OS;
import io.vproxy.vfd.IP;
import io.vproxy.vfx.control.scroll.VScrollPane;
import io.vproxy.vfx.manager.audio.AudioManager;
import io.vproxy.vfx.manager.font.FontManager;
import io.vproxy.vfx.manager.image.ImageManager;
import io.vproxy.vfx.manager.task.TaskManager;
import io.vproxy.vfx.theme.Theme;
import io.vproxy.vfx.ui.alert.SimpleAlert;
import io.vproxy.vfx.ui.alert.StackTraceAlert;
import io.vproxy.vfx.ui.button.FusionButton;
import io.vproxy.vfx.ui.pane.FusionPane;
import io.vproxy.vfx.ui.scene.VScene;
import io.vproxy.vfx.ui.wrapper.FusionW;
import io.vproxy.vfx.ui.wrapper.ThemeLabel;
import io.vproxy.vfx.util.FXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import net.cassite.hottapcassistant.i18n.I18n;
import net.cassite.tofpcap.MessageEvent;
import net.cassite.tofpcap.MessageType;
import net.cassite.tofpcap.TofPcap;
import net.cassite.tofpcap.messages.ChatChannel;
import net.cassite.tofpcap.messages.ChatMessage;
import org.controlsfx.control.Notifications;
import org.pcap4j.core.PcapNetworkInterface;
import org.pcap4j.core.Pcaps;
import vjson.JSON;
import vjson.JSONObject;
import vjson.deserializer.rule.*;
import vjson.util.ObjectBuilder;

import java.util.*;

public class MessageMonitor extends AbstractTool implements Tool {
    public MessageMonitor() {
        setConfigRule("MessageMonitor.vjson.txt", Config.rule);
    }

    @Override
    protected String buildName() {
        return I18n.get().toolName("message-monitor");
    }

    @Override
    protected Image buildIcon() {
        return ImageManager.get().load("/images/icon/message-monitor-icon.png");
    }

    @Override
    protected VScene buildScene() throws Exception {
        List<PcapNetworkInterface> allDevs;
        try {
            allDevs = Pcaps.findAllDevs();
        } catch (Exception e) {
            SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().messageMonitorWinPcapHelp());
            throw e;
        }
        return new S(allDevs);
    }

    @Override
    protected void terminate0() {
        var scene = (S) getScene();
        if (scene == null) {
            return;
        }
        scene.stop();
    }

    @Override
    protected void init(JSONObject config) {
        var scene = (S) getScene();
        var c = (Config) config;
        if (c.nic != null && !c.nic.isBlank()) {
            for (var entry : scene.ck2netifMappping.entrySet()) {
                if (entry.getValue().getName().equals(c.nic)) {
                    entry.getKey().setSelected(true);
                    break;
                }
            }
        }
        if (c.channels != null) {
            scene.worldChannel.setSelected(false);
            scene.guildChannel.setSelected(false);
            scene.teamChannel.setSelected(false);
            scene.coopChannel.setSelected(false);
            scene.privChannel.setSelected(false);
            for (var ch : c.channels) {
                switch (ch) {
                    case WORLD -> scene.worldChannel.setSelected(true);
                    case GUILD -> scene.guildChannel.setSelected(true);
                    case TEAM -> scene.teamChannel.setSelected(true);
                    case COOP -> scene.coopChannel.setSelected(true);
                    case PRIVATE -> scene.privChannel.setSelected(true);
                }
            }
        }
        if (c.watchAllChannels) {
            scene.allChannel.setSelected(true);
        }
        if (c.words != null && !c.words.isEmpty()) {
            scene.monitoringWords.setText(String.join(", ", c.words));
        }
    }

    private static class Config implements JSONObject {
        private String nic;
        private boolean watchAllChannels;
        private List<ChatChannel> channels;
        private List<String> words;

        public static final Rule<Config> rule = new ObjectRule<>(Config::new)
            .put("nic", (o, it) -> o.nic = it, StringRule.get())
            .put("watchAllChannels", (o, it) -> o.watchAllChannels = it, BoolRule.get())
            .put("channels", (o, it) -> o.channels = it,
                new ArrayRule<ArrayList<ChatChannel>, String>(ArrayList::new, (ls, o) -> ls.add(ChatChannel.valueOf0(o)), StringRule.get()))
            .put("words", (o, it) -> o.words = it,
                new ArrayRule<ArrayList<String>, String>(ArrayList::new, ArrayList::add, StringRule.get()));

        @Override
        public JSON.Object toJson() {
            return new ObjectBuilder()
                .put("nic", nic)
                .put("watchAllChannels", watchAllChannels)
                .putArray("channels", ab -> channels.forEach(o -> ab.add(o.name())))
                .putArray("words", ab -> words.forEach(ab::add))
                .build();
        }
    }

    private class S extends ToolScene {
        public final Map<CheckBox, PcapNetworkInterface> ck2netifMappping = new HashMap<>();
        private final CheckBox allChannel = new CheckBox(I18n.get().messageMonitorChannel(null)) {{
            FXUtils.disableFocusColor(this);
            FontManager.get().setFont(this);
            setTextFill(Theme.current().normalTextColor());
        }};
        private final CheckBox worldChannel = new CheckBox(I18n.get().messageMonitorChannel(ChatChannel.WORLD)) {{
            FXUtils.disableFocusColor(this);
            FontManager.get().setFont(this);
            setTextFill(Theme.current().normalTextColor());
        }};
        private final CheckBox guildChannel = new CheckBox(I18n.get().messageMonitorChannel(ChatChannel.GUILD)) {{
            FXUtils.disableFocusColor(this);
            FontManager.get().setFont(this);
            setTextFill(Theme.current().normalTextColor());
        }};
        private final CheckBox teamChannel = new CheckBox(I18n.get().messageMonitorChannel(ChatChannel.TEAM)) {{
            FXUtils.disableFocusColor(this);
            FontManager.get().setFont(this);
            setTextFill(Theme.current().normalTextColor());
        }};
        private final CheckBox coopChannel = new CheckBox(I18n.get().messageMonitorChannel(ChatChannel.COOP)) {{
            FXUtils.disableFocusColor(this);
            FontManager.get().setFont(this);
            setTextFill(Theme.current().normalTextColor());
        }};
        private final CheckBox privChannel = new CheckBox(I18n.get().messageMonitorChannel(ChatChannel.PRIVATE)) {{
            FXUtils.disableFocusColor(this);
            FontManager.get().setFont(this);
            setTextFill(Theme.current().normalTextColor());
        }};
        public final TextArea monitoringWords = new TextArea(I18n.get().messageMonitorWordsDefaultValue());
        private final FusionButton startBtn = new FusionButton(I18n.get().messageMonitorStartBtn());
        private final FusionButton stopBtn = new FusionButton(I18n.get().messageMonitorStopBtn());
        private TofPcap cap = null;

        public S(List<PcapNetworkInterface> allDevs) {
            enableAutoContentWidthHeight();

            var root = new VBox();
            root.setSpacing(20);
            FXUtils.observeWidthHeightCenter(getContentPane(), root);
            getContentPane().getChildren().add(root);

            // nic chooser
            {
                var title = new ThemeLabel(I18n.get().messageMonitorNicChooserTitle());
                root.getChildren().add(title);

                var scrollPane = new VScrollPane();
                scrollPane.getNode().setPrefWidth(600);
                scrollPane.getNode().setPrefHeight(300);
                root.getChildren().add(scrollPane.getNode());

                var vbox = new VBox();
                vbox.setSpacing(30);
                vbox.setLayoutX(1);

                scrollPane.setContent(vbox);

                for (var dev : allDevs) {
                    var pane = new FusionPane();
                    pane.getNode().setPrefWidth(590);
                    pane.getNode().setPrefHeight(120);

                    var ck = new CheckBox();
                    FXUtils.disableFocusColor(ck);
                    ck2netifMappping.put(ck, dev);
                    ck.setPadding(new Insets(10, 0, 0, 0));
                    ck.selectedProperty().addListener(ob -> {
                        if (ck.isSelected()) {
                            for (var c : ck2netifMappping.keySet()) {
                                if (c != ck) {
                                    c.setSelected(false);
                                }
                            }
                        }
                    });

                    pane.getContentPane().getChildren().add(new HBox() {{
                        setSpacing(20);
                        getChildren().addAll(
                            ck,
                            new VBox() {{
                                setSpacing(5);
                                getChildren().add(new ThemeLabel(dev.getName()) {{
                                    FontManager.get().setFont(this, s -> s.setSize(20).setFamily(FontManager.FONT_NAME_JetBrainsMono));
                                }});
                                for (var addr : dev.getAddresses()) {
                                    var ip = IP.from(addr.getAddress());
                                    getChildren().add(new ThemeLabel(ip.formatToIPString()) {{
                                        FontManager.get().setFont(this, s -> s.setSize(12).setFamily(FontManager.FONT_NAME_JetBrainsMono));
                                    }});
                                }
                            }}
                        );
                    }});
                    vbox.getChildren().add(pane.getNode());
                }
            }

            // server host
            {
                var hbox = new HBox();
                hbox.setSpacing(20);
                root.getChildren().add(hbox);

                var title = new ThemeLabel(I18n.get().messageMonitorServerHostTitle());
                hbox.getChildren().add(title);
            }

            // channels
            {
                var hbox = new HBox();
                hbox.setSpacing(30);
                root.getChildren().add(hbox);

                hbox.getChildren().addAll(allChannel, worldChannel, guildChannel, teamChannel, coopChannel, privChannel);

                var all = List.of(worldChannel, guildChannel, teamChannel, coopChannel, privChannel);
                allChannel.selectedProperty().addListener(ob -> {
                    if (allChannel.isSelected()) {
                        for (var c : all) {
                            c.setSelected(true);
                        }
                    }
                });
                for (var c : all) {
                    c.selectedProperty().addListener(ob -> {
                        if (!c.isSelected()) {
                            allChannel.setSelected(false);
                        }
                    });
                }

                // default select all
                allChannel.setSelected(true);
            }

            // words
            {
                var title = new ThemeLabel(I18n.get().messageMonitorWordsTitle());
                root.getChildren().add(title);

                monitoringWords.setPrefWidth(600);
                monitoringWords.setPrefHeight(80);
                var wrapper = new FusionW(monitoringWords);
                wrapper.getLabel().setAlignment(Pos.TOP_LEFT);
                root.getChildren().add(wrapper);
            }

            // buttons
            {
                var hbox = new HBox();
                hbox.setPrefWidth(600);
                hbox.setAlignment(Pos.CENTER_RIGHT);
                hbox.setSpacing(20);
                root.getChildren().add(hbox);

                hbox.getChildren().addAll(startBtn, stopBtn);

                startBtn.setPrefWidth(150);
                startBtn.setPrefHeight(50);
                stopBtn.setPrefWidth(150);
                stopBtn.setPrefHeight(50);

                startBtn.setOnAction(e -> start());
                stopBtn.setOnAction(e -> stop());

                resetButtons(false);
            }
        }

        public void start() {
            var cap = this.cap;
            if (cap != null) {
                SimpleAlert.showAndWait(Alert.AlertType.ERROR, I18n.get().messageMonitorAlreadyStartedAlert());
                return;
            }
            PcapNetworkInterface netif = null;
            for (var entry : ck2netifMappping.entrySet()) {
                if (entry.getKey().isSelected()) {
                    netif = entry.getValue();
                    break;
                }
            }
            if (netif == null) {
                SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().messageMonitorNoNetifSelectedAlert());
                return;
            }
            var words = Arrays.stream(monitoringWords.getText().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
            if (words.isEmpty()) {
                SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().messageMonitorEmptyWordsListAlert());
                return;
            }

            var conf = new Config();
            conf.nic = netif.getName();
            conf.watchAllChannels = allChannel.isSelected();
            conf.channels = new ArrayList<>();
            {
                if (worldChannel.isSelected()) conf.channels.add(ChatChannel.WORLD);
                if (guildChannel.isSelected()) conf.channels.add(ChatChannel.GUILD);
                if (teamChannel.isSelected()) conf.channels.add(ChatChannel.TEAM);
                if (coopChannel.isSelected()) conf.channels.add(ChatChannel.COOP);
                if (privChannel.isSelected()) conf.channels.add(ChatChannel.PRIVATE);
            }
            conf.words = words;
            MessageMonitor.this.save(conf);

            cap = new TofPcap(netif);
            cap.addListener(MessageType.CHAT, e -> handleEvent(e, conf));

            resetButtons(true);

            this.cap = cap;

            final var fcap = cap;
            new Thread(() -> {
                try {
                    fcap.start();
                } catch (Throwable t) {
                    Logger.error(LogType.ALERT, "pcap failed", t);
                    FXUtils.runOnFX(() -> StackTraceAlert.show(I18n.get().messageMonitorCapFailedAlert(), t));
                } finally {
                    resetButtons(false);
                }
            }).start();
        }

        private void handleEvent(MessageEvent e, Config conf) {
            if (e.type() != MessageType.CHAT) {
                return;
            }
            ChatMessage chat = (ChatMessage) e.msg();
            TaskManager.get().execute(() -> handleChat(chat, conf));
        }

        private void handleChat(ChatMessage chat, Config conf) {
            if (!conf.watchAllChannels) {
                if (!conf.channels.contains(chat.channel)) {
                    return;
                }
            }

            var msg = chat.message;
            for (var w : conf.words) {
                if (w.equals("*") || msg.contains(w)) {
                    FXUtils.runOnFX(() -> doNotify(chat));
                    break;
                }
            }
        }

        private void doNotify(ChatMessage chat) {
            var audio = AudioManager.get().loadAudio("/audio/alert.wav");
            audio.setVolume(0.6);
            audio.play();
            Notifications.create()
                .darkStyle()
                .position(OS.isWindows() ? Pos.BOTTOM_RIGHT : Pos.TOP_RIGHT)
                .title(I18n.get().messageMonitorNotificationTitle())
                .text(chat.message)
                .graphic(new ImageView(ImageManager.get().load("/images/icon/info.png")) {{
                    setFitWidth(40);
                    setFitHeight(40);
                }})
                .show();
        }

        public void stop() {
            var cap = this.cap;
            this.cap = null;
            if (cap == null) {
                return;
            }
            stopBtn.setDisable(true);
            TaskManager.get().execute(cap::stop);
        }

        private void resetButtons(boolean isStarted) {
            FXUtils.runOnFX(() -> {
                startBtn.setDisable(isStarted);
                stopBtn.setDisable(!isStarted);
            });
        }
    }
}
