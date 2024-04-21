package net.cassite.hottapcassistant.i18n;

import io.vproxy.vfx.ui.loading.LoadingItem;
import net.cassite.tofpcap.messages.ChatChannel;

import java.util.ArrayList;
import java.util.Set;

public class EnUs extends I18n {
    private final ZhCn delegate = new ZhCn();

    @Override
    public String id() {
        return "enus";
    }

    @Override
    public String loadingFontFailed(String name) {
        return "Loading " + name + " font failed";
    }

    @Override
    public String titleMainScreen() {
        return "Hotta PC Assistant";
    }

    @Override
    public String titleMainScreenDevVersion() {
        return "Developer Version";
    }

    @Override
    public String toolNameGameSettings() {
        return "Game Settings";
    }

    @Override
    public String toolNameInputSettings() {
        return "Input Settings";
    }

    @Override
    public String toolNameMacro() {
        return "Key Macros";
    }

    @Override
    public String toolNameFishing() {
        return "Auto Fishing";
    }

    @Override
    public String toolNameCoolDown() {
        return "Cooldown Indicator";
    }

    @Override
    public String toolNameToolBox() {
        return "Tool Box";
    }

    @Override
    public String toolNameXBox() {
        return "Customize Xbox Keys";
    }

    @Override
    public String toolNameAbout() {
        return "About";
    }

    @Override
    public String toolNameLog() {
        return "Logs";
    }

    @Override
    public String toolNameReset() {
        return "Reset / Fix";
    }

    @Override
    public String toolIsLocked(String name) {
        name = switch (name) {
            case "macro" -> toolNameMacro();
            case "fishing" -> toolNameFishing();
            case "multi" -> toolName("multi-hotta-instance");
            default -> name;
        };
        return "This tool is currently unavailable: " + name;
    }

    @Override
    public String toolNameWelcome() {
        return "Welcome Page";
    }

    @Override
    public String selectGameLocation() {
        return "Select game path";
    }

    @Override
    public String selectGlobalServerGameLocation() {
        return "Select international server game path";
    }

    @Override
    public String selectButton() {
        return "Select";
    }

    @Override
    public String autoSearchButton() {
        return "Auto Search";
    }

    @Override
    public String autoSearchFailed() {
        return "Auto search failed, please manually select a path";
    }

    @Override
    public String selectGameLocationDescription() {
        return "Please select the game path. You can manually select the path where gameLauncher.exe is located, or you can click \"Auto Search\"";
    }

    @Override
    public String selectGlobalServerGameLocationDescription() {
        return "Please select the international server game path, manually select the location of tof_launcher.exe, or click \"Auto Search\"";
    }

    @Override
    public String chosenWrongGameFile() {
        return "Selected wrong file, please select gameLauncher.exe";
    }

    @Override
    public String chosenWrongGlobalServerGameFile() {
        return "Selected wrong file, please select tof_launcher.exe";
    }

    @Override
    public String chosenWrongGlobalServerGameFileNoParentDir() {
        return "Selected wrong file, parent directory not found";
    }

    @Override
    public String selectSavedLocationDescription() {
        return "Please select the Saved location for game config files, which you can manually choose (usually %HOMEPATH%\\AppData\\Local\\Hotta\\Saved\\), or click \"Auto Search\"";
    }

    @Override
    public String selectSavedLocation() {
        return "Select Saved config file path";
    }

    @Override
    public String alertChangeSavedDirectory() {
        return "You do not usually need to change the Saved directory, only when switching to test server/Taiwan server. If you really need to modify, hold down the ALT key and click this button";
    }

    @Override
    public String chosenWrongSavedDirectory() {
        return "Selected the wrong path, please select a folder";
    }

    @Override
    public String hotkeyColumnNameAction() {
        return "Trigger Action";
    }

    @Override
    public String hotkeyColumnNameKey() {
        return "Key";
    }

    @Override
    public String hotkeyColumnNameScale() {
        return "Speed";
    }

    @Override
    public String keyChooserDesc() {
        return "Press the keyboard button you want to configure, or click using the left or right mouse button";
    }

    @Override
    public String keyChooserDescWithoutMouse() {
        return "Press the keyboard button you want to configure";
    }

    @Override
    public String unsupportedKeyErrorMessage() {
        return "This key is not yet supported";
    }

    @Override
    public String notFloatingPointValue() {
        return "The entered value is not a floating point number";
    }

    @Override
    public String inputActionMapping(String action) {
        return switch (action) {
            case "Artifact_1" -> "Artifact 1";
            case "Artifact_2" -> "Artifact 2";
            case "Artifact_1_BreakFate" -> "Break Fate-Artifact 1";
            case "Artifact_2_BreakFate" -> "Break Fate-Artifact 2";
            case "Artifact_gousuo_BreakFate" -> "Break Fate-Hook";
            case "ChangeArtifact0" -> "Switch Artifact";
            case "ChangeArtifact0_BreakFate" -> "Break Fate-Switch Artifact";
            case "ChangeWeapon0" -> "Weapon 1";
            case "ChangeWeapon0_BreakFate" -> "Break Fate-Weapon 1";
            case "ChangeWeapon1" -> "Weapon 2";
            case "ChangeWeapon1_BreakFate" -> "Break Fate-Weapon 2";
            case "ChangeWeapon2" -> "Weapon 3";
            case "ChangeWeapon2_BreakFate" -> "Break Fate-Weapon 3";
            case "Chat" -> "Open Chat Window";
            case "Crouch" -> "Crouch";
            case "Diving" -> "Diving";
            case "Crouch_BreakFate" -> "Break Fate-Crouch";
            case "Evade" -> "Dodge (Mouse)";
            case "Evade_Key" -> "Dodge";
            case "Evade_Key_BreakFate" -> "Break Fate-Dodge";
            case "Interaction" -> "Interaction";
            case "Interaction_BreakFate" -> "Break Fate-Interaction";
            case "Introduce" -> "Introduce/Help";
            case "Introduce_BreakFate" -> "Break Fate-Introduce/Help";
            case "Jump" -> "Jump";
            case "Jump_BreakFate" -> "Break Fate-Jump";
            case "Map" -> "Map";
            case "Map_BreakFate" -> "Break Fate-Map";
            case "Melee" -> "Attack (Mouse)";
            case "Melee_Key" -> "Attack (Keyboard)";
            case "Menu_1" -> "Menu 1";
            case "Menu_2" -> "Menu 2";
            case "Menu_3" -> "Menu 3";
            case "Menu_4" -> "Menu 4";
            case "Mount" -> "Mount";
            case "Mount_BreakFate" -> "Break Fate-Mount";
            case "sign_BreakFate" -> "Break Fate-Signal";
            case "Supply" -> "Use Food";
            case "Supply1_BreakFate" -> "Break Fate-Blood Medicine";
            case "Supply2_BreakFate" -> "Break Fate-Shield Medicine";
            case "SwitchMouse" -> "Unlock Mouse";
            case "SwitchTarget" -> "Switch Target";
            case "SwitchTarget_BreakFate" -> "Break Fate-Switch Target";
            case "Track" -> "Quest Track";
            case "UI_Avatar" -> "Character Interface";
            case "UI_Avatar_BreakFate" -> "Break Fate-Character Interface";
            case "UI_Bag" -> "Bag Interface";
            case "UI_Bag_BreakFate" -> "Break Fate-Bag Interface";
            case "UI_SelfMenu" -> "Menu";
            case "UI_SelfMenu_BreakFate" -> "Break Fate-Menu";
            case "UI_Weapon" -> "Weapon Interface";
            case "UI_Weapon_BreakFate" -> "Break Fate-Weapon Interface";
            case "WeaponSkill" -> "Weapon Skill";
            case "SkillAdditional" -> "Additional Skill";
            case "WeaponSkill_BreakFate" -> "Break Fate-Weapon Skill";
            case "Vines" -> "Climb";
            case "LookUpRate" -> "View Angle [Up] Down";
            case "MoveForward" -> "[Forward] Backward Move";
            case "MoveForward_BreakFate" -> "Break Fate-[Forward] Backward Move";
            case "MoveRight" -> "Move Left [Right]";
            case "MoveRight_BreakFate" -> "Break Fate- Move Left [Right]";
            case "TurnRate" -> "View Angle Left [Right]";
            default -> super.inputActionMapping(action);
        };
    }

