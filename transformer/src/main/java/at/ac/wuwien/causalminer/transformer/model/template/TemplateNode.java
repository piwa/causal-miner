package at.ac.wuwien.causalminer.transformer.model.template;

import lombok.Getter;

import java.util.Objects;
import java.util.UUID;

@Getter
public class TemplateNode {

    private final UUID id = UUID.randomUUID();

    private String eventType;

    public TemplateNode(String eventType) {
        this.eventType = eventType;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TemplateNode that = (TemplateNode) o;
//        return Objects.equals(getId(), that.getId()) &&
//                Objects.equals(getEventType(), that.getEventType());
        return Objects.equals(getEventType(), that.getEventType());
    }

    @Override
    public int hashCode() {
//        return Objects.hash(getId(), getEventType());
        return Objects.hash(getEventType());
    }
}