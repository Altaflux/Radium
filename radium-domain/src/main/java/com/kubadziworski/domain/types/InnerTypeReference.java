package com.kubadziworski.domain.types;

/**
 * Created by pablo.lozano on 4/11/2017.
 */
public interface InnerTypeReference extends ParameterizedTypeReference {


    ParameterizedTypeReference getOuter();
}
