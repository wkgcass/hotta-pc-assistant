package net.cassite.hottapcassistant.status;

import java.util.Objects;

public class Status {
    public final String componentName;
    public final StatusComponent component;
    public final StatusEnum status;

    public Status(String componentName, StatusComponent component, StatusEnum status) {
        this.componentName = componentName;
        this.component = component;
        this.status = status;
    }

    @Override
    public String toString() {
        return "Status{" +
               "componentName='" + componentName + '\'' +
               ", component=" + component +
               ", status=" + status +
               '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Status status = (Status) o;

        if (!Objects.equals(componentName, status.componentName))
            return false;
        return component == status.component;
    }

    @Override
    public int hashCode() {
        int result = componentName != null ? componentName.hashCode() : 0;
        result = 31 * result + (component != null ? component.hashCode() : 0);
        return result;
    }
}
