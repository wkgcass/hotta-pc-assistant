package net.cassite.hottapcassistant.i18n;

public class ZhCn extends I18n {
    @Override
    public String id() {
        return "zhcn";
    }

    @Override
    public String levelInformation() {
        return "信息";
    }

    @Override
    public String levelError() {
        return "错误";
    }

    @Override
    public String levelWarning() {
        return "警告";
    }

    @Override
    public String loadingFontFailed() {
        return "加载字体失败";
    }

    @Override
    public String titleMainScreen() {
        return "幻塔PC助手";
    }

    @Override
    public String titleMainScreenDevVersion() {
        return "开发版";
    }

    @Override
    public String toolNameGameSettings() {
        return "游戏设置";
    }

    @Override
    public String toolNameInputSettings() {
        return "输入设置";
    }

    @Override
    public String toolNameMacro() {
        return "按键宏";
    }

    @Override
    public String toolNameFishing() {
        return "自动钓鱼";
    }

    @Override
    public String toolNameAbout() {
        return "关于";
    }

    @Override
    public String toolNameWelcome() {
        return "欢迎页";
    }

    @Override
    public String selectGameLocation() {
        return "选择游戏路径";
    }

    @Override
    public String selectGlobalServerGameLocation() {
        return "选择国际服游戏路径";
    }

    @Override
    public String selectButton() {
        return "选择";
    }

    @Override
    public String autoSearchButton() {
        return "自动查找";
    }

    @Override
    public String autoSearchFailed() {
        return "自动查找失败，请手动选择路径";
    }

    @Override
    public String selectGameLocationDescription() {
        return "请选择游戏路径，可以手动选择gameLauncher.exe所在路径，也可以点击\"自动查找\"";
    }

    @Override
    public String selectGlobalServerGameLocationDescription() {
        return "请选择国际服游戏路径，可以手动选择tof_launcher.exe所在路径，也可以点击\"自动查找\"";
    }

    @Override
    public String chosenWrongGameFile() {
        return "选择文件错误，请选择gameLauncher.exe";
    }

    @Override
    public String chosenWrongGlobalServerGameFile() {
        return "选择文件错误，请选择tof_launcher.exe";
    }

    @Override
    public String chosenWrongGlobalServerGameFileNoParentDir() {
        return "选择文件错误，没有找到父级目录";
    }

    @Override
    public String selectSavedLocationDescription() {
        return "请选择游戏配置文件路径，可以手动选择（通常为%HOMEPATH%\\AppData\\Local\\Hotta\\Saved\\），也可以点击\"自动查找\"";
    }

    @Override
    public String selectSavedLocation() {
        return "选择游戏配置文件路径";
    }

    @Override
    public String chosenWrongSavedDirectory() {
        return "选择路径错误，请选择文件夹";
    }

    @Override
    public String hotkeyColumnNameAction() {
        return "执行动作";
    }

    @Override
    public String hotkeyColumnNameKey() {
        return "按键";
    }

    @Override
    public String hotkeyColumnNameScale() {
        return "速率";
    }

    @Override
    public String leftMouseButton() {
        return "鼠标左键";
    }

    @Override
    public String rightMouseButton() {
        return "鼠标右键";
    }

    @Override
    public String keyChooserDesc() {
        return "请按下你想配置的键盘按键，或者点击使用鼠标左键/右键按钮";
    }

    @Override
    public String keyChooserDescWithoutMouse() {
        return "请按下你想配置的键盘按键";
    }

    @Override
    public String unsupportedKeyErrorMessage() {
        return "暂不支持该键位";
    }

    @Override
    public String notFloatingPointValue() {
        return "输入的值不是浮点数";
    }

