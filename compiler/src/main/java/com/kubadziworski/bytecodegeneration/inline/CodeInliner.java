package com.kubadziworski.bytecodegeneration.inline;

import com.kubadziworski.domain.type.Type;
import org.objectweb.asm.tree.MethodNode;


public interface CodeInliner {

    MethodNode getMethodNode(Type owner, String name, String desc);
}