    @Override
    public String configNameMapping(String name) {
        return switch (name) {
            case "bAutoCombatDiet" -> "Auto Combat-Use Food";
            case "AutoCombatDietHpPercent" -> "Auto Combat-HP Percentage When Using Food";
            case "bAutoCombatArtifactSkill" -> "Auto Combat-Use Artifact";
            case "bAutoCombatChangeWeaponSkill" -> "Auto Combat-Switch Weapon";
            case "fFightCameraDistance" -> "Fight Camera Distance";
            case "MaxVisibilityPlayer" -> "Max Visibility Player Number";
            case "FrameRateLimit" -> "Frame Rate Limit";
            case "ResolutionSizeX" -> "Resolution X";
            case "ResolutionSizeY" -> "Resolution Y";
            case "FullscreenMode" -> "Fullscreen Mode [1=Fullscreen|2=Windowed]";
            case "bPreferD3D12InGame" -> "Use DX12";
            default -> super.configNameMapping(name);
        };
    }

    @Override
    public String applyButton() {
        return "Apply";
    }

    @Override
    public String resetButton() {
        return "Reset";
    }

    @Override
    public String exitCheckMessage() {
        return "There are unsaved changes, do you want to quit directly?";
    }

    @Override
    public String gamePathNotSet() {
        return "Game path is not set";
    }

    @Override
    public String savedPathNotSet() {
        return "Game config file path is not set";
    }

    @Override
    public String gamePathIsNotDirectory() {
        return "The designated game path is not a folder";
    }

    @Override
    public String savedPathIsNotDirectory() {
        return "The designated game config file path is not a folder";
    }

    @Override
    public String openBrowserForDownloadingFailed(String url) {
        return "Opening browser failed. You can manually type the following URL for downloading: " + url + " or try to paste as the URL has been copied to your clipboard.";
    }

    @Override
    public String invalidResolutionValue() {
        return "Resolution format error, format is NxN";
    }

    @Override
    public String invalidResolutionIntegerValue() {
        return "Resolution format error, number is incorrect";
    }

    @Override
    public String invalidNumberValue() {
        return "Number format error";
    }

    @Override
    public String fightRangeOutOfBounds(double min, double max) {
        return "View distance out of range, limit: " + min + "~" + max;
    }

    @Override
    public String invalidConfigInFile(String key, String value) {
        return "Error in the config file, " + configNameMapping(key) + "=" + value;
    }

    @Override
    public String discardChangesConfirm() {
        return "Do you want to discard the changes made?";
    }

    @Override
    public String settingColumnNameName() {
        return "Configuration Item";
    }

    @Override
    public String settingColumnNameValue() {
        return "Value";
    }

    @Override
    public String openGameUserSettingsIni() {
        return "Open GameUserSettings.ini";
    }

    @Override
    public String openInputIni() {
        return "Open Input.ini";
    }

    @Override
    public String macroTipsButton() {
        return "How to Customize Macros?";
    }

    @Override
    public String editMacro() {
        return "Edit Key Macros";
    }

    @Override
    public String reloadMacro() {
        return "Reload Key Macros";
    }

    @Override
    public String macroTips() {
        return """
                   To configure a new macro, the following steps are required:
                     1. Click on""".trim() + editMacro() +
               """
                   in the assistant, define a new macro according to the convention, and save the configuration
                     2. Click on""".trim() + reloadMacro() +
               """
                   in the assistant, and then define its trigger shortcut key in the table
                     3. Enable macro and press the shortcut key to trigger
                   When configuring a new macro, the following properties need to be specified:
                     1. name = Macro name
                     2. type = Macro type, currently supports:
                                 NORMAL: Normal macro, run only once
                                 INFINITE_LOOP: Infinite loop
                                 FINITE_LOOP: Finite loop
                     3. steps = Step list, each item is a command executed by the macro, such as pressing the keyboard, waiting for time, etc.
                     4. loopLimit = For finite loop macros, you also need to specify its number of loops
                   To configure the steps, you need to specify the type of the step first, and the parameters needed will vary depending on the type:
                     1. @type = Delay delay
                        Parameter:
                          millis = Duration of the delay, the unit of this value is milliseconds
                     2. @type = KeyPress Press a keyboard key or mouse button
                        Parameter:
                          key = String of keyboard key or mouse button, in the format supported by UnrealEngine
                     3. @type = KeyRelease Release a keyboard key or mouse button
                        Parameter:
                          key = String of keyboard key or mouse button, in the format supported by UnrealEngine
                     4. @type = SafePoint Can interrupt in this place when stopping the script
                        Parameter: none
                     5. @type = MouseMove moves your mouse to the specific position
                        Parameter:
                          x = x coordinate (integer)
                          y = y coordinate (integer)
                     6. @type = StressBegin begins consuming the cpu resource
                        Parameter: none
                     7. @type = StressEnd ends consuming the cpu resource
                        Parameter: none
                   For key values, please refer to UnrealEngine standards, most of which have been implemented except for the numeric keypad,
                   or view the io/vproxy/vfx/entity/input/KeyCode.java code in the vfx library
                   Commonly used values ​​are:
                     1. LeftMouseButton, RightMouseButton: Mouse left button/right button
                     2. One, Two, Three, ..., Nine, Zero: Main keyboard area 1, 2, 3, ..., 9, 0
                     3. A~Z: letter keys on the main keyboard
                     4. LeftControl, RightShift, LeftAlt, ...: Left CTRL, Right Shift, Left Alt
                   Example:
                   1. The sledgehammer macro needs to hold down the left mouse button for 3 seconds then release, wait for 0.5 seconds then continue to the loop execution
                      {
                        name = Sledgehammer Macro
                        type = INFINITE_LOOP
                        steps = [
                          { @type: KeyPress, key = LeftMouseButton }
                          { @type: Delay, millis = 3000 }
                          { @type: KeyRelease, key = LeftMouseButton }
                          { @type: Delay, millis = 500 }
                        ]
                      }
                   """.trim();
    }