    @Override
    public String inputActionMapping(String action) {
        return switch (action) {
            case "Artifact_1" -> "源器1";
            case "Artifact_2" -> "源器2";
            case "Artifact_1_BreakFate" -> "命运突围-源器1";
            case "Artifact_2_BreakFate" -> "命运突围-源器2";
            case "Artifact_gousuo_BreakFate" -> "命运突围-钩索";
            case "ChangeArtifact0" -> "切换源器";
            case "ChangeArtifact0_BreakFate" -> "命运突围-切换源器";
            case "ChangeWeapon0" -> "武器1";
            case "ChangeWeapon0_BreakFate" -> "命运突围-武器1";
            case "ChangeWeapon1" -> "武器2";
            case "ChangeWeapon1_BreakFate" -> "命运突围-武器2";
            case "ChangeWeapon2" -> "武器3";
            case "ChangeWeapon2_BreakFate" -> "命运突围-武器3";
            case "Chat" -> "打开聊天窗口";
            case "Crouch" -> "下蹲";
            case "Crouch_BreakFate" -> "命运突围-下蹲";
            case "Evade" -> "闪避（鼠标）";
            case "Evade_Key" -> "闪避";
            case "Evade_Key_BreakFate" -> "命运突围-闪避";
            case "Interaction" -> "交互";
            case "Interaction_BreakFate" -> "命运突围-交互";
            case "Introduce" -> "介绍/帮助";
            case "Introduce_BreakFate" -> "命运突围-介绍/帮助";
            case "Jump" -> "跳跃";
            case "Jump_BreakFate" -> "命运突围-跳跃";
            case "Map" -> "地图";
            case "Map_BreakFate" -> "命运突围-地图";
            case "Melee" -> "攻击（鼠标）";
            case "Melee_Key" -> "攻击（键盘）";
            case "Menu_1" -> "菜单1";
            case "Menu_2" -> "菜单2";
            case "Menu_3" -> "菜单3";
            case "Menu_4" -> "菜单4";
            case "Mount" -> "载具";
            case "Mount_BreakFate" -> "命运突围-载具";
            case "sign_BreakFate" -> "命运突围-信号";
            case "Supply" -> "使用食物";
            case "Supply1_BreakFate" -> "命运突围-血药";
            case "Supply2_BreakFate" -> "命运突围-盾药";
            case "SwitchMouse" -> "解除鼠标锁定";
            case "SwitchTarget" -> "切换目标";
            case "SwitchTarget_BreakFate" -> "命运突围-切换目标";
            case "Track" -> "任务追踪";
            case "UI_Avatar" -> "角色界面";
            case "UI_Avatar_BreakFate" -> "命运突围-角色界面";
            case "UI_Bag" -> "背包界面";
            case "UI_Bag_BreakFate" -> "命运突围-背包界面";
            case "UI_SelfMenu" -> "菜单";
            case "UI_SelfMenu_BreakFate" -> "命运突围-菜单";
            case "UI_Weapon" -> "武器界面";
            case "UI_Weapon_BreakFate" -> "命运突围-武器界面";
            case "WeaponSkill" -> "武器技能";
            case "WeaponSkill_BreakFate" -> "命运突围-武器技能";
            case "Vines" -> "攀爬";
            case "LookUpRate" -> "视角[上]下";
            case "MoveForward" -> "[前]后移动";
            case "MoveForward_BreakFate" -> "命运突围-[前]后移动";
            case "MoveRight" -> "左[右]移动";
            case "MoveRight_BreakFate" -> "命运突围-左[右]移动";
            case "TurnRate" -> "视角左[右]";
            default -> super.inputActionMapping(action);
        };
    }

    @Override
    public String configNameMapping(String name) {
        return switch (name) {
            case "dx11" -> "使用DX11";
            case "Resolution_0" -> "分辨率（-1x-1表示全屏）";
            case "bAutoCombatDiet" -> "自动战斗-使用食物";
            case "AutoCombatDietHpPercent" -> "自动战斗-使用食物时的血量百分比";
            case "bAutoCombatArtifactSkill" -> "自动战斗-使用源器";
            case "bAutoCombatChangeWeaponSkill" -> "自动战斗-切换武器";
            case "fFightCameraDistance" -> "战斗时镜头距离";
            case "MaxVisibilityPlayer" -> "可见玩家数量";
            case "FrameRateLimit" -> "帧率限制";
            default -> super.configNameMapping(name);
        };
    }

    @Override
    public String applyButton() {
        return "应用";
    }

    @Override
    public String resetButton() {
        return "重置";
    }

    @Override
    public String exitCheckMessage() {
        return "有配置尚未保存，是否直接退出？";
    }

    @Override
    public String gamePathNotSet() {
        return "游戏路径没有设置";
    }

    @Override
    public String savedPathNotSet() {
        return "游戏配置文件路径没有设置";
    }

    @Override
    public String gamePathIsNotDirectory() {
        return "指定的游戏路径不是文件夹";
    }

    @Override
    public String savedPathIsNotDirectory() {
        return "指定的游戏配置文件路径不是文件夹";
    }

    @Override
    public String openBrowserForDownloadingFailed(String url) {
        return "打开浏览器失败，你可以手动输入该URL进行下载：" + url + "，也可尝试直接粘贴，程序已尝试将该url放到你的剪贴板里";
    }

