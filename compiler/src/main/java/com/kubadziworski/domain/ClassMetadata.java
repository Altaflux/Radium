package com.kubadziworski.domain;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Created by plozano on 2/24/2017.
 */
public class ClassMetadata implements Serializable {

    public List<FieldMeta> fieldMetas;
    public List<MethodMeta> methodMetas;

    public List<FieldMeta> getFieldMetas() {
        return fieldMetas;
    }

    public void setFieldMetas(List<FieldMeta> fieldMetas) {
        this.fieldMetas = fieldMetas;
    }

    public List<MethodMeta> getMethodMetas() {
        return methodMetas;
    }

    public void setMethodMetas(List<MethodMeta> methodMetas) {
        this.methodMetas = methodMetas;
    }

    public static class FieldMeta implements Serializable {
        public String name;
        public Set<Modifier> modifiers;
        public TypeMeta typeMeta;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Set<Modifier> getModifiers() {
            return modifiers;
        }

        public void setModifiers(Set<Modifier> modifiers) {
            this.modifiers = modifiers;
        }

        public TypeMeta getTypeMeta() {
            return typeMeta;
        }

        public void setTypeMeta(TypeMeta typeMeta) {
            this.typeMeta = typeMeta;
        }
    }

    public static class MethodMeta implements Serializable {
        public String name;
        public TypeMeta returnType;
        public Set<Modifier> modifiers;
        public List<MethodMeta.ParamMeta> params;
        public boolean constructor;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public TypeMeta getReturnType() {
            return returnType;
        }

        public void setReturnType(TypeMeta returnType) {
            this.returnType = returnType;
        }

        public Set<Modifier> getModifiers() {
            return modifiers;
        }

        public void setModifiers(Set<Modifier> modifiers) {
            this.modifiers = modifiers;
        }

        public List<ParamMeta> getParams() {
            return params;
        }

        public void setParams(List<ParamMeta> params) {
            this.params = params;
        }

        public boolean isConstructor() {
            return constructor;
        }

        public void setConstructor(boolean constructor) {
            this.constructor = constructor;
        }

        public static class ParamMeta implements Serializable {
            public String name;
            public TypeMeta typeMeta;
            public boolean hasDefault;
            public int index = 0;

            public int getIndex() {
                return index;
            }

            public void setIndex(int index) {
                this.index = index;
            }

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public TypeMeta getTypeMeta() {
                return typeMeta;
            }

            public void setTypeMeta(TypeMeta typeMeta) {
                this.typeMeta = typeMeta;
            }

            public boolean isHasDefault() {
                return hasDefault;
            }

            public void setHasDefault(boolean hasDefault) {
                this.hasDefault = hasDefault;
            }
        }
    }

    public static class TypeMeta implements Serializable {
        public boolean nullable;

        public boolean isNullable() {
            return nullable;
        }

        public void setNullable(boolean nullable) {
            this.nullable = nullable;
        }
    }
}

