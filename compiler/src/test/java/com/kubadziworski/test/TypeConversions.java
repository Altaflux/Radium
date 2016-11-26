package com.kubadziworski.test;

import com.kubadziworski.domain.type.JavaClassType;
import com.kubadziworski.domain.type.Type;
import com.kubadziworski.domain.type.intrinsic.AnyType;
import com.kubadziworski.domain.type.intrinsic.primitive.IntType;
import com.kubadziworski.domain.type.intrinsic.primitive.LongType;
import com.kubadziworski.domain.type.intrinsic.TypeProjection;
import com.kubadziworski.util.PrimitiveTypesWrapperFactory;
import com.kubadziworski.util.TypeChecker;
import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Test;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.InstructionAdapter;

import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;


public class TypeConversions {

    @Test
    public void testPrimitiveConversions() {

        IntType objectInt = new IntType(false);
        IntType primitiveInt = new IntType(true);
        InstructionAdapter visitor = EasyMock.createMock(InstructionAdapter.class);
        visitor.invokevirtual("java/lang/Number", "intValue", "()I", false);
        EasyMock.expectLastCall().once();
        EasyMock.replay(visitor);
        PrimitiveTypesWrapperFactory.coerce(primitiveInt, objectInt, visitor);

    }

    @Test
    public void testBoxConversions() {
        IntType objectInt = new IntType(false);
        IntType primitiveInt = new IntType(true);
        InstructionAdapter visitor = EasyMock.createMock(InstructionAdapter.class);
        visitor.invokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        EasyMock.expectLastCall().once();
        EasyMock.replay(visitor);
        PrimitiveTypesWrapperFactory.coerce(objectInt, primitiveInt, visitor);
    }

    @Test
    public void testPrimitiveToObject() {
        JavaClassType objectInt = new JavaClassType("java.lang.Object");
        IntType primitiveInt = new IntType(true);
        InstructionAdapter visitor = EasyMock.createMock(InstructionAdapter.class);
        visitor.invokestatic("java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;", false);
        EasyMock.expectLastCall().once();
        EasyMock.replay(visitor);
        PrimitiveTypesWrapperFactory.coerce(objectInt, primitiveInt, visitor);
    }

    @Test
    public void testPrimitiveWidening() {
        LongType objectInt = new LongType(true);
        IntType primitiveInt = new IntType(true);
        MethodVisitor visitor = EasyMock.createMock(MethodVisitor.class);
        visitor.visitInsn(Opcodes.I2L);
        EasyMock.expectLastCall().once();
        EasyMock.replay(visitor);
        PrimitiveTypesWrapperFactory.coerce(objectInt, primitiveInt, new InstructionAdapter(visitor));
    }


    @Test
    public void testPrimitiveWideningToObject() {
        LongType objectInt = new LongType(false);
        IntType primitiveInt = new IntType(true);
        MethodVisitor visitor = EasyMock.createMock(MethodVisitor.class);
        visitor.visitInsn(Opcodes.I2L);
        EasyMock.expectLastCall().once();

        visitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;", false);
        EasyMock.expectLastCall().once();

        EasyMock.replay(visitor);
        PrimitiveTypesWrapperFactory.coerce(objectInt, primitiveInt, new InstructionAdapter(visitor));
    }

    @Test
    public void testObjectWideningToPrimitive() {
        LongType objectInt = new LongType(true);
        IntType primitiveInt = new IntType(false);
        MethodVisitor visitor = EasyMock.createMock(MethodVisitor.class);

        visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
        EasyMock.expectLastCall().once();

        visitor.visitInsn(Opcodes.I2L);
        EasyMock.expectLastCall().once();


        EasyMock.replay(visitor);
        PrimitiveTypesWrapperFactory.coerce(objectInt, primitiveInt, new InstructionAdapter(visitor));
    }

//    @Test
//    public void testNotWideningToLong() {
//        LongType objectInt = new LongType(true);
//        IntType primitiveInt = new IntType(true);
//        MethodVisitor visitor = EasyMock.createMock(MethodVisitor.class);
////
////        visitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Number", "intValue", "()I", false);
////        EasyMock.expectLastCall().once();
////
////        visitor.visitInsn(Opcodes.I2L);
////        EasyMock.expectLastCall().once();
//
//
//        EasyMock.replay(visitor);
//        PrimitiveTypesWrapperFactory.coerce(primitiveInt, objectInt, new InstructionAdapter(visitor));
//    }

    @Test
    public void testNullabilityPairOfBoxed() {
        Type typeAny = new TypeProjection(new IntType(false), Type.Nullability.NOT_NULL);
        Type nullableAny = new TypeProjection(new IntType(false), Type.Nullability.NULLABLE);

        Assert.assertEquals("Non null type cannot inherit from nullable type", -1, nullableAny.inheritsFrom(typeAny));
        Assert.assertEquals("Nullable type can inherit from non null type", 0, typeAny.inheritsFrom(nullableAny));
    }

    @Test
    public void testNullabilityPair() {
        Type typeAny = AnyType.INSTANCE;
        TypeProjection nullableAny = new TypeProjection(AnyType.INSTANCE, Type.Nullability.NULLABLE);

        Assert.assertEquals("Non null type cannot inherit from nullable type", -1, nullableAny.inheritsFrom(typeAny));
        Assert.assertEquals("Nullable type can inherit from non null type", 0, typeAny.inheritsFrom(nullableAny));
    }


    @Test
    public void testEqualityBetweenPrimitives() {
        IntType objectInt = new IntType(false);
        IntType primitive = new IntType(true);

        Assert.assertEquals(0, objectInt.inheritsFrom(primitive));
        Assert.assertEquals(0, primitive.inheritsFrom(objectInt));

        Type objectIntProjected = new TypeProjection(new IntType(false), Type.Nullability.NOT_NULL);
        Assert.assertEquals(0, objectIntProjected.inheritsFrom(primitive));
        Assert.assertEquals(0, primitive.inheritsFrom(objectIntProjected));

        Assert.assertTrue(TypeChecker.isInt(objectIntProjected));
        Assert.assertTrue(TypeChecker.isInt(primitive));
        Assert.assertTrue(TypeChecker.isInt(objectInt));

        Assert.assertFalse(TypeChecker.isInt(new TypeProjection(new IntType(false), Type.Nullability.NULLABLE)));
    }

}