    @Override
    public String aboutEmptyTableOrMissingFields() {
        return "Why is my table empty, or some fields are missing?";
    }

    @Override
    public String detailAboutEmptyTableOrMissingFields() {
        return """
            This tool only allows modifying key settings that exist in the configuration file. If the setting does not exist in the configuration file, it will not be displayed.
            1. After modifying the direction keys (WSAD) in the game, special configurations such as direction and view rotation will be written into the configuration file.
            2. After modifying other keys in the game, all remaining configurations will be written into the configuration file.
            In addition, for special configurations such as direction and view rotation, the "Rate" column can specify its direction, and the square brackets in the field are its positive direction""";
    }

    @Override
    public String openFileFailed() {
        return "Failed to open file";
    }

    @Override
    public String applyPatchFailed() {
        return "Failed to apply patch";
    }

    @Override
    public String launchGameFailed() {
        return "Failed to launch game";
    }

    @Override
    public String macroSwitchButtonLabel() {
        return "Turn on key macro";
    }

    @Override
    public String rememberMousePositionButtonLabel() {
        return "Remember mouse position";
    }

    @Override
    public String macroAlertLabel() {
        return """
            This program needs to be run as administrator in order to use macros in the game.
            Additionally, please note this tool is not a driver-level macro. It uses the JavaFX Robot library to call the Windows API so it can theoretically be detected.""";
    }

    @Override
    public String macroColumnNameEnable() {
        return "Enable";
    }

    @Override
    public String macroColumnNameName() {
        return "Macro Command";
    }

    @Override
    public String macroColumnNameType() {
        return "Type";
    }

    @Override
    public String macroTypeNormal() {
        return "Normal";
    }

    @Override
    public String macroTypeInfiniteLoop() {
        return "Infinite Loop";
    }

    @Override
    public String macroTypeFiniteLoop() {
        return "Finite Loop";
    }

    @Override
    public String macroColumnNameStatus() {
        return "Status";
    }

    @Override
    public String macroStatusRunning() {
        return "Running...";
    }

    @Override
    public String macroStatusStopped() {
        return "Stopped";
    }

    @Override
    public String macroStatusStopping() {
        return "Stopping...";
    }

    @Override
    public String knowConsequencePrompt() {
        return "I am aware of the technical principles and bear the risks myself";
    }

    @Override
    public String fishingStartButton() {
        return "Start";
    }

    @Override
    public String fishingStopKey() {
        return "End key";
    }

    @Override
    public String fishingLeftKey() {
        return "Move left";
    }

    @Override
    public String fishingRightKey() {
        return "Move right";
    }

    @Override
    public String fishingCastKey() {
        return "Cast rod";
    }

    @Override
    public String fishingSkipFishingPointCheckBox() {
        return "Skip green point detection, change to a delay of 5 seconds";
    }

    @Override
    public String fishingUseCastKeyCheckBox() {
        return "Use the keyboard key to cast the rod, do not use the mouse click, and use ESC to close the settlement interface (you need to manually click the game interface once when fishing for the first time)";
    }

    @Override
    public String fishingDebugCheckBox() {
        return "Debug mode. When enabled, screen captures will be stored in the clipboard";
    }

    @Override
    public String resetFishing() {
        return "Reset parameters";
    }

    @Override
    public String configureFishing() {
        return "Configure fishing parameters";
    }

    @Override
    public String configureFishingHelpMsg() {
        return """
            This tool supports automatic fishing rod retraction when detecting fish stamina, as well as recovery after fishing failure. There's no need to set game resolution or window position,
            but it requires the tool itself to be configured, which is divided into two steps:
            The first step is to configure the position of the fishing point and fishing button.
            The second step is to configure the position of the fish's stamina bar and the yellow slider.

            The second step requires fishing once, so we suggest switching to green bait when doing a complete configuration.
            As the fishing point position may vary each time, you need to configure the green point position each time you start fishing, and the fishing will automatically start after configuration.

            If the indicator shows a fluctuating cursor during fishing, it may be because the program misjudged the white background as the cursor. Try to change the viewpoint to alter the top background and try again.""";
    }

    @Override
    public String fishTutorialLinkDesc() {
        return "Click here to watch the fishing tool tutorial video";
    }

    @Override
    public String fishingConfiguringScreenDescription() {
        return "Press Enter to confirm the configuration, or press CTRL+W to cancel the configuration";
    }

    @Override
    public String fishingSwitchButtonLabel() {
        return "Enable Auto Fishing";
    }

    @Override
    public String fishingStartConfiguring() {
        return "Start Configuring";
    }

    @Override
    public String fishingSkipConfigureStep1Button() {
        return "Did not leave the fishing mode after the last configuration, skip this configuration";
    }

    @Override
    public String fishingConfigureTips1() {
        return """
            Please enter the fishing state in the game (the green light is on the screen),
            then return to this window, click "Confirm", and then switch to the game interface immediately. After waiting for a few seconds, you will enter the configuration interface.
            Inside the configuration interface, press Enter to confirm the configuration, or press CTRL+W to cancel the configuration.""";
    }

