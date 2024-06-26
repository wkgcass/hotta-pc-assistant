package net.cassite.hottapcassistant.i18n;

import io.vproxy.base.util.LogType;
import io.vproxy.base.util.Logger;
import io.vproxy.vfx.ui.loading.LoadingItem;
import net.cassite.hottapcassistant.config.AssistantConfig;
import net.cassite.hottapcassistant.entity.Assistant;
import net.cassite.tofpcap.messages.ChatChannel;

import java.util.ArrayList;
import java.util.Set;

public abstract class I18n implements io.vproxy.vfx.manager.internal_i18n.InternalI18n, net.cassite.xboxrelay.ui.I18n {
    private static volatile I18n impl;

    public static I18n get() {
        if (impl == null) {
            synchronized (I18n.class) {
                if (impl == null) {
                    Assistant ass;
                    try {
                        ass = AssistantConfig.readAssistant();
                    } catch (Exception e) {
                        Logger.error(LogType.FILE_ERROR, "failed to load assistant config", e);
                        ass = null;
                    }
                    I18nType type = I18nType.ZhCn;
                    if (ass != null && ass.i18n != null) {
                        type = ass.i18n;
                    }
                    if (type == I18nType.EnUs) {
                        impl = new EnUs();
                    } else {
                        impl = new ZhCn();
                    }
                }
            }
        }
        return impl;
    }

    protected I18n() {
    }

    abstract public String id();

    abstract public String loadingFontFailed(String name);

    abstract public String titleMainScreen();

    abstract public String titleMainScreenDevVersion();

    abstract public String toolNameWelcome();

    abstract public String toolNameGameSettings();

    abstract public String toolNameInputSettings();

    abstract public String toolNameMacro();

    abstract public String toolNameFishing();

    abstract public String toolNameCoolDown();

    abstract public String toolNameToolBox();

    abstract public String toolNameXBox();

    abstract public String toolNameAbout();

    public abstract String toolNameLog();

    abstract public String toolNameReset();

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

    public abstract String alertChangeSavedDirectory();

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

    public abstract String macroTipsButton();

    public abstract String editMacro();

    public abstract String reloadMacro();

    public abstract String macroTips();

    public abstract String aboutEmptyTableOrMissingFields();

    public abstract String detailAboutEmptyTableOrMissingFields();

    public abstract String openFileFailed();

    public abstract String applyPatchFailed();

    public abstract String launchGameFailed();

    public abstract String macroSwitchButtonLabel();

    public abstract String rememberMousePositionButtonLabel();

    public abstract String macroAlertLabel();

    public abstract String macroColumnNameEnable();

    public abstract String macroColumnNameName();

    public abstract String macroColumnNameType();

    public abstract String macroColumnNameStatus();

    public abstract String macroTypeNormal();

    public abstract String macroTypeInfiniteLoop();

    public abstract String macroTypeFiniteLoop();

    public abstract String macroStatusRunning();

    public abstract String macroStatusStopped();

    public abstract String macroStatusStopping();

    public abstract String knowConsequencePrompt();

    public abstract String fishingStartButton();

    public abstract String fishingStopKey();

    public abstract String fishingLeftKey();

    public abstract String fishingRightKey();

    public abstract String fishingCastKey();

    public abstract String fishingSkipFishingPointCheckBox();

    public abstract String fishingUseCastKeyCheckBox();

    public abstract String fishingDebugCheckBox();

    public abstract String resetFishing();

    public abstract String configureFishing();

    public abstract String configureFishingHelpMsg();

    public abstract String fishTutorialLinkDesc();

    public abstract String fishingConfiguringScreenDescription();

    public abstract String fishingSwitchButtonLabel();

    public abstract String fishingStartConfiguring();

    public abstract String fishingSkipConfigureStep1Button();

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

    public abstract String fishingCastKeyNotSet();

    public abstract String fishingOpenBrowserForTutorialFailed(String url);

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

    public abstract String cooldownOptionsBtn();

    public abstract String cooldownScanDischargeDesc();

    public abstract String cooldownScanDischargeCheckBox();

    public abstract String cooldownScanDischargeDebugCheckBox();

    public abstract String cooldownScanDischargeUseNativeCaptureCheckBox();

    public abstract String cooldownScanDischargeUseRoughCaptureCheckBox();

    public abstract String cooldownScanDischargeResetBtn();

    public abstract String cooldownApplyDischargeForYingZhiCheckBox();

    public abstract String hideWhenMouseEnterCheckBox();

    public abstract String lockCDWindowPositionCheckBox();

    public abstract String onlyShowFirstLineBuffCheckBox();

    public abstract String cooldownPlayAudioCheckBox();

    public abstract String cooldownSkipAudioCollection001CheckBox();

    public abstract String cooldownAutoFillPianGuangLingYuSubSkillCheckbox();

    public abstract String cooldownAutoDischargeForYueXingChuanCheckBox();

