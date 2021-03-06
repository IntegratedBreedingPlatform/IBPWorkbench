
package org.generationcp.ibpworkbench.cross.study.adapted.main.pojos;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.generationcp.ibpworkbench.cross.study.constants.CharacterTraitCondition;
import org.generationcp.ibpworkbench.cross.study.constants.TraitWeight;
import org.generationcp.middleware.domain.h2h.TraitInfo;

import java.io.Serializable;
import java.util.List;

public class CharacterTraitFilter implements Serializable {

	private static final long serialVersionUID = -1400001149797183987L;

	private TraitInfo traitInfo;
	private CharacterTraitCondition condition;
	private List<String> limits;
	private TraitWeight priority;

	public CharacterTraitFilter(TraitInfo traitInfo, CharacterTraitCondition condition, List<String> limits, TraitWeight priority) {
		super();
		this.traitInfo = traitInfo;
		this.condition = condition;
		this.limits = limits;
		this.priority = priority;
	}

	public TraitInfo getTraitInfo() {
		return this.traitInfo;
	}

	public void setTraitInfo(TraitInfo traitInfo) {
		this.traitInfo = traitInfo;
	}

	public CharacterTraitCondition getCondition() {
		return this.condition;
	}

	public void setCondition(CharacterTraitCondition condition) {
		this.condition = condition;
	}

	public List<String> getLimits() {
		return this.limits;
	}

	public void setLimits(List<String> limits) {
		this.limits = limits;
	}

	public TraitWeight getPriority() {
		return this.priority;
	}

	public void setPriority(TraitWeight priority) {
		this.priority = priority;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof CharacterTraitFilter)) {
			return false;
		}

		CharacterTraitFilter rhs = (CharacterTraitFilter) obj;
		return new EqualsBuilder().appendSuper(super.equals(obj)).append(this.traitInfo, rhs.traitInfo).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder(17, 37).append(this.traitInfo).toHashCode();
	}
}