    @Override
    public String fishingConfigureTips2() {
        return """
            Please enter the biting state in the game (when a yellow position bar appears at the top),
            then return to this window, click "Confirm", and then switch to the game interface immediately. After waiting for a few seconds, you will enter the configuration interface.
            Inside the configuration interface, press Enter to confirm the configuration, or press CTRL+W to cancel the configuration.""";
    }

    @Override
    public String positionOfFishingPointTip() {
        return "The position where the green light of the fishing point is emitted (you can adjust by pressing WSAD)";
    }

    @Override
    public String positionOfCastingPointTip() {
        return "Place on the fishing button";
    }

    @Override
    public String positionOfPositionTip() {
        return "The range of the yellow slider representing the position information (it should encompass the entire black bar)";
    }

    @Override
    public String positionOfFishStaminaTip() {
        return "The range of the fish's stamina bar (the left blue arc bar)";
    }

    @Override
    public String fishingStatus() {
        return "Status: ";
    }

    @Override
    public String fishingStatusStopped() {
        return "Stopped";
    }

    @Override
    public String fishingStatusStopping() {
        return "Stopping...";
    }

    @Override
    public String fishingStatusBegin() {
        return "Start";
    }

    @Override
    public String fishingStatusFailed() {
        return "Fishing Failed";
    }

    @Override
    public String fishingStatusWaitingForCasting() {
        return "Waiting for Casting";
    }

    @Override
    public String fishingStatusWaitingForBite() {
        return "Waiting for Bite";
    }

    @Override
    public String fishingStatusManagingPos() {
        return "Fishing...";
    }

    @Override
    public String fishingStatusBeforeReeling() {
        return "Before Reeling";
    }

    @Override
    public String fishingStatusAfterReeling() {
        return "After Reeling";
    }

    @Override
    public String fishingCastKeyNotSet() {
        return "The cast key is not set";
    }

    @Override
    public String fishingOpenBrowserForTutorialFailed(String url) {
        return "Failed to open the browser, you can manually enter this URL to view the fishing tutorial: " + url + ", or you can try to paste directly, the program has tried to put the url into your clipboard";
    }

    @Override
    public String about() {
        return delegate.about();
    }

    @Override
    public String version() {
        return "Version";
    }

    @Override
    public String latestVersion() {
        return "Latest Version";
    }

    @Override
    public String latestVersionReleaseTime() {
        return "Release Time";
    }

    @Override
    public String lastSyncTime() {
        return "Last Synchronization Time";
    }

    @Override
    public String contributor() {
        return "Code Contributor for this Tool";
    }

    @Override
    public String serverListColumnNameRegion() {
        return "Region";
    }

    @Override
    public String serverListColumnNameName() {
        return "Server Name";
    }

    @Override
    public String enableHostsFileModificationForGlobalServer() {
        return "Modify hosts file to directly access international server (Admin rights needed to run this program)";
    }

    @Override
    public String failedReadingTofServerList() {
        return "Failed to Read Tower of Fantasy Server List";
    }

    @Override
    public String failedWritingHostsFile() {
        return "Failed to Write to hosts file";
    }

    @Override
    public String swapConfigFailed() {
        return "Failed to Swap between CN and International Game Configuration Files";
    }

    @Override
    public String swapConfigFailedMissingVersionInConfig() {
        return "Current Configuration File Does Not Specify Game Version (CN/International), Unable to Automatically Switch Game Configuration Files";
    }

    @Override
    public String swapConfigFailedTargetFileIsNotDir() {
        return "Target Configuration File Path for Swap Is Not a Directory";
    }

    @Override
    public String swapConfigFailedPathToMoveToIsOccupied() {
        return "Path for Placing Swapped Configuration File Is Already Occupied";
    }

    @Override
    public String swapConfigFailedCreatingSavedDirFailed() {
        return "Failed to Create Directory for Saving Configuration Files";
    }

    @Override
    public String chooseGameVersionDesc() {
        return "Because CN and International Versions of Tower of Fantasy Use the Same Configuration Files, Please Choose Current Game Version to Enable Future Automatic Switching";
    }

    @Override
    public String chooseCNVersion() {
        return "CN Version";
    }

    @Override
    public String chooseGlobalVersion() {
        return "Global Version";
    }

    @Override
    public String weaponName(String name) {
        return delegate.weaponName(name);
    }

    @Override
    public String matrixName(String name) {
        return delegate.matrixName(name);
    }

    @Override
    public String relicsName(String name) {
        return delegate.relicsName(name);
    }

    @Override
    public String simulacraName(String name) {
        return delegate.simulacraName(name);
    }

    @Override
    public String buffName(String name) {
        return delegate.buffName(name);
    }

    @Override
    public String startCoolDown() {
        return "Start";
    }

    @Override
    public String stopCoolDown() {
        return "Stop";
    }

    @Override
    public String cooldownOptionsBtn() {
        return "Options";
    }

    @Override
    public String cooldownScanDischargeDesc() {
        return "The collaborative charging detection feature reads the screen to identify the current charge value.\n" +
               "This detection only takes screenshots of a designated area on the screen (with a high screenshot frequency) and does not read game memory data.";
    }

    @Override
    public String cooldownScanDischargeCheckBox() {
        return "Enable Collaborative Charging Detection (Beta)";
    }

    @Override
    public String cooldownScanDischargeDebugCheckBox() {
        return "Enable Debug Mode (Charging Detection Results Will Be Placed in the Clipboard)";
    }

    @Override
    public String cooldownScanDischargeUseNativeCaptureCheckBox() {
        return "Use Native Screen Capture (Beta)";
    }

    @Override
    public String cooldownScanDischargeUseRoughCaptureCheckBox() {
        return "Use Non-Precise Screen Capture (Affected by High DPI)";
    }

    @Override
    public String cooldownScanDischargeResetBtn() {
        return "Reset Collaborative Charging Detection Settings";
    }

    @Override
    public String cooldownApplyDischargeForYingZhiCheckBox() {
        return "Enable 6-Star YingZhi Collaboration Count";
    }

    @Override
    public String hideWhenMouseEnterCheckBox() {
        return "Auto Hide Counter When Mouse Enters (Hold Alt Key to Enter or Click)";
    }

    @Override
    public String lockCDWindowPositionCheckBox() {
        return "Lock Indicator Window (Allows Dragging While Holding Alt Key)";
    }

    @Override
    public String onlyShowFirstLineBuffCheckBox() {
        return "Only Display the First Line of Buff Bar (Resumes Display When Holding Alt Key)";
    }

    @Override
    public String cooldownPlayAudioCheckBox() {
        return "Play Mimicry Voice";
    }