    @Override
    public String invalidResolutionValue() {
        return "分辨率格式错误，格式为NxN";
    }

    @Override
    public String invalidResolutionIntegerValue() {
        return "分辨率格式错误，数值错误";
    }

    @Override
    public String invalidNumberValue() {
        return "数值格式错误";
    }

    @Override
    public String fightRangeOutOfBounds(double min, double max) {
        return "视距超出范围，限制:" + min + "~" + max;
    }

    @Override
    public String invalidConfigInFile(String key, String value) {
        return "配置文件错误，" + configNameMapping(key) + "=" + value;
    }

    @Override
    public String discardChangesConfirm() {
        return "是否放弃已进行的修改？";
    }

    @Override
    public String settingColumnNameName() {
        return "配置项";
    }

    @Override
    public String settingColumnNameValue() {
        return "值";
    }

    @Override
    public String openConfigIni() {
        return "打开Config.ini";
    }

    @Override
    public String openGameUserSettingsIni() {
        return "打开GameUserSettings.ini";
    }

    @Override
    public String openInputIni() {
        return "打开Input.ini";
    }

    @Override
    public String editMacro() {
        return "编辑按键宏";
    }

    @Override
    public String reloadMacro() {
        return "重新加载按键宏";
    }

    @Override
    public String aboutEmptyTableOrMissingFields() {
        return "为什么我的表格是空的，或者少了一些字段？";
    }

    @Override
    public String detailAboutEmptyTableOrMissingFields() {
        return """
            本工具仅允许修改存在于配置文件中的按键设定，如果配置文件中不存在该设定，则不会显示出来。
            1. 在游戏中修改方向的键位（WSAD）后，方向和视角转动等特殊配置将写入配置文件。
            2. 在游戏中修改其他键位后，剩余的所有配置将写入配置文件。
            另外，方向和视角转动等特殊配置，可以在"速率"一栏规定其方向，字段中的方括号为其正方向""";
    }

    @Override
    public String openFileFailed() {
        return "打开文件失败";
    }

    @Override
    public String launchGameFailed() {
        return "启动游戏失败";
    }

    @Override
    public String macroSwitchButtonLabel() {
        return "开启按键宏";
    }

    @Override
    public String macroAlertLabel() {
        return """
            需要使用管理员权限运行本程序才能在游戏中使用宏。
            此外请注意，本工具并非驱动级宏，会通过JavaFX Robot库调用Windows API，所以理论上可以被检测""";
    }

    @Override
    public String macroColumnNameEnable() {
        return "启用";
    }

    @Override
    public String macroColumnNameName() {
        return "宏命令";
    }

    @Override
    public String knowConsequencePrompt() {
        return "我已知晓相关技术原理并自行承担风险";
    }

    @Override
    public String fishingStartKey() {
        return "开始按键";
    }

    @Override
    public String fishingStopKey() {
        return "结束按键";
    }

    @Override
    public String fishingLeftKey() {
        return "左移";
    }

    @Override
    public String fishingRightKey() {
        return "右移";
    }

    @Override
    public String resetFishing() {
        return "重置参数";
    }

    @Override
    public String configureFishing() {
        return "配置钓鱼参数";
    }

    @Override
    public String configureFishingOnlyStep1() {
        return "仅配置钓鱼点";
    }

    @Override
    public String configureFishingHelpMsg() {
        return """
            本工具支持检测鱼耐力自动收杆，支持检测钓鱼失败以及自动恢复，不需要设置游戏分辨率或窗口位置
            但是需要对工具本身进行配置，配置分为两步：
            第一步配置钓鱼点位置和钓鱼按钮位置
            第二步配置鱼的耐力条和黄色滑块的位置

            第二步需要钓鱼一次，所以建议进行完整配置时换用绿鱼饵。
            由于每次钓鱼的位置都可能不一样，所以这里提供仅进行第一步配置的按钮，仅配置第一步不需要消耗任何道具。""";
    }

    @Override
    public String fishingConfiguringScreenDescription() {
        return "点击Enter（回车）确认配置，或者点击CTRL+W取消配置";
    }

    @Override
    public String fishingSwitchButtonLabel() {
        return "启用自动钓鱼";
    }

    @Override
    public String fishingConfigureTips1() {
        return """
            请在游戏中进入钓鱼状态（屏幕上有绿光的状态）
            之后回到本窗口，点击"确定"，然后马上切换到游戏界面，等待几秒后将进入配置界面。
            配置界面内，点击Enter（回车）确认配置，或者点击CTRL+W取消配置""";
    }