    public abstract String cooldownAutoDischargeForJueXiangCheckBox();

    public abstract String cooldownRefreshBuffRegardlessOfCDForBuMieZhiYi();

    public abstract String cooldownAlwaysCanUseSkillOfPianZhen();

    // TODO custom-weapon-option: totally 9 steps to define a custom weapon option, search for 'custom-weapon-option' globally to see all these steps
    // TODO custom-weapon-option: 1 steps in this file
    // TODO custom-weapon-option: step [6], define method(s) for displaying description for the option

    public abstract String scanDischargeConfigureTips();

    public abstract String positionOfDischargeTip();

    public abstract String scanDischargeScreenDescription();

    public abstract String failedCalculatingCriticalPoints();

    public abstract String weaponNotSelected();

    public abstract String duplicatedWeapon();

    public abstract String duplicatedRelics();

    public abstract String relicsChooserPlaceHolder(int index);

    public abstract String cooldownConfigurationLabel();

    public abstract String cooldownConfigurationDuplicate();

    public abstract String cooldownConfigurationSave();

    public abstract String cooldownConfigurationDelete();

    public abstract String cooldownPauseDesc();

    public abstract String cooldownResumeDesc();

    public abstract String cooldownResetDesc();

    public abstract String loadingStageTitle();

    public abstract String waitForStartupVideoToFinish();

    public abstract String progressWelcomeText();

    public abstract String cooldownTipsButton();

    public abstract String cooldownTips();

    public abstract String cooldownTutorialLink();

    public abstract String cooldownOpenBrowserForTutorialFailed(String url);

    public abstract String invalidAssistantConfigFileAskForDeletion(String configFilePath);

    public abstract String modifyInvalidAssistantConfigBtn();

    public abstract String deleteInvalidAssistantConfigBtn();

    public abstract String cancelInvalidAssistantConfigBtn();

    public abstract String toolName(String name);

    public abstract String worldBossTimerLineCol();

    public abstract String worldBossTimerNameCol();

    public abstract String worldBossTimerLastKillCol();

    public abstract String worldBossTimerETACol();

    public abstract String worldBossTimerSpawnMinutesCol();

    public abstract String worldBossTimerCommentCol();

    public abstract String worldBossTimerLastLineCol();

    public abstract String worldBossTimerAccountNameCol();

    public abstract String worldBossTimerLastSwitchLineTsCol();

    public abstract String worldBossTimerAccountETACol();

    public abstract String worldBossTimerSwitchLineCDMinutes();

    public abstract String worldBossTimerAddBtn();

    public abstract String worldBossTimerEditBtn();

    public abstract String worldBossTimerSpawnBtn();

    public abstract String worldBossTimerDelBtn();

    public abstract String worldBossTimerClearBtn();

    public abstract String worldBossTimerCopyBossInfoBtn();

    public abstract String worldBossTimerExportBtn();

    public abstract String worldBossTimerImportBtn();

    public abstract String worldBossTimerIncludeBossTimerCheckBox();

    public abstract String worldBossTimerIncludeAccountTimerCheckBox();

    public abstract String worldBossTimerIncludeMsgTemplateCheckBox();

    public abstract String worldBossTimerMergeImportCheckBox();

    public abstract String worldBossTimerSwitchLineBtn();

    public abstract String worldBossTimerOkBtn();

    public abstract String worldBossTimerMissingLine();

    public abstract String worldBossTimerMissingName();

    public abstract String worldBossTimerMissingLastKill();

    public abstract String worldBossTimerMissingSpawnMinutes();

    public abstract String worldBossTimerMissingLastSwitchLineTs();

    public abstract String worldBossTimerMissingSwitchLineCD();

    public abstract String worldBossTimerInvalidLine();

    public abstract String worldBossTimerInvalidLastKill();

    public abstract String worldBossTimerInvalidSpawnMinutes();

    public abstract String worldBossTimerInvalidLastSwitchLineTs();

    public abstract String worldBossTimerInvalidSwitchLineCD();

    public abstract String worldBossTimerNextBossInfoDefaultTemplate();

    public abstract String worldBossTimerInvalidTemplate();

    public abstract String worldBossTimerNoDataToImport();

    public abstract String worldBossTimerInvalidImportingData();

    public abstract String brainWashLanStartBtn();

    public abstract String brainWashLanStopBtn();

    public abstract String brainWashLanFreqSliderDesc();

    public abstract String brainWashLanRandTimeSliderDesc();

    public abstract String selectBetaGameLocation();

    public abstract String selectOnlineGameLocation();

    public abstract String selectOnlineModGameLocation();

    public abstract String multiInstanceAdvBranch();

    public abstract String multiInstanceOnlineBranch();

    public abstract String multiInstanceOnlineModBranch();

    public abstract String multiInstanceOnlineVersion();

    public abstract String multiInstanceIsHandlingAdvCheckBox();

    public abstract String multiInstanceSaveCaCert();