    @Override
    public String cooldownSkipAudioCollection001CheckBox() {
        return "Only Play Selected Mimicry Voices";
    }

    @Override
    public String cooldownAutoFillPianGuangLingYuSubSkillCheckbox() {
        return "Always Fill 2 Sub-skill Layers When Switching to PianGuangLingYu";
    }

    @Override
    public String cooldownAutoDischargeForYueXingChuanCheckBox() {
        return "Always Treated as Release Collaboration When Switching to YueXingChuan";
    }

    @Override
    public String cooldownAutoDischargeForJueXiangCheckBox() {
        return "Always Treated as Release Collaboration When Switching to JueXiang";
    }

    @Override
    public String cooldownRefreshBuffRegardlessOfCDForBuMieZhiYi() {
        return "Whenever BtnumieZhiYi Skill Button is Pressed, Reset the Duration of the Field Regardless of Whether in CD";
    }

    @Override
    public String cooldownAlwaysCanUseSkillOfPianZhen() {
        return "PianZhen Skill Always Treated as Usable";
    }

    @Override
    public String scanDischargeConfigureTips() {
        return """
            Please go into the game and charge to nearly full state first (above 90%)
            Then return to this window, click "OK", then immediately switch to the game interface, wait a few seconds to enter the configuration interface.
            In the configuration interface, press Enter to confirm the configuration or press CTRL + W to cancel the configuration""";
    }

    @Override
    public String positionOfDischargeTip() {
        return "Drag the selection box to any backend weapon, preferably just including its charge bar";
    }

    @Override
    public String scanDischargeScreenDescription() {
        return "Press Enter to Confirm the Configuration, or Press CTRL+W to Cancel the Configuration";
    }

    @Override
    public String failedCalculatingCriticalPoints() {
        return "Failed to calculate critical points, please reframe.\n" +
               "Please note, the charge must be almost full (above 90%)";
    }

    @Override
    public String weaponNotSelected() {
        return "Empty weapon slot exists";
    }

    @Override
    public String duplicatedWeapon() {
        return "Duplicated weapon selection";
    }

    @Override
    public String duplicatedRelics() {
        return "Duplicated relics selection";
    }

    @Override
    public String relicsChooserPlaceHolder(int index) {
        return "Relics" + (index + 1);
    }

    @Override
    public String cooldownConfigurationLabel() {
        return "Equipment Schemes";
    }

    @Override
    public String cooldownConfigurationDuplicate() {
        return "Duplicated Equipment Scheme Names";
    }

    @Override
    public String cooldownConfigurationSave() {
        return "Save";
    }

    @Override
    public String cooldownConfigurationDelete() {
        return "Delete";
    }

    @Override
    public String cooldownPauseDesc() {
        return "Pause";
    }

    @Override
    public String cooldownResumeDesc() {
        return "Resume";
    }

    @Override
    public String cooldownResetDesc() {
        return "Reset";
    }

    @Override
    public String loadingStageTitle() {
        return "Loading...";
    }

    @Override
    public String waitForStartupVideoToFinish() {
        return "Playing Startup Animation...";
    }

    @Override
    public String progressWelcomeText() {
        return "Load completed, ready to enter";
    }

    @Override
    public String cooldownTipsButton() {
        return "Explanation";
    }

    @Override
    public String cooldownTips() {
        return """
            Using this tool in the game requires administrator permissions. You can zoom in and out of the indicator window using the mouse wheel. Right-clicking on the buff indicator can switch it to the second line (or switch it back)
            This tool directly listens to keyboard and mouse events and simulates weapon switching and skill releasing based on these events.
            Therefore, this tool does not interact with the game itself and will not be detected.
            But because all events are simulated, the results given by the tool will inevitably differ from the actual situation in the game.
            For this reason, this tool has some fault tolerance design to keep as consistent with the game as possible:
            1. For all weapons, if the cooldown time calculated by the software has passed more than 90%, then it is regarded as usable even if it has not completely cooled down;
            2. For 1-star and above Glaipnir (Fenrir), pressing the skill button 5 times in a row in a short period of time will ignore the current state and directly regard it as completely cooled down and have released a main skill and a 1-star secondary skill once;
            3. For 1-star and above Flowing Spring and Heart (Unbroken Sachiko), clicking on its skill counter can increase the count, which is convenient for players to synchronize the counter after reading the bar;
            4. For 3-star and above Flame Revolver, because it is impossible to determine the burning situation, as long as you perform a dodge attack or a charge attack, it is deemed to have triggered ion burn;
            5. For the 6-star Flame Revolver, only the ion burn on its cooldown reduction effect is calculated, and the cooldown reduction interval uses 2 seconds instead of the 1.5 seconds in the game;
            6. For the Evening Prayer (Stellar Ring) that triggers the Grace Resonance, clicking the Glow counter can reset it to 3 times to facilitate the player to synchronize the counter after reading the bar;
            7. When Ling Light and Four Leaf Cross are equipped at the same time, because the feathers are very easy to obtain, they are always in the state of three feathers by default, and the ammunition consumption of Four Leaf Clover is 4 rounds each time;
            8. The cooldown reduction effect of Four Leaf Clover (Vas) has a strong correlation with the actual operation, so when using Four Leaf Clover (Vas) in actual combat, the cooldown time does not guarantee a perfect match;
            9. Since it is impossible to detect electric explosion, the 6-star effect of Super Electromagnetic Gemini is not processed;
            Even with these fault tolerance designs, there are inevitably some situations where it is inconsistent with the game, so the correct usage is: mainly based on the game, and then glance at the cooldown indicator from time to time.
            In addition, there are the following special treatments:
            1. Holding down (longer than 180 milliseconds) the weapon key to switch to other weapons is regarded as a release of a collaborative skill. Currently, [Flowing Spring and Heart, Dawn] has special processing for this behavior;
            2. For [Evening Prayer, Zero Degree Pointer, Negative Cube, Morning Star], support to cancel the release of skills through jumps or weapon switching during skill preheating;
            3. Press the Chosen Dice shortly to reset the skill cooldown of all weapons, or press long to only provide thermal core buff without refreshing skill cooldown;
            4. There are some additional configuration items for specific weapons in [Options], which can be configured as needed (corresponding configuration can only be made when the weapon is selected);
            """;
    }

    @Override
    public String cooldownTutorialLink() {
        return "Click here to view CD Indicator Tutorial Video";
    }

