package com.kubadziworski.bytecodegeneration.intrinsics;

import com.kubadziworski.domain.node.expression.FieldReference;
import com.kubadziworski.domain.scope.CallableDescriptor;
import com.kubadziworski.domain.scope.CallableMember;
import com.kubadziworski.domain.scope.Field;
import com.kubadziworski.domain.type.Type;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class IntrinsicMap {

    private final Map<Key, IntrinsicMethod> intrinsicMap = new HashMap<>();

    public void registerIntrinsicMethod(CallableDescriptor member, IntrinsicMethod method, int arity) {
        intrinsicMap.put(callableDescriptorToKey(member, arity), method);
    }

    public void registerIntrinsicMethod(CallableDescriptor member, IntrinsicMethod method, Type owner) {
        intrinsicMap.put(callableDescriptorToKey(member, owner), method);
    }

    private Key callableDescriptorToKey(CallableDescriptor descriptor, int arity) {
        int parameterCount = 0;
        if (descriptor instanceof Field) {
            parameterCount = -1;
        } else {
            descriptor.getParameters().size();
        }
        return new Key(parameterCount, descriptor.getName(), descriptor.getType(), descriptor.getOwner(), arity);
    }

    private Key callableDescriptorToKey(CallableDescriptor descriptor, Type owner) {
        int parameterCount = 0;
        if (descriptor instanceof Field) {
            parameterCount = -1;
        } else {
            descriptor.getParameters().size();
        }
        return new Key(parameterCount, descriptor.getName(), descriptor.getType(), owner, 0);
    }

    private Key callableMemberToKey(CallableMember descriptor) {
        int parameterCount = 0;
        if (descriptor instanceof FieldReference) {
            parameterCount = -1;
        } else {
            descriptor.getArguments().size();
        }
        return new Key(parameterCount, descriptor.getName(), descriptor.getType(), descriptor.getOwner().getType());
    }

    public Optional<IntrinsicMethod> getIntrinsicMethod(CallableMember member) {
        Key key = callableMemberToKey(member);
        Optional<Map.Entry<Key, IntrinsicMethod>> intrinsicMethod = intrinsicMap.entrySet().stream().filter(key1 -> key1.getKey().inheritsEqual(key))
                .findAny();
        return intrinsicMethod.map(Map.Entry::getValue);
    }

    private static class Key {
        int parameterCount;
        String identifier;
        Type type;
        int arity;
        Type ownerType;

        private Key(int parameterCount, String identifier, Type type, Type ownerType) {
            this.parameterCount = parameterCount;
            this.identifier = identifier;
            this.type = type;
            this.ownerType = ownerType;
            this.arity = 0;
        }

        private Key(int parameterCount, String identifier, Type type, Type ownerType, int arity) {
            this.parameterCount = parameterCount;
            this.identifier = identifier;
            this.type = type;
            this.ownerType = ownerType;
            this.arity = arity;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            Key key = (Key) o;

            if (parameterCount != key.parameterCount) return false;
            if (!identifier.equals(key.identifier)) return false;
            if (!type.equals(key.type)) return false;
            return ownerType.equals(key.ownerType);
        }

        boolean inheritsEqual(Key key) {
            if (parameterCount != key.parameterCount) return false;
            if (!identifier.equals(key.identifier)) return false;
            if (!type.equals(key.type)) return false;

            return key.ownerType.inheritsFrom(ownerType) > -1;
        }

        @Override
        public int hashCode() {
            int result = parameterCount;
            result = 31 * result + identifier.hashCode();
            result = 31 * result + type.hashCode();
            result = 31 * result + ownerType.hashCode();
            return result;
        }
    }

}
