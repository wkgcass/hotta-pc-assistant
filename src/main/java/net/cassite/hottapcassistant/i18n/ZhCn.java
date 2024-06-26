package net.cassite.hottapcassistant.i18n;

import io.vproxy.vfx.ui.loading.LoadingItem;
import net.cassite.tofpcap.messages.ChatChannel;

import java.util.ArrayList;
import java.util.Set;

public class ZhCn extends I18n {
    @Override
    public String id() {
        return "zhcn";
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
        return "CD指示器";
    }

    @Override
    public String toolNameToolBox() {
        return "工具箱";
    }

    @Override
    public String toolNameXBox() {
        return "自定义XBox键位";
    }

    @Override
    public String toolNameAbout() {
        return "关于";
    }

    @Override
    public String toolNameLog() {
        return "日志";
    }

    @Override
    public String toolNameReset() {
        return "重置/修复";
    }

    @Override
    public String toolIsLocked(String name) {
        name = switch (name) {
            case "macro" -> toolNameMacro();
            case "fishing" -> toolNameFishing();
            case "multi" -> toolName("multi-hotta-instance");
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
    public String alertChangeSavedDirectory() {
        return "通常不需要修改游戏配置文件路径，一般仅在切换测试服/台服时才需要修改。如果真的要修改，请按住ALT键再点击该按钮";
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
            case "SkillAdditional" -> "额外技能";
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
        return "游戏配置文件路径没有设置，无法自动切换国服与国际服游戏配置文件";
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
    public String macroTipsButton() {
        return "如何自定义宏？";
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
    public String macroTips() {
        return """
                   配置一个新宏，需要如下步骤：
                     1. 在助手中点击[""".trim() + editMacro() +
               """
                   ]，按照约定规范定义一个新宏，保存配置
                     2. 在助手中点击[""".trim() + reloadMacro() +
               """
                   ]，然后在表格中定义其触发快捷键
                     3. 启用宏，按下快捷键触发
                   在配置新宏时，需要指定如下属性：
                     1. name = 宏名称
                     2. type = 宏类型，目前支持：
                                 NORMAL: 普通宏，只运行一次
                                 INFINITE_LOOP: 无限循环
                                 FINITE_LOOP: 有限循环
                     3. steps = 步骤列表，每一项是宏执行的一条命令，例如按下键盘、等待时间等
                     4. loopLimit = 对于有限循环宏，还需指定其循环次数
                   配置步骤时，首先需要指定该步骤的类型，需要的参数会根据类型的不同而有所不同：
                     1. @type = Delay 延时
                        参数:
                          millis = 延时的时长，该值单位为毫秒
                     2. @type = KeyPress 按下键盘按键或鼠标按钮
                        参数:
                          key = 键盘按键或鼠标按钮的字符串，格式为UnrealEngine支持的格式
                     3. @type = KeyRelease 弹起键盘按键或鼠标按钮
                        参数:
                          key = 键盘按键或鼠标按钮的字符串，格式为UnrealEngine支持的格式
                     4. @type = SafePoint 当停止运行脚本时可以在此处中断
                        参数：无
                     5. @type = MouseMove 将鼠标移动至指定坐标
                        参数:
                          x = 屏幕X坐标（整数）
                          y = 屏幕Y坐标（整数）
                     6. @type = StressBegin 开始占用CPU资源
                        参数：无
                     7. @type = StressEnd 结束占用CPU资源
                        参数：无
                   其中，key取值可参考UnrealEngine规范，除小键盘区外绝大部分均已实现
                        或者查看vfx库的io/vproxy/vfx/entity/input/KeyCode.java代码
                   常用的值有：
                     1. LeftMouseButton, RightMouseButton: 鼠标左键/右键
                     2. One, Two, Three, ..., Nine, Zero: 主键盘区1，2，3，...，9，0
                     3. A~Z: 主键盘区字母按键
                     4. LeftControl, RightShift, LeftAlt, ...: 左侧CTRL，右侧Shift，左侧Alt
                   举例：
                   1. 大锤宏需要按住鼠标左键3秒然后释放，等待0.5秒后继续循环执行
                      {
                        name = 大锤宏
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
    public String applyPatchFailed() {
        return "应用补丁失败";
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
    public String macroColumnNameType() {
        return "类型";
    }

    @Override
    public String macroTypeNormal() {
        return "普通";
    }

    @Override
    public String macroTypeInfiniteLoop() {
        return "无限循环";
    }

    @Override
    public String macroTypeFiniteLoop() {
        return "有限循环";
    }

    @Override
    public String macroColumnNameStatus() {
        return "状态";
    }

    @Override
    public String macroStatusRunning() {
        return "运行中...";
    }

    @Override
    public String macroStatusStopped() {
        return "停止";
    }

    @Override
    public String macroStatusStopping() {
        return "正在停止...";
    }

    @Override
    public String knowConsequencePrompt() {
        return "我已知晓相关技术原理并自行承担风险";
    }

    @Override
    public String fishingStartButton() {
        return "开始";
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
    public String fishingCastKey() {
        return "抛竿";
    }

    @Override
    public String fishingSkipFishingPointCheckBox() {
        return "跳过绿点检测，改为5秒延迟";
    }

    @Override
    public String fishingUseCastKeyCheckBox() {
        return "使用键盘按键抛竿，不使用鼠标点击，并且使用ESC关闭结算界面（首次钓鱼时需手动点击一次游戏界面）";
    }

    @Override
    public String fishingDebugCheckBox() {
        return "Debug模式。启用后，屏幕截图将存入剪贴板";
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
    public String configureFishingHelpMsg() {
        return """
            本工具支持检测鱼耐力自动收杆，支持检测钓鱼失败以及自动恢复，不需要设置游戏分辨率或窗口位置
            但是需要对工具本身进行配置，配置分为两步：
            第一步配置钓鱼点位置和钓鱼按钮位置
            第二步配置鱼的耐力条和黄色滑块的位置

            第二步需要钓鱼一次，所以建议进行完整配置时换用绿鱼饵。
            由于每次钓鱼的位置都可能不一样，所以每次开始钓鱼时，都需要配置一次绿点位置，配置后钓鱼将自动开始。

            如果钓鱼时指示器显示游标飘忽不定，可能是程序将背景的白色误判为了游标，尝试转动视角改变顶部背景再试一次""";
    }

    @Override
    public String fishTutorialLinkDesc() {
        return "点这里查看钓鱼工具教学视频";
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
    public String fishingStartConfiguring() {
        return "开始配置";
    }

    @Override
    public String fishingSkipConfigureStep1Button() {
        return "上次配置后没有离开过钓鱼模式，跳过该配置";
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
    public String fishingCastKeyNotSet() {
        return "抛竿按键没有设置";
    }

    @Override
    public String fishingOpenBrowserForTutorialFailed(String url) {
        return "打开浏览器失败，你可以手动输入该URL查看钓鱼教程：" + url + "，也可尝试直接粘贴，程序已尝试将该url放到你的剪贴板里";
    }

    @Override
    public String about() {
        return """
            本工具通过GPLv2开源。你可以在 https://github.com/wkgcass/hotta-pc-assistant 获取源代码。
            如果你要修改本工具并分发修改后的版本，请依旧遵循GPLv2协议，并提供源代码。
            请在github release页获取本工具，或者使用B站wkgcass发布的网盘链接，不要轻信其他来源。
            本程序不会报毒，如果发现报毒，请立即删除、断网并全盘查杀。
                        
            英文翻译来源说明：
            使用GPT-4翻译，部分进行过微调。
                        
            美术素材来源说明：
            本程序使用JavaFX默认字体、Jetbrains Mono、Noto、得意黑（B站@oooooohmygosh）
            本程序欢迎页封面图来自幻塔官网或者游戏内素材，做了调色
            本程序图标为旅行莎莉头像，图片来自B站 @幻塔手游 的头像
            武器、意志、源器、技能、buff等图标，来源于fandom tof wiki图片和幻塔客户端截图
            拟态语音来自游戏内语音
            CD指示器的问号图标：Help icons created by Vaadin - Flaticon
            CD指示器的重置图标：Reload icons created by IYAHICON - Flaticon
            CD指示器的暂停图标：Pause icons created by Hilmy Abiyyu A. - Flaticon
            CD指示器的恢复图标：Play button icons created by Roundicons - Flaticon
            聊天消息辅助图标：Wired icons created by Vectorslab - Flaticon
            幻塔多开图标：Ui icons created by Graphics Plazza - Flaticon
            状态指示器图标：Conclusion icons created by Kiranshastry - Flaticon
            聊天消息监听器: Chat box icons created by Pixel perfect - Flaticon
            消息图标：Info icons created by Plastic Donut - Flaticon
            补丁管理器：Puzzle icons created by Freepik - Flaticon

            依赖开源项目：
            1. openjdk: GPLv2 with Classpath Exception
            2. javafx: GPLv2 with Classpath Exception
            3. vertx: Eclipse Public License
            4. vfx: MIT License
            5. jna: LGPL 2.1
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
            case "yuè xīng chuàn" -> "月星钏";
            case "xiǎo xiǎo jǜ fēng" -> "小小飓风";
            case "jué xiǎng" -> "绝响";
            case "sōng hùi" -> "松彗";
            case "pián zhēn" -> "骈臻";
            case "zhí míng" -> "执明";
            case "mèng zhāng" -> "孟章";
            case "chóng ruǐ" -> "重蕊";
            case "jiān bīng" -> "监兵";
            case "zǐ zhú" -> "紫竹";
            case "guī héng" -> "规衡";
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
            case "shuiYiShuoHuaBuff" -> "水意烁华效果持续时间";
            case "huiXiangCount" -> "回响计数";
            case "haiLaZhiYongTime" -> "海拉之拥增伤持续时间";
            case "guanFengCD" -> "观风冷却时间";
            case "mengZhangSkillCounter" -> "孟章技能层数";
            case "shenLouTime" -> "蜃楼持续时间";
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
    public String hideWhenMouseEnterCheckBox() {
        return "自动躲避鼠标（鼠标进入时隐藏计数器，按住ALT键才可进入或者点击）";
    }

    @Override
    public String lockCDWindowPositionCheckBox() {
        return "锁定指示器窗口（按住ALT键时允许拖动）";
    }

    @Override
    public String onlyShowFirstLineBuffCheckBox() {
        return "仅显示第一行buff栏（按住ALT键时恢复显示）";
    }

    @Override
    public String cooldownPlayAudioCheckBox() {
        return "播放拟态语音";
    }

    @Override
    public String cooldownSkipAudioCollection001CheckBox() {
        return "仅播放精选拟态语音";
    }

    @Override
    public String cooldownAutoFillPianGuangLingYuSubSkillCheckbox() {
        return "切换到片光零羽时总是填充2层子技能";
    }

    @Override
    public String cooldownAutoDischargeForYueXingChuanCheckBox() {
        return "切换到月星钏时总是视为释放连携";
    }

    @Override
    public String cooldownAutoDischargeForJueXiangCheckBox() {
        return "切换到绝响时总是视为释放连携";
    }

    @Override
    public String cooldownRefreshBuffRegardlessOfCDForBuMieZhiYi() {
        return "每当按下不灭之翼技能时，无论是否在cd均重置其领域持续时间";
    }

    @Override
    public String cooldownAlwaysCanUseSkillOfPianZhen() {
        return "骈臻技能总是视为可使用状态";
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
    public String waitForStartupVideoToFinish() {
        return "正在播放启动动画...";
    }

    @Override
    public String progressWelcomeText() {
        return "已完成加载，准备进入";
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
            3. 对于1星及以上的流泉澈心（不破咲），点击其技能计数器可以增加次数，方便玩家在读条后同步该计数器；
            4. 对于3星及以上的炽焰左轮，由于无法判定灼烧情况，所以只要进行闪避攻击或者蓄力攻击，就认为触发了离子灼烧；
            5. 对于6星的炽焰左轮，只计算离子灼烧对其的冷却减少效果，且冷却减少间隔使用2秒而非游戏中的1.5秒；
            6. 对于触发恩赐共鸣的晚祷（星环），点击辉起计数器可以将其重置为3次，方便玩家在读条后同步该计数器；
            7. 同时装备1星及以上陵光和四叶十字时，由于羽翎非常容易获取，所以默认永远处于三把羽翎的状态，四叶十字的弹药消耗为每次4发；
            8. 四叶十字（瓦斯）的冷却减少效果和实际操作有极强的关联性，所以在实战中使用四叶十字（瓦斯）时，冷却时间不保证完全匹配；
            9. 由于无法检测电爆，所以不处理超电磁双星6星效果；
            即使存在这些容错设计，某些情况下还是不可避免的与游戏发生不一致，所以正确用法是：主要以游戏为准，然后时不时瞅一眼冷却指示器。
            此外有如下特殊处理：
            1. 长按（大于180毫秒）武器按键切换到其他武器时，视为释放了一次连携技，目前[流泉澈心, 破晓]对此行为有特殊处理；
            2. 对于[晚祷, 零度指针, 否决立方, 启明星]，支持在技能前摇时通过跳跃或者切换武器来取消技能的释放；
            3. 短按天选骰子则重置所有武器的技能冷却，长按则只提供热核buff，不刷新技能冷却；
            4. 在[选项]中还有一些针对特定武器的额外配置项，可按需配置（选中武器时才可做相应的配置）；
            """;
    }

    @Override
    public String cooldownTutorialLink() {
        return "点这里查看CD指示器教学视频";
    }

    @Override
    public String cooldownOpenBrowserForTutorialFailed(String url) {
        return "打开浏览器失败，你可以手动输入该URL查看CD指示器教学：" + url + "，也可尝试直接粘贴，程序已尝试将该url放到你的剪贴板里";
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
            case "status-indicator" -> "状态指示器";
            case "message-monitor" -> "聊天消息监控";
            case "patch-manager" -> "补丁管理器";
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
        return "导入时合并数据";
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
            msg = (name + '将于' + hh + ':' + mm + '在<at>' + line + '</>线刷新，')
            if: remainingMillis > 0; then {
              msg += ('剩余') + (remainingMillis / 1000 / 60)
            } else {
              msg += ('已刷新') + (-remainingMillis / 1000 / 60)
            }
            msg += ('分钟')
            if: msg.length < 38; then {
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
    public String selectOnlineModGameLocation() {
        return "选择MOD正式服路径";
    }

    @Override
    public String multiInstanceAdvBranch() {
        return "测试服分支";
    }

    @Override
    public String multiInstanceOnlineBranch() {
        return "正式服分支";
    }

    @Override
    public String multiInstanceOnlineModBranch() {
        return "Mod正式服分支";
    }

    @Override
    public String multiInstanceOnlineVersion() {
        return "正式服版本";
    }

    @Override
    public String multiInstanceIsHandlingAdvCheckBox() {
        return "处理测试服客户端流量";
    }

    @Override
    public String multiInstanceSaveCaCert() {
        return "获取根证书";
    }

    @Override
    public String multiInstanceTutorialLink() {
        return "点这里查看幻塔双开工具教学视频";
    }

    @Override
    public String multiInstanceInvalidFieldAlert(String field) {
        return switch (field) {
            case "onlinePath" -> "正式服路径为空或有误";
            case "advBranch" -> "测试服分支为空或有误";
            case "onlineBranch" -> "正式服分支为空或有误";
            case "onlineModBranch" -> "正式服Mod分支为空或有误";
            case "onlineVersion" -> "正式服版本为空或有误（x.y.z）";
            case "betaPath&onlineModPath" -> "正式服Mod和测试服的路径至少填写一项";
            default -> STR."\{field} 为空或有误";
        };
    }

    @Override
    public String multiInstanceLaunchStep(String step) {
        return switch (step) {
            case "lock" -> "功能锁定检查";
            case "clientVersion" -> "设置客户端版本";
            case "UserData" -> "替换用户文件";
            case "UserData2" -> "为mod启动器替换用户文件";
            case "ResList.xml" -> "为测试服启动器写入资源配置文件(1)";
            case "ResList.xml-2" -> "为mod启动器写入资源配置文件(1)";
            case "config.xml" -> "为测试服启动器写入资源配置文件(2)";
            case "config.xml-2" -> "为mod启动器写入资源配置文件(2)";
            case "Client" -> "链接客户端目录";
            case "Client2" -> "为mod启动器链接客户端目录";
            case "resolve" -> "执行DNS解析";
            case "server" -> "启动代理服务器";
            case "hijack" -> "启动DNS劫持";
            case "flush-dns" -> "清空DNS缓存";
            default -> step;
        };
    }

    @Override
    public String multiInstanceFailedSettingClientVersion() {
        return "设置客户端版本失败，请确认正式服路径是否设置正确";
    }

    @Override
    public String multiInstanceFailedReplacingUserDataDir() {
        return "替换用户文件失败，请确认多开客户端对应的Wmgp进程已退出";
    }

    @Override
    public String multiInstanceFailedWritingResListXml() {
        return "写入资源配置文件ResList.xml失败";
    }

    @Override
    public String multiInstanceFailedWritingConfigXml() {
        return "写入资源配置文件config.xml失败";
    }

    @Override
    public String multiInstanceCannotMakeLink() {
        return "创建链接文件失败。如果游戏客户端目录曾移动过，你需要手动删除多开启动器目录下的Client目录链接";
    }

    @Override
    public String multiInstanceResolvingFailed() {
        return "域名解析失败";
    }

    @Override
    public String multiInstanceLaunchProxyServerFailed() {
        return "启动代理服务失败，请确保本机443端口未被占用";
    }

    @Override
    public String multiInstanceLaunchDNSHijackerFailed() {
        return "启动DNS劫持失败，请查看日志并联系开发者";
    }

    @Override
    public String multiInstancesOpenBrowserForTutorialFailed(String url) {
        return "打开浏览器失败，你可以手动输入该URL查看多开教学：" + url + "，也可尝试直接粘贴，程序已尝试将该url放到你的剪贴板里";
    }

    @Override
    public String multiInstanceTips() {
        return "如果提示游戏需要更新，请不要点击更新。退出游戏并重新解压预制的客户端压缩包，然后重新使用本工具启动即可";
    }

    @Override
    public String multiInstanceConfirmAndDisableTipsButton() {
        return "我已知晓，后续不再提示";
    }

    @Override
    public String multiInstanceSingleLaunchButton() {
        return "启动";
    }

    @Override
    public String selectGameLocationDescriptionWithoutAutoSearching() {
        return "请选择游戏路径，手动选择gameLauncher.exe所在路径";
    }

    @Override
    public String failedSavingCaCertFile() {
        return "保存CA证书失败";
    }

    @Override
    public String initRobotFailed() {
        return "初始化Robot失败";
    }

    @Override
    public String readAssistantConfigFailed() {
        return "读取助手配置文件失败";
    }

    @Override
    public String readInputConfigFailed() {
        return "读取输入配置失败";
    }

    @Override
    public String writeAssistantConfigFailed() {
        return "写入助手配置文件失败";
    }

    @Override
    public String confirm() {
        return "确定";
    }

    @Override
    public String messageHelperDesc() {
        return """
            对于某些中文输入法，按下数字、空格键时，幻塔客户端可能会丢失光标、无法正常输入中文。
            这个小工具就是为了应对这种场景而设计的。
            操作说明：
              1. 当在游戏中按下Enter时，该工具会自动移至最前，并闪烁以进行提示
              2. 在工具的输入框中输入内容并按下Enter，内容会被转移到剪贴板里，可以到游戏中进行粘贴
              3. 在输入框中按上/下方向键，可以滚动浏览最近的20条历史消息
              4. 按住本工具输入框以外的部分，可以拖拽移动窗口
              5. 使用Ctrl-Z可以回退最近一次的修改，注意仅可回退一次
              6. 点击界面中的按钮可自动填充颜色代码，请注意总长度限制
            """;
    }

    @Override
    public String messageHelperColorButton(String name) {
        return switch (name) {
            case "red" -> "红字";
            case "blue" -> "蓝字";
            case "white" -> "白字";
            case "gold" -> "黄字";
            case "purple" -> "紫字";
            case "green" -> "绿字";
            case "green_bold" -> "加粗绿字";
            default -> name;
        };
    }

    @Override
    public String messageHelperItemButton() {
        return "展示物品";
    }

    @Override
    public String scrollLogCheckBoxDesc() {
        return "滚动显示最新日志";
    }

    @Override
    public String gplAlert(String url) {
        return "本软件通过GPLv2协议开源，根据协议，分发时需要提供相应的源代码。\n" +
               "本软件源码仓库位于:" + url + "。具体信息可在[关于]页中查看。";
    }

    @Override
    public String confirmAndDisableGPLAlert() {
        return "我已了解，后续启动时不再提示";
    }

    @Override
    public String authorGameAccount() {
        return "游戏账号";
    }

    @Override
    public String authorContribution() {
        return "贡献内容";
    }

    @Override
    public String statusIndicatorDesc() {
        return "状态指示器标明了当前幻塔PC助手的运行状态，包括各工具的启用状态、宏的启用状态等。";
    }

    @Override
    public String statusIndicatorTitle() {
        return "状态指示器";
    }

    @Override
    public String statusComponentModule() {
        return "模块";
    }

    @Override
    public String statusComponentTool() {
        return "工具";
    }

    @Override
    public String statusComponentMacro() {
        return "宏";
    }

    @Override
    public String yueXingChuanJuShuiSkill() {
        return "聚水";
    }

    @Override
    public String yueXingChuanYongJuanSkill() {
        return "涌卷";
    }

    @Override
    public String yueXingChuanTaoYaSkill() {
        return "涛压";
    }

    @Override
    public String yueXingChuanWoXuanSkill() {
        return "涡旋";
    }

    @Override
    public String yueXingChuanYuGuSkill() {
        return "潏梏";
    }

    @Override
    public String yueXingChuanZiQuanSkill() {
        return "滋泉";
    }

    @Override
    public String yueXingChuanSanLiuSkillCoolDownDesc(String name) {
        return name + "冷却时间";
    }

    @Override
    public String yueXingChuanSanLiuSkillBuffDesc(String name) {
        return name + "效果持续时间";
    }

    @Override
    public String resetSceneDesc() {
        return "重置功能可以快速清除运行中留下的文件，当助手运行异常时可以使用。";
    }

    @Override
    public String resetSceneResetConfigButton() {
        return "重置配置文件";
    }

    @Override
    public String resetSceneResetConfigSucceeded() {
        return "重置成功，即将退出助手，请手动重新打开";
    }

    @Override
    public String messageMonitorNicChooserTitle() {
        return "选择监控的网卡";
    }

    @Override
    public String messageMonitorServerHostTitle() {
        return "输入幻塔服务器IP地址";
    }

    @Override
    public String messageMonitorWordsTitle() {
        return "输入要监控的文字，多个用英文逗号隔开";
    }

    @Override
    public String messageMonitorStartBtn() {
        return "启动";
    }

    @Override
    public String messageMonitorStopBtn() {
        return "停止";
    }

    @Override
    public String messageMonitorServerHostDefaultValue() {
        return "39.96.163.203";
    }

    @Override
    public String messageMonitorWordsDefaultValue() {
        return "玄鸦, 丫丫, 鸭脖, 乌鸦";
    }

    @Override
    public String messageMonitorAlreadyStartedAlert() {
        return "消息监控已启动";
    }

    @Override
    public String messageMonitorNoNetifSelectedAlert() {
        return "没有选定待监控的网卡";
    }

    @Override
    public String messageMonitorInvalidServerHostAlert(String str) {
        return "服务器地址不是正确的IP地址: " + str;
    }

    @Override
    public String messageMonitorEmptyWordsListAlert() {
        return "监控文字为空";
    }

    @Override
    public String messageMonitorNotificationTitle() {
        return "幻塔PC助手 - 聊天消息监控";
    }

    @Override
    public String messageMonitorCapFailedAlert() {
        return "监控异常！";
    }

    @Override
    public String messageMonitorChannel(ChatChannel channel) {
        if (channel == null) {
            return "所有频道";
        }
        return switch (channel) {
            case WORLD -> "世界";
            case GUILD -> "公会";
            case TEAM -> "组队";
            case COOP -> "协力";
        };
    }

    @Override
    public String clearHostsFailed() {
        return "清理hosts文件失败，你需要手动清理，或者重复执行退出操作以便重新清理，否则将导致网络异常";
    }

    @Override
    public String loadingFailedErrorMessage(LoadingItem failedItem) {
        return "加载失败。失败项为：" + failedItem.name;
    }

    @Override
    public String skipAnimation() {
        return "跳过";
    }

    @Override
    public String newCriticalVersionAvailable(String ver) {
        return "有新的关键性更新：" + ver + ", 可以前往 github.com/wkgcass/hotta-pc-assistant 获取";
    }

    @Override
    public String alertInfoTitle() {
        return "信息";
    }

    @Override
    public String alertWarningTitle() {
        return "警告";
    }

    @Override
    public String alertErrorTitle() {
        return "错误";
    }

    @Override
    public String cannotFindAnyDisplay() {
        return "找不到显示器";
    }

    @Override
    public String stacktraceAlertTitle() {
        return "异常";
    }

    @Override
    public String stacktraceAlertHeaderText() {
        return "检测到抛出异常";
    }

    @Override
    public String stacktraceAlertLabel() {
        return "异常栈为：";
    }

    @Override
    public String keyChooserLeftMouseButton() {
        return "鼠标左键";
    }

    @Override
    public String keyChooserRightMouseButton() {
        return "鼠标右键";
    }

    @Override
    public String keyChooserMiddleMouseButton() {
        return "鼠标中键";
    }

    @Override
    public String keyChooserWheelScrollUpButton() {
        return "滚轮向上";
    }

    @Override
    public String keyChooserWheelScrollDownButton() {
        return "滚轮向下";
    }

    @Override
    public String emptyTableLabel() {
        return "没有数据";
    }

    @Override
    public String cancelButton() {
        return "取消";
    }

    @Override
    public String alertOkButton() {
        return "确认";
    }

    @Override
    public String confirmationYesButton() {
        return "是";
    }

    @Override
    public String confirmationNoButton() {
        return "否";
    }

    @Override
    public String sceneGroupPreCheckShowSceneFailed() {
        return "前置检查未通过";
    }

    @Override
    public String loadingCanceled() {
        return "加载进程已取消";
    }

    @Override
    public String globalScreenRegisterFailed() {
        return "启用GlobalScreen失败";
    }

    @Override
    public String title() {
        return toolNameXBox();
    }

    @Override
    public String agentAddressLabel() {
        return "Agent地址";
    }

    @Override
    public String disconnectButton() {
        return "断开";
    }

    @Override
    public String connectButton() {
        return "连接";
    }

    @Override
    public String disconnectedAlert() {
        return "与Agent断开连接";
    }

    @Override
    public String planLabel() {
        return "配置";
    }

    @Override
    public String savePlanButton() {
        return "保存";
    }

    @Override
    public String deletePlanButton() {
        return "删除";
    }

    @Override
    public String showTableButton() {
        return "显示当前设定";
    }

    @Override
    public String savingConfigurationFailed() {
        return "保存配置失败";
    }

    @Override
    public String applyConfButton() {
        return "确定";
    }

    @Override
    public String enableKeyPress() {
        return "按键";
    }

    @Override
    public String enableMouseMove() {
        return "鼠标移动";
    }

    @Override
    public String enableMouseWheel() {
        return "鼠标滚轮";
    }

    @Override
    public String enableFN() {
        return "功能键";
    }

    @Override
    public String enableFnInput() {
        return "功能键输入";
    }

    @Override
    public String invalidKey() {
        return "不支持该键位";
    }

    @Override
    public String invalidMoveX() {
        return "鼠标移动横坐标配置错误";
    }

    @Override
    public String invalidMoveY() {
        return "鼠标移动纵坐标配置错误";
    }

    @Override
    public String invalidMouseWheel() {
        return "鼠标滚轮配置错误";
    }

    @Override
    public String cannotOverwritePrebuiltPlan() {
        return "不可以覆盖预设配置";
    }

    @Override
    public String cannotDeletePlan() {
        return "无法删除该配置";
    }

    @Override
    public String failedToStart() {
        return "启动失败";
    }

    @Override
    public String tableColName() {
        return "名称";
    }

    @Override
    public String back() {
        return "返回";
    }

    @Override
    public String patchManagerEditBtn() {
        return "修改";
    }

    @Override
    public String patchManagerRemoveBtn() {
        return "删除";
    }

    @Override
    public String patchManagerReloadBtn() {
        return "重新读取";
    }

    @Override
    public String patchManagerOpenFolderBtn() {
        return "打开目录";
    }

    @Override
    public String patchManagerEnabledCol() {
        return "启用";
    }

    @Override
    public String patchManagerNameCol() {
        return "名称";
    }

    @Override
    public String patchManagerCNCol() {
        return "国服";
    }

    @Override
    public String patchManagerGlobalCol() {
        return "国际服";
    }

    @Override
    public String patchManagerDescCol() {
        return "描述";
    }

    @Override
    public String patchManagerLoadAfterCol() {
        return "前置加载项";
    }

    @Override
    public String patchManagerDependsOnCol() {
        return "依赖项";
    }

    @Override
    public String patchManagerOkBtn() {
        return "确认";
    }

    @Override
    public String patchManagerDepNotExist(String colName, String parentItemName) {
        return colName + "不存在: " + parentItemName;
    }

    @Override
    public String patchManagerDepCircular(String colName, ArrayList<String> newPath) {
        return colName + "存在循环依赖: " + newPath;
    }

    @Override
    public String patchManagerAlertInvalidConfigTitle() {
        return "配置文件错误";
    }

    @Override
    public String patchManagerAlertInvalidConfigContent(String name) {
        return "配置文件 " + name + " 错误，将对其使用空配置";
    }

    @Override
    public String patchManagerAlertFailedToWriteConfigTitle() {
        return "存储配置失败";
    }

    @Override
    public String patchManagerAlertFailedToWriteConfigContent() {
        return "存储配置失败，请查看日志以获取更多信息";
    }

    @Override
    public String patchManagerAlertHasDependedCannotDelete(String name, Set<String> set) {
        return "存在依赖" + name + "的补丁: " + set;
    }

    @Override
    public String patchManagerConfirmRemove(String name) {
        return "你确定要删除 " + name + " 吗？磁盘上的文件也将被删除！";
    }

    @Override
    public String applyPatchLoadingStageTitle() {
        return "正在应用补丁...";
    }

    @Override
    public String applyPatchLoadingPreparePatchDirectory() {
        return "准备补丁目录";
    }

    @Override
    public String applyPatchLoadingPrepareSigFile() {
        return "准备签名文件";
    }
}