    @Override
    public String cooldownOpenBrowserForTutorialFailed(String url) {
        return "Failed to open browser, manually enter this URL to see CD Indicator Tutorial:" + url + ", or try to paste directly, the program has tried to put this url into your clipboard";
    }

    @Override
    public String invalidAssistantConfigFileAskForDeletion(String configFilePath) {
        return "Panthan PC Assistant configuration file exception. You can modify file " + configFilePath + " to manually fix the exception," + "\n" +
               "or you can directly delete the configuration file and perform subsequent operations with the initial configuration.";
    }

    @Override
    public String modifyInvalidAssistantConfigBtn() {
        return "Modify";
    }

    @Override
    public String deleteInvalidAssistantConfigBtn() {
        return "Delete";
    }

    @Override
    public String cancelInvalidAssistantConfigBtn() {
        return "Cancel";
    }

    @Override
    public String toolName(String name) {
        return switch (name) {
            case "world-boss-timer" -> "World Boss Timer";
            case "message-helper" -> "Chat Message Helper";
            case "lan's-brain-wash" -> "Lan's Brain Wash";
            case "multi-hotta-instance" -> "Multiple Hotta Instance";
            case "status-indicator" -> "Status Indicator";
            case "message-monitor" -> "Chat Message Monitor";
            case "patch-manager" -> "Patch Manager";
            default -> name;
        };
    }

    @Override
    public String worldBossTimerLineCol() {
        return "Line";
    }

    @Override
    public String worldBossTimerNameCol() {
        return "Boss Name";
    }

    @Override
    public String worldBossTimerLastKillCol() {
        return "Last Kill Time";
    }

    @Override
    public String worldBossTimerETACol() {
        return "Estimated Refresh Time";
    }

    @Override
    public String worldBossTimerSpawnMinutesCol() {
        return "Refresh Duration (minutes)";
    }

    @Override
    public String worldBossTimerCommentCol() {
        return "Comments";
    }

    @Override
    public String worldBossTimerLastLineCol() {
        return "Line";
    }

    @Override
    public String worldBossTimerAccountNameCol() {
        return "Account Name";
    }

    @Override
    public String worldBossTimerLastSwitchLineTsCol() {
        return "Last Switch Time";
    }

    @Override
    public String worldBossTimerAccountETACol() {
        return "Estimated Switch Time";
    }

    @Override
    public String worldBossTimerSwitchLineCDMinutes() {
        return "Switch Line CD Time (minutes)";
    }

    @Override
    public String worldBossTimerAddBtn() {
        return "Add";
    }

    @Override
    public String worldBossTimerEditBtn() {
        return "Edit";
    }

    @Override
    public String worldBossTimerSpawnBtn() {
        return "Kill";
    }

    @Override
    public String worldBossTimerDelBtn() {
        return "Delete";
    }

    @Override
    public String worldBossTimerCopyBossInfoBtn() {
        return "Copy Announcement";
    }

    @Override
    public String worldBossTimerExportBtn() {
        return "Export";
    }

    @Override
    public String worldBossTimerImportBtn() {
        return "Import";
    }

    @Override
    public String worldBossTimerIncludeBossTimerCheckBox() {
        return "Include World Boss Timer Data";
    }

    @Override
    public String worldBossTimerIncludeAccountTimerCheckBox() {
        return "Include Account Timer Data";
    }

    @Override
    public String worldBossTimerIncludeMsgTemplateCheckBox() {
        return "Include Announcement Message Template";
    }

    @Override
    public String worldBossTimerMergeImportCheckBox() {
        return "Merge Data When Importing";
    }

    @Override
    public String worldBossTimerSwitchLineBtn() {
        return "Switch Line";
    }

    @Override
    public String worldBossTimerClearBtn() {
        return "Clear";
    }

    @Override
    public String worldBossTimerOkBtn() {
        return "OK";
    }

    @Override
    public String worldBossTimerMissingLine() {
        return "Line Not Filled In";
    }

    @Override
    public String worldBossTimerMissingName() {
        return "Boss Name Not Filled In";
    }

    @Override
    public String worldBossTimerMissingLastKill() {
        return "Last Kill Time Not Filled In";
    }

    @Override
    public String worldBossTimerMissingSpawnMinutes() {
        return "Refresh Time Not Filled In";
    }

    @Override
    public String worldBossTimerMissingLastSwitchLineTs() {
        return "Last Switch Time Not Filled In";
    }

    @Override
    public String worldBossTimerMissingSwitchLineCD() {
        return "Switch Line CD Time Not Defined";
    }

    @Override
    public String worldBossTimerInvalidLine() {
        return "Line Filling Error";
    }

    @Override
    public String worldBossTimerInvalidLastKill() {
        return "Last Kill Time Filling Error";
    }

    @Override
    public String worldBossTimerInvalidSpawnMinutes() {
        return "Refresh Time Filling Error";
    }

    @Override
    public String worldBossTimerInvalidLastSwitchLineTs() {
        return "Last Switch Time Filling Error";
    }

    @Override
    public String worldBossTimerInvalidSwitchLineCD() {
        return "Switch Line CD Time Filling Error";
    }

    @Override
    public String worldBossTimerNextBossInfoDefaultTemplate() {
        return delegate.worldBossTimerNextBossInfoDefaultTemplate();
    }

    @Override
    public String worldBossTimerInvalidTemplate() {
        return "Shout template configuration error";
    }

    @Override
    public String worldBossTimerNoDataToImport() {
        return "No data available for import, please copy before clicking the import button";
    }

    @Override
    public String worldBossTimerInvalidImportingData() {
        return "Error importing data";
    }

    @Override
    public String brainWashLanStartBtn() {
        return "Start";
    }

    @Override
    public String brainWashLanStopBtn() {
        return "Stop";
    }

    @Override
    public String brainWashLanFreqSliderDesc() {
        return "Brainwashing frequency (unit: times/minute)";
    }

    @Override
    public String brainWashLanRandTimeSliderDesc() {
        return "Random time interval (unit: seconds)";
    }

    @Override
    public String selectBetaGameLocation() {
        return "Select test server path";
    }

    @Override
    public String selectOnlineGameLocation() {
        return "Select online server path";
    }

    @Override
    public String selectOnlineModGameLocation() {
        return "Select online mod server path";
    }

    @Override
    public String multiInstanceAdvBranch() {
        return "Test server branch";
    }

    @Override
    public String multiInstanceOnlineBranch() {
        return "Online server branch";
    }

    @Override
    public String multiInstanceOnlineModBranch() {
        return "Online mod server branch";
    }

