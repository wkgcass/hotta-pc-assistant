package net.cassite.hottapcassistant.config;

import net.cassite.hottapcassistant.entity.Key;
import net.cassite.hottapcassistant.entity.KeyBinding;
import net.cassite.hottapcassistant.util.Logger;
import net.cassite.hottapcassistant.util.Utils;
import vjson.JSON;
import vjson.ex.ParserException;
import vjson.parser.StringParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class InputConfig {
    public final String path;

    private InputConfig(String path) {
        this.path = path;
    }

    public static InputConfig ofSaved(String path) {
        return new InputConfig(path);
    }

    private static final List<String> availableActions = List.of(
        "Artifact_1",
        "Artifact_2",
        "ChangeArtifact0",
        "ChangeWeapon0",
        "ChangeWeapon1",
        "ChangeWeapon2",
        "Chat",
        "Melee",
        "Melee_Key",
        "Evade",
        "Evade_Key",
        "WeaponSkill",
        "SwitchTarget",
        "Interaction",
        "Jump",
        "Crouch",
        "Diving",
        "Mount",
        "Vines",
        "Supply",
        "Map",
        "Menu_1",
        "Menu_2",
        "Menu_3",
        "Menu_4",
        "UI_Avatar",
        "UI_Bag",
        "UI_SelfMenu",
        "UI_Weapon",
        "SwitchMouse",
        "Track",

        "Artifact_1_BreakFate",
        "Artifact_2_BreakFate",
        "Artifact_gousuo_BreakFate",
        "ChangeArtifact0_BreakFate",
        "ChangeWeapon0_BreakFate",
        "ChangeWeapon1_BreakFate",
        "ChangeWeapon2_BreakFate",
        "Evade_Key_BreakFate",
        "WeaponSkill_BreakFate",
        "SwitchTarget_BreakFate",
        "Interaction_BreakFate",
        "Jump_BreakFate",
        "Crouch_BreakFate",
        "Mount_BreakFate",
        "Supply1_BreakFate",
        "Supply2_BreakFate",
        "sign_BreakFate",
        "Map_BreakFate",
        "UI_Avatar_BreakFate",
        "UI_Bag_BreakFate",
        "UI_SelfMenu_BreakFate",
        "UI_Weapon_BreakFate"
    );
    private static final List<String> availableAxisActions = List.of(
        "MoveForward",
        "MoveRight",
        "LookUpRate",
        "TurnRate",
        "MoveForward_BreakFate",
        "MoveRight_BreakFate"
    );

    public List<KeyBinding> read() throws IOException {
        var actions = new ArrayList<KeyBinding>();
        var lines = Files.readAllLines(Path.of(path));
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            KeyBinding kb;
            if (line.startsWith("ActionMappings")) {
                kb = readActionMappings(line, i);
            } else if (line.startsWith("AxisMappings")) {
                kb = readAxisConfig(line, i);
            } else {
                continue;
            }
            if (kb != null) {
                if ((availableActions.contains(kb.action) && !kb.isAxis) || (availableAxisActions.contains(kb.action) && kb.isAxis)) {
                    actions.add(kb);
                }
            }
        }
        actions.sort((a, b) -> {
            if (availableActions.contains(a.action) && availableActions.contains(b.action)) {
                return availableActions.indexOf(a.action) - availableActions.indexOf(b.action);
            } else if (availableActions.contains(a.action)) {
                return -1;
            } else if (availableActions.contains(b.action)) {
                return 1;
            } else {
                if (availableAxisActions.contains(a.action) && availableAxisActions.contains(b.action)) {
                    return availableAxisActions.indexOf(a.action) - availableAxisActions.indexOf(b.action);
                } else if (availableAxisActions.contains(a.action)) {
                    return -1;
                } else if (availableAxisActions.contains(b.action)) {
                    return 1;
                } else {
                    return a.action.compareTo(b.action);
                }
            }
        });
        return actions;
    }

    private KeyBinding readActionMappings(String line, int lineIndex) {
        line = Utils.readValueOf("ActionMappings", line);
        if (line == null) {
            return null;
        }
        if (!line.startsWith("(") || !line.endsWith(")")) {
            return null;
        }
        line = line.substring(1, line.length() - 1);
        String[] split = line.split(",");

        String action = null;
        Boolean ctrl = false;
        Boolean alt = false;
        Boolean shift = false;
        Key key = null;

        for (var s : split) {
            String v = Utils.readValueOf("ActionName", s);
            if (v != null) {
                JSON.String jsonAction = null;
                try {
                    jsonAction = new StringParser().last(v);
                } catch (ParserException ignore) {
                }
                if (jsonAction == null) {
                    continue;
                }
                action = jsonAction.toJavaObject();
                continue;
            }
            v = Utils.readValueOf("bCtrl", s);
            if (v != null) {
                ctrl = Utils.booleanValue(v);
                continue;
            }
            v = Utils.readValueOf("bAlt", s);
            if (v != null) {
                alt = Utils.booleanValue(v);
                continue;
            }
            v = Utils.readValueOf("bShift", s);
            if (v != null) {
                shift = Utils.booleanValue(v);
                continue;
            }
            v = Utils.readValueOf("Key", s);
            if (v != null) {
                key = new Key(v);
                //noinspection UnnecessaryContinue
                continue;
            }
        }

        if (action == null || ctrl == null || alt == null || shift == null || key == null || !key.isValid()) {
            if (!allowUnknownKey(action, ctrl, alt, shift, key)) {
                if (key == null || !knownToBeUnsupported(key.toString())) {
                    Logger.debug("input line skipped: " + line + " extracted data: " + action + ", " + ctrl + ", " + alt + ", " + shift + ", " + key);
                }
                return null;
            }
        }

        var ret = new KeyBinding();
        ret.action = action;
        ret.ctrl = ctrl;
        ret.alt = alt;
        ret.shift = shift;
        ret.key = key;
        ret.lineIndex = lineIndex;
        return ret;
    }

    private boolean allowUnknownKey(String action, Boolean ctrl, Boolean alt, Boolean shift, Key key) {
        return action != null && ctrl != null && alt != null && shift != null && key != null;
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    private boolean knownToBeUnsupported(String s) {
        if (s.startsWith("Gamepad")) {
            return true;
        }
        if (s.startsWith("Mouse")) {
            return true;
        }
        if (s.startsWith("Touch")) {
            return true;
        }
        if (s.equalsIgnoreCase("add") || s.equalsIgnoreCase("subtract")) {
            return true;
        }
        //noinspection RedundantIfStatement
        if (s.equalsIgnoreCase("rt")) {
            return true;
        }
        return false;
    }

    private KeyBinding readAxisConfig(String line, int lineIndex) {
        line = Utils.readValueOf("AxisMappings", line);
        if (line == null) {
            return null;
        }
        if (!line.startsWith("(") || !line.endsWith(")")) {
            return null;
        }
        line = line.substring(1, line.length() - 1);
        String[] split = line.split(",");

        String action = null;
        Key key = null;
        Double scale = null;

        for (var s : split) {
            String v = Utils.readValueOf("AxisName", s);
            if (v != null) {
                JSON.String jsonAction = null;
                try {
                    jsonAction = new StringParser().last(v);
                } catch (ParserException ignore) {
                }
                if (jsonAction == null) {
                    continue;
                }
                action = jsonAction.toJavaObject();
                continue;
            }
            v = Utils.readValueOf("Scale", s);
            if (v != null) {
                try {
                    scale = Double.parseDouble(v);
                } catch (NumberFormatException ignore) {
                }
                continue;
            }
            v = Utils.readValueOf("Key", s);
            if (v != null) {
                key = new Key(v);
                //noinspection UnnecessaryContinue
                continue;
            }
        }

        if (action == null || scale == null || key == null || !key.isValid()) {
            if (key == null || !knownToBeUnsupported(key.toString())) {
                Logger.debug("axis input line skipped: " + line + " extracted data: " + action + ", " + scale + ", " + key);
            }
            return null;
        }

        var ret = new KeyBinding();
        ret.action = action;
        ret.key = key;
        ret.isAxis = true;
        ret.lineIndex = lineIndex;
        ret.scale = scale;
        return ret;
    }

    public void write(List<KeyBinding> bindings) throws IOException {
        Path path = Path.of(this.path);
        var lines = Files.readAllLines(path);
        for (var kb : bindings) {
            if (!kb.key.isValid()) {
                continue;
            }
            if (kb.lineIndex == -1) {
                lines.add(kb.toString());
            } else {
                if (kb.lineIndex >= lines.size()) {
                    throw new IOException("input file has been changed, lines cannot match");
                }
                lines.set(kb.lineIndex, kb.toString());
            }
        }
        Utils.writeFile(path, String.join("\n", lines));
    }
}
