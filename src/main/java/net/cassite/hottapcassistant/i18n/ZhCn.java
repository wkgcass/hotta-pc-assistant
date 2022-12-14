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
    public String toolNameCoolDown() {
        return "CD指示器(beta)";
    }

    @Override
    public String toolNameAbout() {
        return "关于";
    }

    @Override
    public String toolIsLocked(String name) {
        name = switch (name) {
            case "macro" -> toolNameMacro();
            case "fishing" -> toolNameFishing();
            default -> name;
        };
        return "此工具暂未开放：" + name;
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
            case "Diving" -> "潜水";
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
            case "ResolutionSizeX" -> "[国际服生效]分辨率X";
            case "ResolutionSizeY" -> "[国际服生效]分辨率Y";
            case "FullscreenMode" -> "[国际服生效]全屏模式[1=全屏|2=窗口]";
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
    public String rememberMousePositionButtonLabel() {
        return "记忆鼠标位置";
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
        return "垂钓点的绿光发出的位置（按WSAD可微调）";
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
        return "鱼的体力条的范围（左边青色弧形条）";
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
            本程序欢迎页封面图来自幻塔官网或者游戏内素材，做了调色
            本程序图标为旅行莎莉头像，图片来自B站 @幻塔手游 的头像
            武器、意志、源器、技能、buff等图标，来源于fandom tof wiki图片和幻塔客户端截图
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
        return "修改hosts文件直连国际服（需要使用管理员权限运行本程序）";
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

    @Override
    public String weaponName(String name) {
        return switch (name) {
            case "gé lái pǔ ní" -> "格莱普尼";
            case "liú quán chè xīn" -> "流泉澈心";
            case "qǐ míng xīng" -> "启明星";
            case "yǐng zhī" -> "影织";
            case "pò jūn" -> "破均";
            case "bīng fēng zhī shǐ" -> "冰风之矢";
            case "bā ěr méng kè" -> "巴尔蒙克";
            case "bú miè zhī yì" -> "不灭之翼";
            case "hóng lián rèn" -> "红莲刃";
            case "fù hé gōng" -> "复合弓";
            case "hēi yā míng lián" -> "黑鸦冥镰";
            case "v2 róng qǖ dùn" -> "V2熔驱盾";
            case "fǒu jué lì fāng" -> "否决立方";
            case "chì yàn zuǒ lún" -> "炽焰左轮";
            case "sī pà kè" -> "斯帕克";
            case "sì yè shí zì (burn)" -> "四叶十字(灼烧)";
            case "sì yè shí zì (gas)" -> "四叶十字(瓦斯)";
            case "pò xiǎo" -> "破晓";
            case "wǎn dǎo" -> "晚祷";
            case "líng guāng" -> "陵光";
            default -> name;
        };
    }

    @Override
    public String matrixName(String name) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (name) {
            case "kè láo dí yà" -> "克劳迪娅";
            default -> name;
        };
    }

    @Override
    public String relicsName(String name) {
        return switch (name) {
            case "dice" -> "天选骰子";
            case "kǎo-ēn-tè" -> "考恩特";
            case "kǎo-ēn-tè-2" -> "考恩特II型";
            default -> name;
        };
    }

    @Override
    public String simulacraName(String name) {
        //noinspection SwitchStatementWithTooFewBranches
        return switch (name) {
            case "xīng huán" -> "星寰";
            default -> name;
        };
    }

    @Override
    public String buffName(String name) {
        return switch (name) {
            case "liuQuanCheXinCounter" -> "流泉澈心技能次数";
            case "yingYueZhiJingBuffTimer" -> "映月之境";
            case "bingFengZhiShiBuffTimer" -> "冰风之矢增伤";
            case "siYeShiZiShotRemain" -> "四叶十字狙击次数";
            case "siYeShiZiDodgeRemain" -> "四叶十字闪避次数";
            case "opticalSpaceTimer" -> "光学空间";
            case "shiZiZhuoShaoBuffTimer" -> "十字灼烧";
            case "liZiZhuoShaoBuffTimer" -> "离子灼烧";
            case "burnSettleTimer" -> "灼烧结算";
            case "diceBuffTimer" -> "热核增伤";
            case "kaoEnTeBuffTimer" -> "考恩特增伤";
            case "xingHuanSimulacraTimer" -> "星寰拟态霸体效果";
            case "wanDaoHuiQiCounter" -> "晚祷辉起计数";
            default -> name;
        };
    }

    @Override
    public String startCoolDown() {
        return "启动";
    }

    @Override
    public String stopCoolDown() {
        return "停止";
    }

    @Override
    public String weaponNotSelected() {
        return "存在空的武器位";
    }

    @Override
    public String duplicatedWeapon() {
        return "武器选择重复";
    }

    @Override
    public String duplicatedRelics() {
        return "源器选择重复";
    }

    @Override
    public String relicsChooserPlaceHolder(int index) {
        return "源器" + (index + 1);
    }

    @Override
    public String loadingStageTitle() {
        return "加载中...";
    }

    @Override
    public String hintPressAlt() {
        return "按住alt键有惊喜！";
    }

    @Override
    public String cooldownTipsButton() {
        return "说明";
    }

    @Override
    public String cooldownTips() {
        return """
            在游戏中使用本工具需要管理员权限。
            本工具会直接监听键盘鼠标事件，并根据这些事件来模拟武器的切换和技能的释放。
            所以本工具不会和游戏本体有任何交互，也不会被检测。
            但正因为所有事件都是模拟的，工具给出的结果会不可避免的与游戏实际情况发生差异。
            为此，本工具设计了一些容错方案，尽可能的与游戏保持一致：
            1. 对于所有武器，如果软件计算的冷却时间已经经过了90%以上，那么即使没有冷却完全，也视为可使用的状态；
            2. 对于1星及以上的格莱普尼（芬璃尔），在短时间内连续按下5次技能按钮，则无视当前状态，直接视为冷却完全并依次释放了一次主技能和一次1星副技能的状态；
            3. 对于6星影织（凛夜），技能冷却进度条会正常显示，但因软件无法计算连携次数，所以技能本身不会进入冷却，需要玩家自行盯紧buff栏；
            4. 对于1星及以上的流泉澈心（不破咲），点击其技能计数器可以增加次数，方便玩家在读条后同步该计数器；
            5. 对于3星及以上的炽焰左轮，由于无法判定灼烧情况，所以只要进行闪避攻击或者蓄力攻击，就认为触发了离子灼烧；
            6. 对于6星的炽焰左轮，只计算离子灼烧对其的冷却减少效果，且冷却减少间隔使用2秒而非游戏中的1.5秒；
            7. 对于触发恩赐共鸣的晚祷（星环），点击辉起计数器可以将其重置为3次，方便玩家在读条后同步该计数器；
            8. 短按天选骰子则重置所有武器的技能冷却，长按（大于300毫秒）则只提供热核buff，不刷新技能冷却；
            9. 同时装备1星或以上陵光和四叶十字时，由于羽翎非常容易获取，所以默认永远处于三把羽翎的状态，四叶十字的弹药消耗为每次4发；
            10. 四叶十字（瓦斯）的冷却减少效果和实际操作有极强的关联性，所以在实战中使用四叶十字（瓦斯）时，冷却时间不保证完全匹配；
            即使存在这些容错设计，某些情况下还是不可避免的与游戏发生不一致，所以正确用法是：主要以游戏为准，然后时不时瞅一眼冷却指示器。
            """;
    }
}