    @Override
    public String multiInstanceOnlineVersion() {
        return "Online server version";
    }

    @Override
    public String multiInstanceIsHandlingAdvCheckBox() {
        return "Handle test server client traffic";
    }

    @Override
    public String multiInstanceSaveCaCert() {
        return "Get root certificate";
    }

    @Override
    public String multiInstanceTutorialLink() {
        return "Click here to view the dual-use tool tutorial video of the illusion tower";
    }

    @Override
    public String multiInstanceInvalidFieldAlert(String field) {
        return switch (field) {
            case "onlinePath" -> "Online game path is empty or invalid";
            case "advBranch" -> "Beta branch is empty or invalid";
            case "onlineBranch" -> "Online branch is empty or invalid";
            case "onlineModBranch" -> "Online mod branch is empty or invalid";
            case "onlineVersion" -> "Online version is empty or invalid (x.y.z)";
            case "betaPath&onlineModPath" -> "At least one of online mod and beta game path should be filled";
            default -> STR."\{field} is empty or invalid";
        };
    }

    @Override
    public String multiInstanceLaunchStep(String step) {
        return switch (step) {
            case "lock" -> "Function lock checking";
            case "clientVersion" -> "Setting client version";
            case "UserData" -> "Replacing user files";
            case "UserData2" -> "Replacing user files for mod launcher";
            case "ResList.xml" -> "Write resource configuration file(1) for beta launcher";
            case "ResList.xml-2" -> "Write resource configuration file(1) for mod launcher";
            case "config.xml" -> "Write resource configuration file(2) for beta launcher";
            case "config.xml-2" -> "Write resource configuration file(2) for mod launcher";
            case "Client" -> "Linking to client directory";
            case "Client2" -> "Linking to client directory for mod launcher";
            case "resolve" -> "Resolving related domain names";
            case "server" -> "Starting proxy server";
            case "hijack" -> "Hijacking DNS requests";
            case "flush-dns" -> "Flushing DNS cache";
            case "launch" -> "Launch";
            default -> step;
        };
    }

    @Override
    public String multiInstanceFailedSettingClientVersion() {
        return "Failed to set client version, please confirm whether the online service path is set correctly";
    }

    @Override
    public String multiInstanceFailedReplacingUserDataDir() {
        return "Failed to replace user files, please confirm that the Wmgp process corresponding to the dual open client has exited";
    }

    @Override
    public String multiInstanceFailedWritingResListXml() {
        return "Failed to write resource configuration file ResList.xml";
    }

    @Override
    public String multiInstanceFailedWritingConfigXml() {
        return "Failed to write resource configuration file config.xml";
    }

    @Override
    public String multiInstanceCannotMakeLink() {
        return "Failed to create link file. If the game client directory has been moved, you need to manually delete the Client directory link in the multicasting launcher directory";
    }

    @Override
    public String multiInstanceResolvingFailed() {
        return "Domain resolving failed";
    }

    @Override
    public String multiInstanceLaunchProxyServerFailed() {
        return "Failed to start proxy service, please ensure that the 443 port of this machine is not occupied";
    }

    @Override
    public String multiInstanceLaunchDNSHijackerFailed() {
        return "Failed to start dns hijacker, please check log and contact the developer";
    }

    @Override
    public String multiInstancesOpenBrowserForTutorialFailed(String url) {
        return "Failed to open the browser, you can manually enter this URL to view the multi-open tutorial: " + url + ", or try to paste directly, the program has tried to put this url into your clipboard";
    }

    @Override
    public String multiInstanceTips() {
        return "Do NOT click the upgrade button if the game prompts an upgrade.\n" +
               "Please terminate the game and re-extract the prebuilt compressed package, then re-launch the game with this tool";
    }

    @Override
    public String multiInstanceConfirmAndDisableTipsButton() {
        return "I understand, don't remind again later";
    }

    @Override
    public String multiInstanceSingleLaunchButton() {
        return "Launch";
    }

    @Override
    public String selectGameLocationDescriptionWithoutAutoSearching() {
        return "Please select the game path and manually select the path where gameLauncher.exe is located";
    }

    @Override
    public String failedSavingCaCertFile() {
        return "Failed to save CA certificate";
    }

    @Override
    public String initRobotFailed() {
        return "Failed to initialize Robot";
    }

    @Override
    public String readAssistantConfigFailed() {
        return "Failed to read assistant configuration file";
    }

    @Override
    public String readInputConfigFailed() {
        return "Failed to read input configuration";
    }

    @Override
    public String writeAssistantConfigFailed() {
        return "Failed to write assistant configuration file";
    }

    @Override
    public String confirm() {
        return "Confirm";
    }

    @Override
    public String messageHelperDesc() {
        return """
            For some Chinese input methods, when the number key or space key is pressed, the Illusion Tower client may lose the cursor and cannot input Chinese normally.
            This small tool is designed to deal with such scenarios.
            Operation instructions:
              1. When Enter is pressed in the game, this tool will automatically move to the front and flash to prompt
              2. Enter content in the input box of the tool and press Enter. The content will be transferred to the clipboard and can be pasted into the game.
              3. Press the up/down arrow key in the input box to scroll through the last 20 messages.
              4. Hold down the part outside the input box of this tool to drag and move the window
              5. Using Ctrl-Z can undo the last modification, note that it can only undo once
              6. Click the button in the interface to automatically fill in the color code, please pay attention to the overall length limit
            """;
    }

    @Override
    public String messageHelperColorButton(String name) {
        return switch (name) {
            case "red" -> "Red text";
            case "blue" -> "Blue text";
            case "white" -> "White text";
            case "gold" -> "Yellow text";
            case "purple" -> "Purple text";
            case "green" -> "Green text";
            case "green_bold" -> "Bold green text";
            default -> name;
        };
    }

    @Override
    public String messageHelperItemButton() {
        return "Show item";
    }

    @Override
    public String scrollLogCheckBoxDesc() {
        return "Scroll to display latest log";
    }

    @Override
    public String gplAlert(String url) {
        return "This software is open sourced through the GPLv2 protocol, and according to the agreement, corresponding source code is required to be provided when distributed.\n" +
               "The source code repository of this software is at:" + url + ". Detailed information can be found on the [About] page.";
    }

    @Override
    public String confirmAndDisableGPLAlert() {
        return "I understand, don't remind again when starting up later";
    }

    @Override
    public String authorGameAccount() {
        return "Game account";
    }

    @Override
    public String authorContribution() {
        return "Contribution content";
    }

