package com.kubadziworski.domain.type.rtype;

import org.objectweb.asm.tree.ClassNode;

/**
 * Created by plozano on 4/6/2017.
 */
public interface JavaDeclaredClass extends DeclaredType {

    public ClassNode getClassNode(boolean skipCode);
}
