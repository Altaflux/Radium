package com.kubadziworski.domain.type.rtype;

import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.scope.RField;
import com.kubadziworski.domain.scope.RFunctionSignature;

import java.util.List;


public interface DeclaredType extends ComponentType {

    List<TypeReference> getSuperTypes();

    Modifiers getModifiers();

    String getPackageName();

    List<RField> getFields();

    List<RFunctionSignature> getFunctionSignatures();

    List<RFunctionSignature> getConstructorSignatures();


}
