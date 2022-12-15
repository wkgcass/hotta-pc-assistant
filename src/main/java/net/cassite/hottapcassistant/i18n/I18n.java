package net.cassite.hottapcassistant.i18n;

public abstract class I18n {
    private static volatile I18n impl;

    public static I18n get() {
        if (impl == null) {
            synchronized (I18n.class) {
                if (impl == null) {
                    impl = new ZhCn();
                }
            }
        }
        return impl;
    }

    protected I18n() {
    }

    abstract public String id();

    abstract public String levelInformation();

    abstract public String levelError();

    abstract public String levelWarning();

    abstract public String loadingFontFailed(String name);

    abstract public String titleMainScreen();

    abstract public String titleMainScreenDevVersion();

    abstract public String toolNameWelcome();

    abstract public String toolNameGameSettings();

    abstract public String toolNameInputSettings();

    abstract public String toolNameMacro();

    abstract public String toolNameFishing();

    abstract public String toolNameCoolDown();

    abstract public String toolNameAbout();

    abstract public String toolIsLocked(String name);

    abstract public String selectGameLocation();

    abstract public String selectGlobalServerGameLocation();

    abstract public String selectButton();

    abstract public String autoSearchButton();

    abstract public String autoSearchFailed();

    abstract public String selectGameLocationDescription();

    abstract public String selectGlobalServerGameLocationDescription();

    abstract public String chosenWrongGameFile();

    abstract public String chosenWrongGlobalServerGameFile();

    abstract public String chosenWrongGlobalServerGameFileNoParentDir();

    public abstract String selectSavedLocationDescription();

    public abstract String selectSavedLocation();

    public abstract String chosenWrongSavedDirectory();

    abstract public String hotkeyColumnNameAction();

    public String hotkeyColumnNameCtrl() {
        return "ctrl";
    }

    public String hotkeyColumnNameAlt() {
        return "alt";
    }

    public String hotkeyColumnNameShift() {
        return "shift";
    }

    abstract public String hotkeyColumnNameKey();

    abstract public String hotkeyColumnNameScale();

    abstract public String leftMouseButton();

    abstract public String rightMouseButton();

    abstract public String keyChooserDesc();

    abstract public String keyChooserDescWithoutMouse();

    abstract public String unsupportedKeyErrorMessage();

    public abstract String notFloatingPointValue();

    public String inputActionMapping(String action) {
        return action;
    }

    public String configNameMapping(String name) {
        return name;
    }

    abstract public String applyButton();

    abstract public String resetButton();

    abstract public String exitCheckMessage();

    abstract public String gamePathNotSet();

    abstract public String savedPathNotSet();

    abstract public String gamePathIsNotDirectory();

    abstract public String savedPathIsNotDirectory();

    abstract public String openBrowserForDownloadingFailed(String url);

    abstract public String invalidResolutionValue();

    abstract public String invalidResolutionIntegerValue();

    abstract public String invalidNumberValue();

    abstract public String fightRangeOutOfBounds(double min, double max);

    public abstract String invalidConfigInFile(String key, String value);

    abstract public String discardChangesConfirm();

    public abstract String settingColumnNameName();

    public abstract String settingColumnNameValue();

    public abstract String openGameUserSettingsIni();

    public abstract String openInputIni();

    public abstract String editMacro();

    public abstract String reloadMacro();

    public abstract String aboutEmptyTableOrMissingFields();

    public abstract String detailAboutEmptyTableOrMissingFields();

    public abstract String openFileFailed();

    public abstract String launchGameFailed();

    public abstract String macroSwitchButtonLabel();

    public abstract String rememberMousePositionButtonLabel();

    public abstract String macroAlertLabel();

    public abstract String macroColumnNameEnable();

    public abstract String macroColumnNameName();

    public abstract String knowConsequencePrompt();

    public abstract String fishingStartKey();

    public abstract String fishingStopKey();

    public abstract String fishingLeftKey();

    public abstract String fishingRightKey();

    public abstract String resetFishing();

    public abstract String configureFishing();

    public abstract String configureFishingOnlyStep1();

    public abstract String configureFishingHelpMsg();

    public abstract String fishingConfiguringScreenDescription();

    public abstract String fishingSwitchButtonLabel();

    public abstract String fishingConfigureTips1();

    public abstract String fishingConfigureTips2();

    public abstract String positionOfFishingPointTip();

    public abstract String positionOfCastingPointTip();

    public abstract String positionOfPositionTip();

    public abstract String positionOfFishStaminaTip();

    public abstract String fishingStatus();

    public abstract String fishingStatusStopped();

    public abstract String fishingStatusStopping();

    public abstract String fishingStatusBegin();

    public abstract String fishingStatusFailed();

    public abstract String fishingStatusWaitingForCasting();

    public abstract String fishingStatusWaitingForBite();

    public abstract String fishingStatusManagingPos();

    public abstract String fishingStatusBeforeReeling();

    public abstract String fishingStatusAfterReeling();

    public abstract String about();

    public abstract String version();

    public abstract String latestVersion();

    public abstract String latestVersionReleaseTime();

    public abstract String lastSyncTime();

    public abstract String contributor();

    public abstract String serverListColumnNameRegion();

    public abstract String serverListColumnNameName();

    public abstract String enableHostsFileModificationForGlobalServer();

    public abstract String failedReadingTofServerList();

    public abstract String failedWritingHostsFile();

    public abstract String swapConfigFailed();

    public abstract String swapConfigFailedMissingVersionInConfig();

    public abstract String swapConfigFailedTargetFileIsNotDir();

    public abstract String swapConfigFailedPathToMoveToIsOccupied();

    public abstract String swapConfigFailedCreatingSavedDirFailed();

    public abstract String chooseGameVersionDesc();

    public abstract String chooseCNVersion();

    public abstract String chooseGlobalVersion();

    public abstract String weaponName(String name);

    public abstract String matrixName(String name);

    public abstract String relicsName(String name);

    public abstract String simulacraName(String name);

    public abstract String buffName(String name);

    public abstract String startCoolDown();

    public abstract String stopCoolDown();

    public abstract String weaponNotSelected();

    public abstract String duplicatedWeapon();

    public abstract String duplicatedRelics();

    public abstract String relicsChooserPlaceHolder(int index);

    public abstract String loadingStageTitle();

    public abstract String hintPressAlt();

    public abstract String cooldownTipsButton();

    public abstract String cooldownTips();
}
