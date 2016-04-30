package com.kubadziworski.domain.type;

/**
 * Created by kuba on 28.03.16.
 */
public interface Type {
    String getName();
    Class<?> getTypeClass();
    String getDescriptor();
    String getInternalName();
    int getLoadVariableOpcode();
    int getStoreVariableOpcode();
    int getReturnOpcode();
    int getAddOpcode();
    int getSubstractOpcode();
    int getMultiplyOpcode();
    int getDividOpcode();
}
