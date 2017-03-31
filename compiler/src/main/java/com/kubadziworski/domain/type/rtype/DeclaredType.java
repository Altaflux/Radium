package com.kubadziworski.domain.type.rtype;

import com.kubadziworski.domain.Modifiers;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.scope.FunctionSignature;

import java.util.List;

/**
 * Created by plozano on 3/29/2017.
 */
public interface DeclaredType extends ComponentType {

    List<RType> getSuperTypes();

    Modifiers getModifiers();

    String getPackageName();

    List<Field> getFields();

    List<FunctionSignature> getFunctionSignatures();

    List<FunctionSignature> getConstructorSignatures();


}
