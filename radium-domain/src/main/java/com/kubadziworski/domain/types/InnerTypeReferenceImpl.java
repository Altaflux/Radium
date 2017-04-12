package com.kubadziworski.domain.types;

import java.util.List;

/**
 * Created by pablo.lozano on 4/12/2017.
 */
public class InnerTypeReferenceImpl extends ParameterizedTypeReferenceImpl implements InnerTypeReference {

    private final ParameterizedTypeReference outer;

    public InnerTypeReferenceImpl(RType rType, List<TypeReference> arguments, ParameterizedTypeReference outer) {
        super(rType, arguments);
        this.outer = outer;
    }

    @Override
    public ParameterizedTypeReference getOuter() {
        return outer;
    }
}
