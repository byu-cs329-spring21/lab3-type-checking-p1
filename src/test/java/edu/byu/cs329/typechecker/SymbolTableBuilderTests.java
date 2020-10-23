package edu.byu.cs329.typechecker;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("Tests for the SymbolTableBuilder")
public class SymbolTableBuilderTests {
  SymbolTableBuilder stb = null;

  @BeforeEach
  void beforeEach() {
    stb = new SymbolTableBuilder();
  }

  @Test
  @DisplayName("Should throw assertion when program has imports")
  void should_throwAssertion_when_programHasImports() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_throwAssertion_when_programHasImports.java");
    assertThrows(RuntimeException.class, () -> stb.getSymbolTable(compilationUnit));
  }

  @Test
  @DisplayName("Should throw assertion when program defines two classes")
  void should_throwAssertion_when_programDefinesTwoClasses() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_throwAssertion_when_programDefinesTwoClasses.java");
    assertThrows(RuntimeException.class, () -> stb.getSymbolTable(compilationUnit));
  }

  @Test
  @DisplayName("Should throw assertion when program defines inner class")
  void should_throwAssertion_when_programDefinesInnerClass() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_throwAssertion_when_programDefinesInnerClass.java");
    assertThrows(RuntimeException.class, () -> stb.getSymbolTable(compilationUnit));
  }

  @Test
  @DisplayName("Should throw assertion when multiple variables in fragments")
  void should_throwAssertion_when_multipleVariablesInFragments() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_throwAssertion_when_multipleVariablesInFragments.java");
    assertThrows(RuntimeException.class, () -> stb.getSymbolTable(compilationUnit));
  }

  @Test
  @DisplayName("Should throw assertion when type name is not simple")
  void should_throwAssertion_when_typeNameIsNotSimple() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_throwAssertion_when_typeNameIsNotSimple.java");
    assertThrows(RuntimeException.class, () -> stb.getSymbolTable(compilationUnit));
  }

  @Test
  @DisplayName("Should throw assertion when primitive type is not int or boolean")
  void should_throwAssertion_when_primitiveTypeIsNotIntOrBoolean() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_throwAssertion_when_primitiveTypeIsNotIntOrBoolean.java");
    assertThrows(RuntimeException.class, () -> stb.getSymbolTable(compilationUnit));
  }

  @Test
  @DisplayName("Should throw assertion when modifiers not private, public, or protected")
  void should_throwAssertion_when_modifiersNotPrivatePublicProtected() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_throwAssertion_when_modifiersNotPrivatePublicProtected.java");
    assertThrows(RuntimeException.class, () -> stb.getSymbolTable(compilationUnit));
  }

  @Test
  @DisplayName("Should throw assertion when methods have same name but different parameter types")
  void should_throwAssertion_when_methodsHaveSameNameButDifferentParameterTypes() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_throwAssertion_when_methodsHaveSameNameButDifferentParameterTypes.java");
    assertThrows(RuntimeException.class, () -> stb.getSymbolTable(compilationUnit));
  }

  @Test
  @DisplayName("Should add all fields when all fields correctly declared")
  void should_addAllFields_when_allFieldsCorrectlyDeclared() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_addAllFields_when_allFieldsCorrectlyDeclared.java");
    ISymbolTable st = stb.getSymbolTable(compilationUnit);
    assertAll(
        () -> assertEquals(ISymbolTable.INT, st.getType("should_addAllFields_when_allFieldsCorrectlyDeclared.i")),
        () -> assertEquals(ISymbolTable.INT, st.getType("should_addAllFields_when_allFieldsCorrectlyDeclared.j")),
        () -> assertEquals("should_addAllFields_when_allFieldsCorrectlyDeclared", st.getType("should_addAllFields_when_allFieldsCorrectlyDeclared.k")),
        () -> assertEquals("Integer", st.getType("should_addAllFields_when_allFieldsCorrectlyDeclared.m"))
    );
  }

  @Test
  @DisplayName("Should create parameter type maps when methods defined")
  void should_createParameterTypeMaps_when_methodsExist() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_createParameterTypeMaps_when_methodsExist.java");
    ISymbolTable st = stb.getSymbolTable(compilationUnit);
    String className = "should_createParameterTypeMaps_when_methodsExist";
    String nameForM = Utils.buildName(className, "m");
    String nameForN = Utils.buildName(className, "n");
    Map<String, String> typeMapForM = st.getParameterTypeMap(nameForM);
    Map<String, String> typeMapForN = st.getParameterTypeMap(nameForN);
    assertAll(
        () -> assertEquals(ISymbolTable.VOID, st.getType(nameForM)),
        () -> assertTrue(typeMapForM.containsKey("i")),
        () -> assertEquals(ISymbolTable.INT, typeMapForM.get("i")),
        () -> assertTrue(typeMapForM.containsKey("j")),
        () -> assertEquals("Integer", typeMapForM.get("j")),
        () -> assertEquals(0, typeMapForN.size())
    );  
  }

  @Test
  @DisplayName("Should add and remove scopes when adding and removing locals")
  void should_addAndRemoveScopes_when_addingAndRemovingLocals() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_addAllFields_when_allFieldsCorrectlyDeclared.java");
    ISymbolTable st = stb.getSymbolTable(compilationUnit);
    st.pushScope();
    st.addLocal("i", "int");
    st.addLocal("j", "Integer");
    assertEquals(ISymbolTable.INT, st.getType("i"));
    assertEquals("Integer", st.getType("j"));
    st.popScope();
    assertEquals(ISymbolTable.ERROR, st.getType("i"));
    assertEquals(ISymbolTable.ERROR, st.getType("j"));
  }

  @Test
  @DisplayName("Should throw exception when adding duplicate local variables")
  void should_throwException_when_addingDuplicateLocalVariables() {
    ASTNode compilationUnit = Utils.getASTNodeFor(this, "symbolTable/should_addAllFields_when_allFieldsCorrectlyDeclared.java");
    ISymbolTable st = stb.getSymbolTable(compilationUnit);
    assertThrows(RuntimeException.class, () -> {
      st.pushScope();
      st.addLocal("i", "int");
      st.addLocal("i", "Integer");
    });
  }
}
