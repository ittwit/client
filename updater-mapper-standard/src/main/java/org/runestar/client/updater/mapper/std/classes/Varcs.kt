package org.runestar.client.updater.mapper.std.classes

import org.kxtra.lang.list.startsWith
import org.objectweb.asm.Opcodes.*
import org.objectweb.asm.Type.*
import org.runestar.client.updater.mapper.IdentityMapper
import org.runestar.client.updater.mapper.OrderMapper
import org.runestar.client.updater.mapper.annotations.DependsOn
import org.runestar.client.updater.mapper.annotations.MethodParameters
import org.runestar.client.updater.mapper.extensions.Predicate
import org.runestar.client.updater.mapper.extensions.and
import org.runestar.client.updater.mapper.extensions.predicateOf
import org.runestar.client.updater.mapper.extensions.type
import org.runestar.client.updater.mapper.tree.Class2
import org.runestar.client.updater.mapper.tree.Field2
import org.runestar.client.updater.mapper.tree.Instruction2
import org.runestar.client.updater.mapper.tree.Method2

class Varcs : IdentityMapper.Class() {

    override val predicate = predicateOf<Class2> { it.superType == Any::class.type }
            .and { it.instanceFields.count { it.type == BooleanArray::class.type } == 2 }
            .and { it.instanceFields.count { it.type == IntArray::class.type } == 1 }
            .and { it.instanceFields.count { it.type == Array<String>::class.type } == 1 }

    class ints : IdentityMapper.InstanceField() {
        override val predicate = predicateOf<Field2> { it.type == IntArray::class.type }
    }

    class strings : IdentityMapper.InstanceField() {
        override val predicate = predicateOf<Field2> { it.type == Array<String>::class.type }
    }

    @DependsOn(AccessFile::class)
    class getPreferencesFile : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.returnType == type<AccessFile>() }
    }

    @DependsOn(AccessFile.read::class)
    class read : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.instructions.any { it.isMethod && it.methodId == method<AccessFile.read>().id } }
    }

    @DependsOn(AccessFile.write::class)
    class write : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.instructions.any { it.isMethod && it.methodId == method<AccessFile.write>().id } }
    }

    class intsPersistence : OrderMapper.InConstructor.Field(Varcs::class, 0, 2) {
        override val predicate = predicateOf<Instruction2> { it.opcode == PUTFIELD && it.fieldType == BooleanArray::class.type }
    }

    class stringsPersistence : OrderMapper.InConstructor.Field(Varcs::class, 1, 2) {
        override val predicate = predicateOf<Instruction2> { it.opcode == PUTFIELD && it.fieldType == BooleanArray::class.type }
    }

    class unwrittenChanges : IdentityMapper.InstanceField() {
        override val predicate = predicateOf<Field2> { it.type == BOOLEAN_TYPE }
    }

    class lastWriteTimeMs : IdentityMapper.InstanceField() {
        override val predicate = predicateOf<Field2> { it.type == LONG_TYPE }
    }

    @MethodParameters("index")
    class getString : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.returnType == String::class.type }
    }

    @MethodParameters("index")
    class getInt : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.returnType == INT_TYPE }
    }

    @MethodParameters()
    class hasUnwrittenChanges : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.returnType == BOOLEAN_TYPE }
    }

    @MethodParameters("index", "s")
    class setString : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.returnType == VOID_TYPE }
                .and { it.arguments.startsWith(INT_TYPE, String::class.type) }
    }

    @MethodParameters("index", "n")
    class setInt : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.returnType == VOID_TYPE }
                .and { it.arguments.startsWith(INT_TYPE, INT_TYPE) }
    }

    @MethodParameters()
    class clearTransient : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.returnType == VOID_TYPE }
                .and { it.arguments.size in 0..1 }
                .and { it.node.tryCatchBlocks.isEmpty() }
                .and { it.instructions.any { it.opcode == AASTORE } }
    }

    @MethodParameters()
    class tryWrite : IdentityMapper.InstanceMethod() {
        override val predicate = predicateOf<Method2> { it.returnType == VOID_TYPE }
                .and { it.arguments.size in 0..1 }
                .and { it.node.tryCatchBlocks.isEmpty() }
                .and { it.instructions.none { it.opcode == AASTORE } }
    }
}