    @Override
    public String statusIndicatorDesc() {
        return "The status indicator represents the current running status of the Illusion Tower PC assistant, including the enabled status of various tools, and macros.";
    }

    @Override
    public String statusIndicatorTitle() {
        return "Status indicator";
    }

    @Override
    public String statusComponentModule() {
        return "Module";
    }

    @Override
    public String statusComponentTool() {
        return "Tool";
    }

    @Override
    public String statusComponentMacro() {
        return "Macro";
    }

    @Override
    public String yueXingChuanJuShuiSkill() {
        return "Gathering water";
    }

    @Override
    public String yueXingChuanYongJuanSkill() {
        return "Surge roll";
    }

    @Override
    public String yueXingChuanTaoYaSkill() {
        return "Wave pressure";
    }

    @Override
    public String yueXingChuanWoXuanSkill() {
        return "Vortex";
    }

    @Override
    public String yueXingChuanYuGuSkill() {
        return "Water bondage";
    }

    @Override
    public String yueXingChuanZiQuanSkill() {
        return "Gushing spring";
    }

    @Override
    public String yueXingChuanSanLiuSkillCoolDownDesc(String name) {
        return name + " cooldown time";
    }

    @Override
    public String yueXingChuanSanLiuSkillBuffDesc(String name) {
        return name + " effect duration";
    }

    @Override
    public String resetSceneDesc() {
        return "Reset function can quickly clear the files left in operation, you can use it when the assistant runs abnormally.";
    }

    @Override
    public String resetSceneResetConfigButton() {
        return "Reset configuration file";
    }

    @Override
    public String resetSceneResetConfigSucceeded() {
        return "Reset successful, assistant will shut down, please manually reopen it";
    }

    @Override
    public String messageMonitorNicChooserTitle() {
        return "Select the network card to monitor";
    }

    @Override
    public String messageMonitorServerHostTitle() {
        return "Enter the IP address of the Illusion Tower server";
    }

    @Override
    public String messageMonitorWordsTitle() {
        return "Enter the words to monitor, separated by English commas";
    }

    @Override
    public String messageMonitorStartBtn() {
        return "Start";
    }

    @Override
    public String messageMonitorStopBtn() {
        return "Stop";
    }

    @Override
    public String messageMonitorServerHostDefaultValue() {
        return "39.96.163.203";
    }

    @Override
    public String messageMonitorWordsDefaultValue() {
        return "Xuan Ya, Ya Ya, Duck Neck, Crow";
    }

    @Override
    public String messageMonitorAlreadyStartedAlert() {
        return "Message monitoring has started";
    }

    @Override
    public String messageMonitorNoNetifSelectedAlert() {
        return "No network card selected for monitoring";
    }

    @Override
    public String messageMonitorInvalidServerHostAlert(String str) {
        return "Server address is not a correct IP address: " + str;
    }

    @Override
    public String messageMonitorEmptyWordsListAlert() {
        return "Monitoring text is empty";
    }

    @Override
    public String messageMonitorNotificationTitle() {
        return "Illusion Tower PC Assistant - Chat Message Monitoring";
    }

    @Override
    public String messageMonitorCapFailedAlert() {
        return "Monitoring exception!";
    }

    @Override
    public String messageMonitorChannel(ChatChannel channel) {
        if (channel == null) {
            return "All channels";
        }
        return switch (channel) {
            case WORLD -> "World";
            case GUILD -> "Guild";
            case TEAM -> "Team";
            case COOP -> "Cooperative";
        };
    }

    @Override
    public String clearHostsFailed() {
        return "Clearing hosts file failed, you need to manually clear it, or repeat the exit operation for re-clear, otherwise it will cause network abnormality";
    }

    @Override
    public String loadingFailedErrorMessage(LoadingItem failedItem) {
        return "Loading failed. Failure item is: " + failedItem.name;
    }

    @Override
    public String skipAnimation() {
        return "Skip";
    }

    @Override
    public String newCriticalVersionAvailable(String ver) {
        return "New critical updates available: " + ver + ", you can get the latest version from github.com/wkgcass/hotta-pc-assistant";
    }

    @Override
    public String patchManagerEditBtn() {
        return "Edit";
    }

    @Override
    public String patchManagerRemoveBtn() {
        return "Remove";
    }

    @Override
    public String patchManagerReloadBtn() {
        return "Reload";
    }

    @Override
    public String patchManagerOpenFolderBtn() {
        return "Open Folder";
    }

    @Override
    public String patchManagerEnabledCol() {
        return "enabled";
    }

    @Override
    public String patchManagerNameCol() {
        return "name";
    }

    @Override
    public String patchManagerCNCol() {
        return "cn";
    }

    @Override
    public String patchManagerGlobalCol() {
        return "global";
    }

    @Override
    public String patchManagerDescCol() {
        return "description";
    }

    @Override
    public String patchManagerLoadAfterCol() {
        return "load after";
    }

    @Override
    public String patchManagerDependsOnCol() {
        return "depends on";
    }

    @Override
    public String patchManagerOkBtn() {
        return "Ok";
    }

    @Override
    public String patchManagerDepNotExist(String colName, String parentItemName) {
        return "Missing item detected for `" + colName + "`: " + parentItemName;
    }

    @Override
    public String patchManagerDepCircular(String colName, ArrayList<String> newPath) {
        return "Circular dependency detected for `" + colName + "`: " + newPath;
    }

    @Override
    public String patchManagerAlertInvalidConfigTitle() {
        return "Invalid patch config";
    }

    @Override
    public String patchManagerAlertInvalidConfigContent(String name) {
        return "Unable to parse patch config: " + name + ", will use empty config instead";
    }

    @Override
    public String patchManagerAlertFailedToWriteConfigTitle() {
        return "Failed to persist config";
    }

    @Override
    public String patchManagerAlertFailedToWriteConfigContent() {
        return "Failed to persist config, please check the logs for more info";
    }

    @Override
    public String patchManagerAlertHasDependedCannotDelete(String name, Set<String> set) {
        return "Patches " + set + " depend on " + name;
    }

    @Override
    public String patchManagerConfirmRemove(String name) {
        return "Are you sure to delete " + name + "? The file on disk will be removed!";
    }

    @Override
    public String applyPatchLoadingStageTitle() {
        return "Applying patches ...";
    }

    @Override
    public String applyPatchLoadingPreparePatchDirectory() {
        return "Prepare patch directory";
    }

    @Override
    public String applyPatchLoadingPrepareSigFile() {
        return "Prepare sig file";
    }
}
