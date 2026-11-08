package co.edu.corhuila.taskmanager;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

/**
 * TC-027 — Layer and module rules of 05-architecture/boundary-enforcement.md (RNF-11).
 */
@AnalyzeClasses(packages = "co.edu.corhuila.taskmanager", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    /** AB-02, AB-07: controller → service → repository, never backwards or skipping. */
    @ArchTest
    static final ArchRule layersAreRespected = layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controller").definedBy("..controller..")
            .layer("Service").definedBy("..service..")
            .layer("Repository").definedBy("..repository..")
            .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
            .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller")
            .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service");

    /** AB-05: tasks depends on no other business module. */
    @ArchTest
    static final ArchRule tasksIsIndependent = noClasses()
            .that().resideInAPackage("..taskmanager.tasks..")
            .should().dependOnClassesThat().resideInAnyPackage("..taskmanager.users..", "..taskmanager.auth..");

    /** AB-05: users depends on no other business module. */
    @ArchTest
    static final ArchRule usersIsIndependent = noClasses()
            .that().resideInAPackage("..taskmanager.users..")
            .should().dependOnClassesThat().resideInAnyPackage("..taskmanager.auth..", "..taskmanager.tasks..");

    /** AB-01: auth uses users only through its service, never its repository. */
    @ArchTest
    static final ArchRule authUsesUsersOnlyThroughService = noClasses()
            .that().resideInAPackage("..taskmanager.auth..")
            .should().dependOnClassesThat().resideInAPackage("..taskmanager.users.repository..");

    /** common holds technical code and depends on no business module. */
    @ArchTest
    static final ArchRule commonIsIndependent = noClasses()
            .that().resideInAPackage("..taskmanager.common..")
            .should().dependOnClassesThat().resideInAnyPackage(
                    "..taskmanager.auth..", "..taskmanager.users..", "..taskmanager.tasks..");
}
