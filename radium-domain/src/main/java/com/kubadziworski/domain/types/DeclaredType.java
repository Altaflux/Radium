package com.kubadziworski.domain.types;

import java.util.List;


public interface DeclaredType extends ComponentType {

    List<TypeReference> getSuperTypes();

    Modifiers getModifiers();

    String getPackageName();

    List<RField> getFields();

    List<RFunctionSignature> getFunctionSignatures();

    List<RFunctionSignature> getConstructorSignatures();


}
