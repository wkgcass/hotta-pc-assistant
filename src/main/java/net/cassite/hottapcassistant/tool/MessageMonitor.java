package net.cassite.hottapcassistant.tool;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.base.util.OS;
import io.vproxy.vfd.IP;
import io.vproxy.vfx.control.scroll.VScrollPane;
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
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
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
        var allDevs = Pcaps.findAllDevs();
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
        if (c.serverHost != null && !c.serverHost.isBlank()) {
            scene.serverHost.setValue(new ServerInfo(c.serverHost));
        }
        if (c.channels != null) {
            scene.worldChannel.setSelected(false);
            scene.guildChannel.setSelected(false);
            scene.teamChannel.setSelected(false);
            scene.coopChannel.setSelected(false);
            for (var ch : c.channels) {
                switch (ch) {
                    case WORLD -> scene.worldChannel.setSelected(true);
                    case GUILD -> scene.guildChannel.setSelected(true);
                    case TEAM -> scene.teamChannel.setSelected(true);
                    case COOP -> scene.coopChannel.setSelected(true);
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
        private String serverHost;
        private boolean watchAllChannels;
        private List<ChatChannel> channels;
        private List<String> words;

        public static final Rule<Config> rule = new ObjectRule<>(Config::new)
            .put("nic", (o, it) -> o.nic = it, StringRule.get())
            .put("serverHost", (o, it) -> o.serverHost = it, StringRule.get())
            .put("watchAllChannels", (o, it) -> o.watchAllChannels = it, BoolRule.get())
            .put("channels", (o, it) -> o.channels = it,
                new ArrayRule<ArrayList<ChatChannel>, String>(ArrayList::new, (ls, o) -> ls.add(ChatChannel.valueOf0(o)), StringRule.get()))
            .put("words", (o, it) -> o.words = it,
                new ArrayRule<ArrayList<String>, String>(ArrayList::new, ArrayList::add, StringRule.get()));

        @Override
        public JSON.Object toJson() {
            return new ObjectBuilder()
                .put("nic", nic)
                .put("serverHost", serverHost)
                .put("watchAllChannels", watchAllChannels)
                .putArray("channels", ab -> channels.forEach(o -> ab.add(o.name())))
                .putArray("words", ab -> words.forEach(ab::add))
                .build();
        }
    }

    private class S extends ToolScene {
        public final Map<CheckBox, PcapNetworkInterface> ck2netifMappping = new HashMap<>();
        public final ComboBox<ServerInfo> serverHost = new ComboBox<>() {{
            setConverter(new StringConverter<>() {
                @Override
                public String toString(ServerInfo object) {
                    return ServerInfo.toDisplayString(object);
                }

                @Override
                public ServerInfo fromString(String string) {
                    if (string.isBlank()) return null;
                    return new ServerInfo(string);
                }
            });
            setEditable(true);
            setItems(FXCollections.observableArrayList(serverInfoList));
            setValue(serverInfoList.get(0));
        }};
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

                serverHost.setPrefWidth(300);
                var wrapper = new FusionW(serverHost);
                wrapper.getLabel().setAlignment(Pos.TOP_LEFT);
                FontManager.get().setFont(wrapper.getLabel(), s -> s.setFamily(FontManager.FONT_NAME_JetBrainsMono));
                hbox.getChildren().add(wrapper);
            }

            // channels
            {
                var hbox = new HBox();
                hbox.setSpacing(30);
                root.getChildren().add(hbox);

                hbox.getChildren().addAll(allChannel, worldChannel, guildChannel, teamChannel, coopChannel);

                var all = List.of(worldChannel, guildChannel, teamChannel, coopChannel);
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
            var serverHost = ServerInfo.toIPString(this.serverHost.getValue());
            if (!IP.isIpLiteral(serverHost)) {
                SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().messageMonitorInvalidServerHostAlert(serverHost));
                return;
            }
            var words = Arrays.stream(monitoringWords.getText().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
            if (words.isEmpty()) {
                SimpleAlert.showAndWait(Alert.AlertType.INFORMATION, I18n.get().messageMonitorEmptyWordsListAlert());
                return;
            }

            var conf = new Config();
            conf.nic = netif.getName();
            conf.serverHost = serverHost;
            conf.watchAllChannels = allChannel.isSelected();
            conf.channels = new ArrayList<>();
            {
                if (worldChannel.isSelected()) conf.channels.add(ChatChannel.WORLD);
                if (guildChannel.isSelected()) conf.channels.add(ChatChannel.GUILD);
                if (teamChannel.isSelected()) conf.channels.add(ChatChannel.TEAM);
                if (coopChannel.isSelected()) conf.channels.add(ChatChannel.COOP);
            }
            conf.words = words;
            MessageMonitor.this.save(conf);

            cap = new TofPcap(netif, IP.from(serverHost));
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
                if (msg.contains(w)) {
                    FXUtils.runOnFX(() -> doNotify(chat));
                    break;
                }
            }
        }

        private void doNotify(ChatMessage chat) {
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

    private static class ServerInfo {
        public String name;
        public String ip;
        private String text;

        public ServerInfo() {
        }

        public ServerInfo(String text) {
            text = text.trim();
            this.text = text;
            var idx = text.indexOf("(");
            if (idx != -1) {
                var a = text.substring(0, idx).trim();
                var b = text.substring(idx);
                if (IP.isIpLiteral(a)) {
                    ip = a;
                }
                if (b.endsWith(")")) {
                    name = b.substring(1, b.length() - 1).trim();
                }
            }
        }

        public static final Rule<ServerInfo> rule = new ObjectRule<>(ServerInfo::new)
            .put("name", (o, it) -> o.name = it, StringRule.get())
            .put("ip", (o, it) -> o.ip = it, StringRule.get());

        public static String toIPString(ServerInfo value) {
            if (value == null) return "";
            if (value.ip != null) return value.ip;
            if (value.text != null) return value.text;
            if (value.name != null) return value.name;
            return "";
        }

        public static String toDisplayString(ServerInfo value) {
            if (value.ip != null && value.name != null)
                return value.ip + "(" + value.name + ")";
            if (value.ip != null)
                return value.ip;
            if (value.text != null)
                return value.text;
            if (value.name != null)
                return "(" + value.name + ")";
            return "";
        }
    }

    private static final String serversJson = """
        [
            {
                "name":"离州",
                "ip":"39.106.8.2"
            },
            {
                "name":"白月破晓",
                "ip":"101.200.45.104"
            },
            {
                "name":"千镜",
                "ip":"123.56.86.19"
            },
            {
                "name":"银岸",
                "ip":"123.56.86.19"
            },
            {
                "name":"迷城",
                "ip":"101.200.45.104"
            },
            {
                "name":"月影",
                "ip":"101.200.45.104"
            },
            {
                "name":"雾泽",
                "ip":"47.93.87.105"
            },
            {
                "name":"溟海",
                "ip":"47.93.156.58"
            },
            {
                "name":"幽岩",
                "ip":"47.93.80.150"
            },
            {
                "name":"茵纳斯",
                "ip":"101.201.211.109"
            },
            {
                "name":"星岛HT-01",
                "ip":"59.110.113.76"
            },
            {
                "name":"星岛HT-02",
                "ip":"39.96.163.203"
            },
            {
                "name":"星岛HT-03",
                "ip":"39.96.163.203"
            },
            {
                "name":"星岛HT-04",
                "ip":"39.96.163.203"
            },
            {
                "name":"星岛HT-05",
                "ip":"39.96.163.203"
            },
            {
                "name":"星岛HT-06",
                "ip":"39.96.163.203"
            },
            {
                "name":"星岛HT-07",
                "ip":"39.96.163.203"
            },
            {
                "name":"星岛HT-08",
                "ip":"39.96.163.203"
            },
            {
                "name":"星岛HT-09",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-10",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-11",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-12",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-13",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-14",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-15",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-16",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-17",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-18",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-19",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-20",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-21",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-22",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-23",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-24",
                "ip":"123.56.149.221"
            },
            {
                "name":"星岛HT-25",
                "ip":"123.56.149.221"
            },
            {
                "name":"班吉斯HT-01",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-02",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-03",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-04",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-05",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-06",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-07",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-08",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-09",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-10",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-11",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-12",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-13",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-14",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-15",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-16",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-17",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-18",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-19",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-20",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-21",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-22",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-23",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-24",
                "ip":"101.200.234.225"
            },
            {
                "name":"班吉斯HT-25",
                "ip":"101.200.234.225"
            },
            {
                "name":"纳维亚HT-01",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-02",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-03",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-04",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-05",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-06",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-07",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-08",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-09",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-10",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-11",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-12",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-13",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-14",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-15",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-16",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-17",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-18",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-19",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-20",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-21",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-22",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-23",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-24",
                "ip":"39.105.120.152"
            },
            {
                "name":"纳维亚HT-25",
                "ip":"39.105.120.152"
            },
            {
                "name":"克罗恩HT-01",
                "ip":"123.57.217.236"
            },
            {
                "name":"克罗恩HT-02",
                "ip":"123.57.217.236"
            },
            {
                "name":"克罗恩HT-03",
                "ip":"123.57.217.236"
            },
            {
                "name":"克罗恩HT-04",
                "ip":"123.57.217.236"
            },
            {
                "name":"克罗恩HT-05",
                "ip":"123.57.217.236"
            },
            {
                "name":"克罗恩HT-06",
                "ip":"123.57.217.236"
            },
            {
                "name":"克罗恩HT-07",
                "ip":"123.57.217.236"
            },
            {
                "name":"克罗恩HT-08",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-01",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-02",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-03",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-04",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-05",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-06",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-07",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-08",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-09",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-10",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-11",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-12",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-13",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-14",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-15",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-16",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-17",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-18",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-19",
                "ip":"123.57.217.236"
            },
            {
                "name":"海嘉德HT-20",
                "ip":"123.57.217.236"
            }
        ]
        """;
    private static final List<ServerInfo> serverInfoList = JSON.deserialize(serversJson, JSONObject.buildArrayRule(ServerInfo.rule));
}
