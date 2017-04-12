package com.kubadziworski.domain.types;

import com.kubadziworski.domain.types.builder.MemberBuilder;

import java.util.List;
import java.util.stream.Collectors;


public class WildcardTypeReferenceImpl extends TypeReferenceImpl implements WildcardTypeReference {

    private final List<Constraint> constraints;

    public WildcardTypeReferenceImpl(List<MemberBuilder<Constraint, ConstraintOwner>> constraints) {
        super();
        this.constraints = constraints
                .stream().map(constraintConstraintOwnerMemberBuilder -> constraintConstraintOwnerMemberBuilder
                        .build(this)).collect(Collectors.toList());
    }

    public RType getType() {
        return null;
    }

    @Override
    public List<Constraint> getConstraints() {
        return constraints;
    }
}
