package net.cassite.hottapcassistant.entity;

import io.vproxy.vfx.entity.input.InputData;
import io.vproxy.vfx.ui.table.RowInformer;
import io.vproxy.vfx.ui.table.RowInformerAware;
import net.cassite.hottapcassistant.status.Status;
import net.cassite.hottapcassistant.status.StatusComponent;
import net.cassite.hottapcassistant.status.StatusEnum;
import net.cassite.hottapcassistant.status.StatusManager;
import vjson.JSON;
import vjson.deserializer.rule.*;
import vjson.util.ObjectBuilder;

import java.util.ArrayList;
import java.util.List;

public class AssistantMacroData extends InputData implements RowInformerAware {
    public boolean enabled;
    public String name;
    public AssistantMacroType type = AssistantMacroType.NORMAL;
    public int loopLimit = 0;
    public List<AssistantMacroStep> steps;
    public boolean isSystemPreBuilt;

    private AssistantMacroStatus status = AssistantMacroStatus.STOPPED;
    private RowInformer rowInformer;

    public static final Rule<AssistantMacroData> rule = new ObjectRule<>(AssistantMacroData::new, (ObjectRule<InputData>) InputData.rule)
        .put("enabled", (o, it) -> o.enabled = it, BoolRule.get())
        .put("name", (o, it) -> o.name = it, StringRule.get())
        .put("type", (o, it) -> o.type = AssistantMacroType.valueOf(it), StringRule.get())
        .put("loopLimit", (o, it) -> o.loopLimit = it, IntRule.get())
        .put("steps", (o, it) -> o.steps = it,
            new ArrayRule<ArrayList<AssistantMacroStep>, AssistantMacroStep>(ArrayList::new, ArrayList::add, AssistantMacroStep.rule))
        .put("isSystemPreBuilt", (o, it) -> o.isSystemPreBuilt = it, BoolRule.get());

    @Override
    public JSON.Object toJson() {
        return new ObjectBuilder(super.toJson())
            .put("enabled", enabled)
            .put("name", name)
            .put("type", type.name())
            .put("loopLimit", loopLimit)
            .putArray("steps", a -> steps.forEach(e -> a.addInst(e.toJson())))
            .put("isSystemPreBuilt", isSystemPreBuilt)
            .build();
    }

    public void exec() {
        for (var s : steps) {
            if (s instanceof AssistantMacroStep.SafePoint && status != AssistantMacroStatus.RUNNING) {
                break;
            }
            s.exec();
        }
    }

    public AssistantMacroStatus getStatus() {
        return status;
    }

    public void setStatus(AssistantMacroStatus status) {
        this.status = status;
        rowInformer.informRowUpdate();
        if (status == AssistantMacroStatus.STOPPED) {
            StatusManager.get().removeStatus(new Status(name, StatusComponent.MACRO, StatusEnum.STOPPED));
        } else {
            StatusManager.get().updateStatus(new Status(name, StatusComponent.MACRO,
                (status == AssistantMacroStatus.RUNNING
                    ? StatusEnum.RUNNING
                    : StatusEnum.STOPPING)
            ));
        }
    }

    @Override
    public void setRowInformer(RowInformer rowInformer) {
        this.rowInformer = rowInformer;
    }
}
