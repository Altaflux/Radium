package com.kubadziworski.domain;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kubadziworski.domain.node.expression.Parameter;
import com.kubadziworski.domain.type.Type;
import org.apache.commons.io.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class MetaDataBuilder {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ClassMetadata classMetadata(ClassDeclaration classDeclaration) {
        List<ClassMetadata.MethodMeta> methodMetadataList = classDeclaration.getMethods().stream()
                .map(function -> {
                    ClassMetadata.MethodMeta methodMetadata = new ClassMetadata.MethodMeta();
                    methodMetadata.modifiers = function.getModifiers().getModifiers();

                    methodMetadata.params = IntStream.range(0, function.getParameters().size()).mapToObj(index -> {
                        Parameter parameter = function.getParameters().get(index);
                        ClassMetadata.MethodMeta.ParamMeta param = new ClassMetadata.MethodMeta.ParamMeta();
                        param.hasDefault = parameter.getDefaultValue().isPresent();
                        param.name = parameter.getName();
                        ClassMetadata.TypeMeta typeMeta = new ClassMetadata.TypeMeta();
                        typeMeta.nullable = parameter.getType().isNullable().equals(Type.Nullability.NULLABLE);
                        param.typeMeta = typeMeta;
                        param.index = index;
                        return param;
                    }).collect(Collectors.toList());

                    methodMetadata.name = function.getName();
                    methodMetadata.constructor = function instanceof Constructor;

                    ClassMetadata.TypeMeta typeMeta = new ClassMetadata.TypeMeta();
                    typeMeta.nullable = function.getReturnType().isNullable().equals(Type.Nullability.NULLABLE);
                    methodMetadata.returnType = typeMeta;
                    return methodMetadata;
                }).collect(Collectors.toList());

        List<ClassMetadata.FieldMeta> fieldMetas = classDeclaration.getFields().stream()
                .map(field -> {
                    ClassMetadata.FieldMeta fieldMeta = new ClassMetadata.FieldMeta();
                    fieldMeta.modifiers = field.getModifiers().getModifiers();
                    fieldMeta.name = field.getName();

                    ClassMetadata.TypeMeta typeMeta = new ClassMetadata.TypeMeta();
                    typeMeta.nullable = field.getType().isNullable().equals(Type.Nullability.NULLABLE);
                    fieldMeta.typeMeta = typeMeta;
                    return fieldMeta;
                }).collect(Collectors.toList());

        ClassMetadata classMetadata = new ClassMetadata();
        classMetadata.fieldMetas = fieldMetas;
        classMetadata.methodMetas = methodMetadataList;

        return classMetadata;
    }


    public ClassMetadata fromString(String s) {
        try {
            return objectMapper.readValue(decompress(s.getBytes()), ClassMetadata.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static byte[] compress(byte[] content) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try {
            GZIPOutputStream gzipOutputStream = new GZIPOutputStream(byteArrayOutputStream);
            gzipOutputStream.write(content);
            gzipOutputStream.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return byteArrayOutputStream.toByteArray();
    }

    private static byte[] decompress(byte[] contentBytes) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            IOUtils.copy(new GZIPInputStream(new ByteArrayInputStream(contentBytes)), out);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return out.toByteArray();
    }


    public String toString(Serializable o) {
        try {
            return new String(compress(objectMapper.writeValueAsString(o).getBytes()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
