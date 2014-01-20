package net.sf.gazpachosurvey.dto.support;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public abstract class AbstractIdentifiableDTO implements Identifiable {

    private static final long serialVersionUID = 2830103041683278252L;

    private Integer id;

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof Identifiable) {
            final Identifiable other = (Identifiable) obj;
            if (!isNew()) {
                return new EqualsBuilder().append(getId(), other.getId()).isEquals();
            } else {
                return EqualsBuilder.reflectionEquals(this, obj);
            }
        } else {
            return false;
        }
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public int hashCode() {
        if (!isNew()) {
            return new HashCodeBuilder().append(getId()).toHashCode();
        } else {
            return HashCodeBuilder.reflectionHashCode(this);
        }
    }

    @Override
    public boolean isNew() {
        return null == getId();
    }

    public void setId(final Integer id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("Entity of type %s with id: %s", this.getClass().getName(), getId());
    }

}