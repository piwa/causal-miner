package at.ac.wuwien.causalminer.transformer.model;

import java.util.Objects;

public abstract class AbstractNode implements INode {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        INode node = (INode) o;
        return getId() == node.getId() &&
                Objects.equals(getLabel(), node.getLabel()) &&
//                Objects.equals(getInstanceIds(), node.getInstanceIds()) &&
//                Objects.equals(getActivityCreationTime(), node.getActivityCreationTime()) &&
//                Objects.equals(getActivityChangeDate(), node.getActivityChangeDate()) &&
                Objects.equals(getEventType(), node.getEventType());
    }

    @Override
    public int hashCode() {
//        return Objects.hash(getId(), getLabel(), getInstanceIds(), getActivityCreationTime(), getActivityChangeDate(), getEventType());
//        return Objects.hash(getId(), getLabel(), getActivityCreationTime(), getActivityChangeDate(), getEventType());
        return Objects.hash(getId(), getLabel(), getEventType());
    }

}
