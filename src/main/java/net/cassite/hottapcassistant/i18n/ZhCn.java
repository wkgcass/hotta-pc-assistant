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
    public String loadingFontFailed(String name) {
        return "加载" + name + "字体失败";
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
    public String toolNameToolBox() {
        return "工具箱";
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
            case "bAutoCombatDiet" -> "自动战斗-使用食物";
            case "AutoCombatDietHpPercent" -> "自动战斗-使用食物时的血量百分比";
            case "bAutoCombatArtifactSkill" -> "自动战斗-使用源器";
            case "bAutoCombatChangeWeaponSkill" -> "自动战斗-切换武器";
            case "fFightCameraDistance" -> "战斗时镜头距离";
            case "MaxVisibilityPlayer" -> "可见玩家数量";
            case "FrameRateLimit" -> "帧率限制";
            case "ResolutionSizeX" -> "分辨率X";
            case "ResolutionSizeY" -> "分辨率Y";
            case "FullscreenMode" -> "全屏模式[1=全屏|2=窗口]";
            case "bPreferD3D12InGame" -> "使用DX12";
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
            由于每次钓鱼的位置都可能不一样，所以这里提供仅进行第一步配置的按钮，仅配置第一步不需要消耗任何道具。

            如果钓鱼时指示器显示游标飘忽不定，可能是程序将背景的白色误判为了游标，尝试转动视角改变顶部背景再试一次""";
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
            本程序使用JavaFX默认字体、Noto字体、得意黑字体 B站@oooooohmygosh
            本程序欢迎页封面图来自幻塔官网或者游戏内素材，做了调色
            本程序图标为旅行莎莉头像，图片来自B站 @幻塔手游 的头像
            武器、意志、源器、技能、buff等图标，来源于fandom tof wiki图片和幻塔客户端截图
            拟态语音来自游戏内语音
            CD指示器的重置图标：Reload icons created by IYAHICON - Flaticon
            CD指示器的暂停图标：Pause icons created by Hilmy Abiyyu A. - Flaticon
            CD指示器的恢复图标：Play button icons created by Roundicons - Flaticon
            聊天消息辅助图标：Keyboard icons created by Freepik - Flaticon
            幻塔多开图标：Ui icons created by Graphics Plazza - Flaticon
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
            case "líng dù zhǐ zhēn" -> "零度指针";
            case "ā lài yē shí" -> "阿赖耶识";
            case "jí léi shuāng rèn" -> "疾雷双刃";
            case "chāo diàn cí shuāng xīng" -> "超电磁双星";
            case "chū dòng zhòng jī" -> "初动重击";
            case "diàn cí rèn" -> "电磁刃";
            case "gě dòu dāo" -> "格斗刀";
            case "huán hǎi lún rèn" -> "环海轮刃";
            case "léi tíng zhàn jǐ" -> "雷霆战戟";
            case "mò bǐ wū sī" -> "莫比乌斯";
            case "qiáng wēi zhī fēng" -> "蔷薇之锋";
            case "shèng hén quán zhàng" -> "圣痕权杖";
            case "shuāng dòng cháng qiāng" -> "霜冻长枪";
            case "yè què zhī yǔ" -> "夜雀之羽";
            case "zhōng jié zhě" -> "终结者";
            case "piàn guāng líng yǚ" -> "片光零羽";
            default -> name;
        };
    }

    @Override
    public String matrixName(String name) {
        return switch (name) {
            case "kè láo dí yà" -> "克劳迪娅";
            case "lǐn yè" -> "凛夜";
            case "léi bèi" -> "蕾贝";
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
        return switch (name) {
            case "xīng huán" -> "星寰";
            case "ài lì sī" -> "艾丽丝";
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
            case "linYe2MatrixBuffTimer" -> "凛夜意志两件套";
            case "yongDongCD" -> "涌动";
            case "lingDuZhiZhenBeeTimer" -> "零度指针小蜜蜂存在时间";
            case "leiBeiMatrixBuffTimer" -> "蕾贝意志四件套";
            case "lingGuangYuJing" -> "陵光御境";
            case "lingGuangTaunt" -> "陵光坚毅嘲讽";
            case "yeQueZhiYuStar5" -> "夜雀之羽5星重伤";
            case "moShuShiJian" -> "魔术时间";
            case "chargePercentage" -> "充能百分比";
            case "yingZhiStar6Counter" -> "影织6星连携次数";
            case "aiLiSiSimulacraBuff" -> "艾丽丝拟态增伤效果";
            case "zhiHanChangYu" -> "滞寒场域";
            case "moBiWuSiBuff" -> "莫比乌斯提前离开领域增伤";
            case "leiDianGanYing" -> "雷电感应";
            case "guiJiCounter" -> "归寂可用次数";
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
    public String cooldownOptionsBtn() {
        return "选项";
    }

    @Override
    public String cooldownOptionsTitle() {
        return "冷却指示器选项";
    }

    @Override
    public String cooldownScanDischargeDesc() {
        return "连携充能检测功能会读取屏幕，以识别当前的充能值。\n" +
               "该检测仅对屏幕指定区域进行截图操作（截图频率较高），不会读取游戏内存数据。";
    }

    @Override
    public String cooldownScanDischargeCheckBox() {
        return "启用连携充能检测（beta）";
    }

    @Override
    public String cooldownScanDischargeDebugCheckBox() {
        return "开启debug模式（充能检测结果会放置在剪贴板中）";
    }

    @Override
    public String cooldownScanDischargeUseNativeCaptureCheckBox() {
        return "使用native屏幕捕捉（beta）";
    }

    @Override
    public String cooldownScanDischargeUseRoughCaptureCheckBox() {
        return "使用不精确的屏幕捕捉（高DPI有影响）";
    }

    @Override
    public String cooldownScanDischargeResetBtn() {
        return "重置连携充能检测配置";
    }

    @Override
    public String cooldownApplyDischargeForYingZhiCheckBox() {
        return "开启6星影织连携计数";
    }

    @Override
    public String cooldownPlayAudioCheckBox() {
        return "播放拟态语音";
    }

    @Override
    public String cooldownAutoFillPianGuangLingYuSubSkillCheckbox() {
        return "切换到片光零羽时总是填充2层子技能";
    }

    @Override
    public String scanDischargeConfigureTips() {
        return """
            请先进入游戏将充能打到即将充满的状态（90%以上）
            之后回到本窗口，点击"确定"，然后马上切换到游戏界面，等待几秒后将进入配置界面。
            配置界面内，点击Enter（回车）确认配置，或者点击CTRL+W取消配置""";
    }

    @Override
    public String positionOfDischargeTip() {
        return "将选框拖动至任意一把后台武器上，尽量刚好包括其充能条";
    }

    @Override
    public String scanDischargeScreenDescription() {
        return "点击Enter（回车）确认配置，或者点击CTRL+W取消配置";
    }

    @Override
    public String failedCalculatingCriticalPoints() {
        return "计算关键点失败，请重新框选。\n" +
               "请注意，充能必须是差一点点充满的状态（90%以上）";
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
    public String cooldownConfigurationLabel() {
        return "装配方案";
    }

    @Override
    public String cooldownConfigurationDuplicate() {
        return "装配方案名称重复";
    }

    @Override
    public String cooldownConfigurationSave() {
        return "保存";
    }

    @Override
    public String cooldownConfigurationDelete() {
        return "删除";
    }

    @Override
    public String cooldownPauseDesc() {
        return "暂停";
    }

    @Override
    public String cooldownResumeDesc() {
        return "恢复";
    }

    @Override
    public String cooldownResetDesc() {
        return "重置";
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
            在游戏中使用本工具需要管理员权限。使用鼠标滚轮可以缩放指示器窗口。右键点击buff指示器可以将其切换到第二行（或切换回来）
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
            8. 同时装备1星及以上陵光和四叶十字时，由于羽翎非常容易获取，所以默认永远处于三把羽翎的状态，四叶十字的弹药消耗为每次4发；
            9. 四叶十字（瓦斯）的冷却减少效果和实际操作有极强的关联性，所以在实战中使用四叶十字（瓦斯）时，冷却时间不保证完全匹配；
            10. 由于无法检测电爆，所以不处理超电磁双星6星效果；
            即使存在这些容错设计，某些情况下还是不可避免的与游戏发生不一致，所以正确用法是：主要以游戏为准，然后时不时瞅一眼冷却指示器。
            此外有如下特殊处理：
            1. 长按（大于180毫秒）武器按键切换到其他武器时，视为释放了一次连携技，目前[流泉澈心, 破晓]对此行为有特殊处理；
            2. 对于[晚祷, 零度指针, 否决立方, 启明星]，支持在技能前摇时通过跳跃或者切换武器来取消技能的释放；
            3. 短按天选骰子则重置所有武器的技能冷却，长按则只提供热核buff，不刷新技能冷却；
            """;
    }

    @Override
    public String invalidAssistantConfigFileAskForDeletion(String configFilePath) {
        return "幻塔PC助手配置文件异常。您可以修改文件" + configFilePath + "手动修复异常，" + "\n" +
               "也可以直接删除该配置文件并以初始配置执行后续操作。";
    }

    @Override
    public String modifyInvalidAssistantConfigBtn() {
        return "修改";
    }

    @Override
    public String deleteInvalidAssistantConfigBtn() {
        return "删除";
    }

    @Override
    public String cancelInvalidAssistantConfigBtn() {
        return "取消";
    }

    @Override
    public String toolName(String name) {
        return switch (name) {
            case "world-boss-timer" -> "世界Boss计时器";
            case "message-helper" -> "聊天消息辅助";
            case "lan's-brain-wash" -> "岚的洗脑循环";
            case "multi-hotta-instance" -> "幻塔双开";
            default -> name;
        };
    }

    @Override
    public String worldBossTimerLineCol() {
        return "线";
    }

    @Override
    public String worldBossTimerNameCol() {
        return "Boss名称";
    }

    @Override
    public String worldBossTimerLastKillCol() {
        return "最后击杀时间";
    }

    @Override
    public String worldBossTimerETACol() {
        return "预计刷新时间";
    }

    @Override
    public String worldBossTimerSpawnMinutesCol() {
        return "刷新用时（分钟）";
    }

    @Override
    public String worldBossTimerCommentCol() {
        return "备注";
    }

    @Override
    public String worldBossTimerLastLineCol() {
        return "线";
    }

    @Override
    public String worldBossTimerAccountNameCol() {
        return "账号名";
    }

    @Override
    public String worldBossTimerLastSwitchLineTsCol() {
        return "最后切换时间";
    }

    @Override
    public String worldBossTimerAccountETACol() {
        return "预计可切线时间";
    }

    @Override
    public String worldBossTimerSwitchLineCDMinutes() {
        return "换线CD时间（分钟）";
    }

    @Override
    public String worldBossTimerAddBtn() {
        return "添加";
    }

    @Override
    public String worldBossTimerEditBtn() {
        return "修改";
    }

    @Override
    public String worldBossTimerSpawnBtn() {
        return "击杀";
    }

    @Override
    public String worldBossTimerDelBtn() {
        return "删除";
    }

    @Override
    public String worldBossTimerCopyBossInfoBtn() {
        return "复制喊话";
    }

    @Override
    public String worldBossTimerExportBtn() {
        return "导出";
    }

    @Override
    public String worldBossTimerImportBtn() {
        return "导入";
    }

    @Override
    public String worldBossTimerIncludeBossTimerCheckBox() {
        return "包含世界Boss计时数据";
    }

    @Override
    public String worldBossTimerIncludeAccountTimerCheckBox() {
        return "包含账号计时数据";
    }

    @Override
    public String worldBossTimerIncludeMsgTemplateCheckBox() {
        return "包含喊话消息模板";
    }

    @Override
    public String worldBossTimerMergeImportCheckBox() {
        return "合并数据（不勾选则为替换数据）";
    }

    @Override
    public String worldBossTimerSwitchLineBtn() {
        return "切线";
    }

    @Override
    public String worldBossTimerClearBtn() {
        return "清空";
    }

    @Override
    public String worldBossTimerOkBtn() {
        return "确定";
    }

    @Override
    public String worldBossTimerMissingLine() {
        return "没有填写分线";
    }

    @Override
    public String worldBossTimerMissingName() {
        return "没有填写Boss名称";
    }

    @Override
    public String worldBossTimerMissingLastKill() {
        return "没有填写最后击杀时间";
    }

    @Override
    public String worldBossTimerMissingSpawnMinutes() {
        return "没有填写刷新时间";
    }

    @Override
    public String worldBossTimerMissingLastSwitchLineTs() {
        return "没有填写最后切线时间";
    }

    @Override
    public String worldBossTimerMissingSwitchLineCD() {
        return "没有指定切线CD时间";
    }

    @Override
    public String worldBossTimerInvalidLine() {
        return "分线填写错误";
    }

    @Override
    public String worldBossTimerInvalidLastKill() {
        return "最后击杀时间填写错误";
    }

    @Override
    public String worldBossTimerInvalidSpawnMinutes() {
        return "刷新时间填写错误";
    }

    @Override
    public String worldBossTimerInvalidLastSwitchLineTs() {
        return "最后切线时间填写错误";
    }

    @Override
    public String worldBossTimerInvalidSwitchLineCD() {
        return "切线CD时间填写错误";
    }

    @Override
    public String worldBossTimerNextBossInfoDefaultTemplate() {
        return """
            msg = ('预计下一个[' + name + ']将于' + hh + ':' + mm + '在' + line + '线刷新，')
            if: remainingMillis > 0; then {
              msg += ('剩余') + (remainingMillis / 1000 / 60)
            } else {
              msg += ('已刷新') + (-remainingMillis / 1000 / 60)
            }
            msg += ('分钟')
            if: msg.length < 35; then {
              msg += ('，请换线的大佬喊一声再换')
            }
            """;
    }

    @Override
    public String worldBossTimerInvalidTemplate() {
        return "喊话模板配置错误";
    }

    @Override
    public String worldBossTimerNoDataToImport() {
        return "没有可导入的数据，请先复制再点击导入按钮";
    }

    @Override
    public String worldBossTimerInvalidImportingData() {
        return "导入数据错误";
    }

    @Override
    public String brainWashLanStartBtn() {
        return "开始";
    }

    @Override
    public String brainWashLanStopBtn() {
        return "停止";
    }

    @Override
    public String brainWashLanFreqSliderDesc() {
        return "洗脑频率（单位：次/分钟）";
    }

    @Override
    public String brainWashLanRandTimeSliderDesc() {
        return "随机时间区间（单位：秒）";
    }

    @Override
    public String selectBetaGameLocation() {
        return "选择测试服路径";
    }

    @Override
    public String selectOnlineGameLocation() {
        return "选择正式服路径";
    }

    @Override
    public String multiInstanceAdvBranch() {
        return "分支";
    }

    @Override
    public String multiInstanceResourceVersion() {
        return "资源版本";
    }

    @Override
    public String multiInstanceResourceSubVersion() {
        return "资源子版本";
    }

    @Override
    public String multiInstanceClientVersion() {
        return "客户端版本";
    }

    @Override
    public String multiInstanceSaveCaCert() {
        return "获取根证书";
    }

    @Override
    public String multiInstanceEmptyFieldAlert() {
        return "存在空字段，请填充后再启动";
    }

    @Override
    public String multiInstanceCannotMakeLink() {
        return "创建链接文件失败";
    }

    @Override
    public String multiInstanceCannotSetHostsFile() {
        return "修改hosts文件失败，请检查本程序是否以管理员权限启动";
    }

    @Override
    public String multiInstanceLaunchProxyServerFailed() {
        return "启动代理服务失败，请确保本机443端口未被占用";
    }

    @Override
    public String selectGameLocationDescriptionWithoutAutoSearching() {
        return "请选择游戏路径，手动选择gameLauncher.exe所在路径";
    }

    @Override
    public String failedSavingCaCertFile() {
        return "保存CA证书失败";
    }
}