    @Override
    public String fishingConfigureTips2() {
        return """
            请在游戏中进入咬钩状态（顶部出现黄色位置条时）
            之后回到本窗口，点击"确定"，然后马上切换到游戏界面，等待几秒后将进入配置界面。
            配置界面内，点击Enter（回车）确认配置，或者点击CTRL+W取消配置""";
    }

    @Override
    public String positionOfFishingPointTip() {
        return "垂钓点的绿光发出的位置";
    }

    @Override
    public String positionOfCastingPointTip() {
        return "放置在钓鱼按钮上";
    }

    @Override
    public String positionOfPositionTip() {
        return "代表位置信息的黄色滑块的范围（要包括整个黑色长条）";
    }

    @Override
    public String positionOfFishStaminaTip() {
        return "鱼的体力条的范围";
    }

    @Override
    public String fishingStatus() {
        return "状态：";
    }

    @Override
    public String fishingStatusStopped() {
        return "停止";
    }

    @Override
    public String fishingStatusStopping() {
        return "停止中...";
    }

    @Override
    public String fishingStatusBegin() {
        return "开始";
    }

    @Override
    public String fishingStatusFailed() {
        return "钓鱼失败";
    }

    @Override
    public String fishingStatusWaitingForCasting() {
        return "等待抛竿";
    }

    @Override
    public String fishingStatusWaitingForBite() {
        return "等待咬钩";
    }

    @Override
    public String fishingStatusManagingPos() {
        return "钓鱼中...";
    }

    @Override
    public String fishingStatusBeforeReeling() {
        return "收杆前";
    }

    @Override
    public String fishingStatusAfterReeling() {
        return "收杆后";
    }

    @Override
    public String about() {
        return """
            本工具通过GPLv2开源。你可以在 https://github.com/wkgcass/hotta-pc-assistant 获取源代码。
            如果你要修改本工具并分发修改后的版本，请依旧遵循GPLv2协议，并提供源代码。
            请在github release页获取本工具，或者使用B站wkgcass发布的网盘链接，不要轻信其他来源。
            本程序不会报毒，如果发现报毒，请立即删除、断网并全盘查杀。

            美术素材来源说明：
            本程序使用JavaFX默认字体或者得意黑字体 B站@oooooohmygosh
            本程序欢迎页封面图来自幻塔官网，做了调色
            本程序图标为旅行莎莉头像，图片来自B站 @幻塔手游 的头像
            """;
    }

    @Override
    public String version() {
        return "版本号";
    }

    @Override
    public String latestVersion() {
        return "最新版本";
    }

    @Override
    public String latestVersionReleaseTime() {
        return "发布时间";
    }

    @Override
    public String lastSyncTime() {
        return "最后一次同步时间";
    }

    @Override
    public String contributor() {
        return "本工具代码贡献者";
    }

    @Override
    public String serverListColumnNameRegion() {
        return "地区";
    }

    @Override
    public String serverListColumnNameName() {
        return "服务器名称";
    }

    @Override
    public String enableHostsFileModificationForGlobalServer() {
        return "修改hosts文件";
    }

    @Override
    public String failedReadingTofServerList() {
        return "读取Tower of Fantasy服务器列表失败";
    }

    @Override
    public String failedWritingHostsFile() {
        return "写入hosts文件失败";
    }

    @Override
    public String swapConfigFailed() {
        return "切换国服和国际服配置文件失败";
    }

    @Override
    public String swapConfigFailedMissingVersionInConfig() {
        return "当前配置文件中没有指定游戏版本（国服/国际服），游戏配置文件目录无法自动切换";
    }

    @Override
    public String swapConfigFailedTargetFileIsNotDir() {
        return "待切换的配置文件路径不是文件夹";
    }

    @Override
    public String swapConfigFailedPathToMoveToIsOccupied() {
        return "切换后的配置文件路径已被占用";
    }

    @Override
    public String swapConfigFailedCreatingSavedDirFailed() {
        return "创建配置文件保存目录失败";
    }

    @Override
    public String chooseGameVersionDesc() {
        return "由于幻塔国服和国际服使用相同的配置文件目录，为了后续能够自动切换国服和国际服，请选择当前配置文件目录对应的游戏版本";
    }

    @Override
    public String chooseCNVersion() {
        return "国服";
    }

    @Override
    public String chooseGlobalVersion() {
        return "国际服";
    }
}