    public abstract String multiInstanceTutorialLink();

    public abstract String multiInstanceInvalidFieldAlert(String field);

    public abstract String multiInstanceLaunchStep(String step);

    public abstract String multiInstanceFailedSettingClientVersion();

    public abstract String multiInstanceFailedReplacingUserDataDir();

    public abstract String multiInstanceFailedWritingResListXml();

    public abstract String multiInstanceFailedWritingConfigXml();

    public abstract String multiInstanceCannotMakeLink();

    public abstract String multiInstanceResolvingFailed();

    public abstract String multiInstanceLaunchProxyServerFailed();

    public abstract String multiInstanceLaunchDNSHijackerFailed();

    public abstract String multiInstancesOpenBrowserForTutorialFailed(String url);

    public abstract String multiInstanceTips();

    public abstract String multiInstanceConfirmAndDisableTipsButton();

    public abstract String multiInstanceSingleLaunchButton();

    public abstract String selectGameLocationDescriptionWithoutAutoSearching();

    public abstract String failedSavingCaCertFile();

    public abstract String initRobotFailed();

    public abstract String readAssistantConfigFailed();

    public abstract String readInputConfigFailed();

    public abstract String writeAssistantConfigFailed();

    public abstract String confirm();

    public abstract String messageHelperDesc();

    public abstract String messageHelperColorButton(String name);

    public abstract String messageHelperItemButton();

    public abstract String scrollLogCheckBoxDesc();

    public abstract String gplAlert(String url);

    public abstract String confirmAndDisableGPLAlert();

    public String authorBilibili() {
        return "bilibili";
    }

    public abstract String authorGameAccount();

    public abstract String authorContribution();

    public abstract String statusIndicatorDesc();

    public abstract String statusIndicatorTitle();

    public abstract String statusComponentModule();

    public abstract String statusComponentTool();

    public abstract String statusComponentMacro();

    public abstract String yueXingChuanJuShuiSkill();

    public abstract String yueXingChuanYongJuanSkill();

    public abstract String yueXingChuanTaoYaSkill();

    public abstract String yueXingChuanWoXuanSkill();

    public abstract String yueXingChuanYuGuSkill();

    public abstract String yueXingChuanZiQuanSkill();

    public abstract String yueXingChuanSanLiuSkillCoolDownDesc(String name);

    public abstract String yueXingChuanSanLiuSkillBuffDesc(String name);

    public abstract String resetSceneDesc();

    public abstract String resetSceneResetConfigButton();

    public abstract String resetSceneResetConfigSucceeded();

    public abstract String messageMonitorNicChooserTitle();

    public abstract String messageMonitorServerHostTitle();

    public abstract String messageMonitorWordsTitle();

    public abstract String messageMonitorStartBtn();

    public abstract String messageMonitorStopBtn();

    public abstract String messageMonitorServerHostDefaultValue();

    public abstract String messageMonitorWordsDefaultValue();

    public abstract String messageMonitorAlreadyStartedAlert();

    public abstract String messageMonitorNoNetifSelectedAlert();

    public abstract String messageMonitorInvalidServerHostAlert(String str);

    public abstract String messageMonitorEmptyWordsListAlert();

    public abstract String messageMonitorNotificationTitle();

    public abstract String messageMonitorCapFailedAlert();

    public abstract String messageMonitorChannel(ChatChannel channel);

    public abstract String clearHostsFailed();

    public abstract String loadingFailedErrorMessage(LoadingItem failedItem);

    public abstract String skipAnimation();

    public abstract String newCriticalVersionAvailable(String ver);

    public abstract String patchManagerEditBtn();

    public abstract String patchManagerRemoveBtn();

    public abstract String patchManagerReloadBtn();

    public abstract String patchManagerOpenFolderBtn();

    public abstract String patchManagerEnabledCol();

    public abstract String patchManagerNameCol();

    public abstract String patchManagerCNCol();

    public abstract String patchManagerGlobalCol();

    public abstract String patchManagerDescCol();

    public abstract String patchManagerLoadAfterCol();

    public abstract String patchManagerDependsOnCol();

    public abstract String patchManagerOkBtn();

    public abstract String patchManagerDepNotExist(String colName, String parentItemName);

    public abstract String patchManagerDepCircular(String colName, ArrayList<String> newPath);

    public abstract String patchManagerAlertInvalidConfigTitle();

    public abstract String patchManagerAlertInvalidConfigContent(String name);

    public abstract String patchManagerAlertFailedToWriteConfigTitle();

    public abstract String patchManagerAlertFailedToWriteConfigContent();

    public abstract String patchManagerAlertHasDependedCannotDelete(String name, Set<String> set);

    public abstract String patchManagerConfirmRemove(String name);

    public abstract String applyPatchLoadingStageTitle();

    public abstract String applyPatchLoadingPreparePatchDirectory();

    public abstract String applyPatchLoadingPrepareSigFile();
}
