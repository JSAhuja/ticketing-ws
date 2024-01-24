package demo.apps.ticketingws.arch;

import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.lang.ArchRule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static com.tngtech.archunit.library.Architectures.layeredArchitecture;

@SpringBootTest()
class LayeredArchitectureTest {

    private final String basePackage = "demo.apps.ticketingws";

    @Test
    void layer_validation_check() {
        ArchRule validatorCondition =
                layeredArchitecture()
                        .consideringAllDependencies()
                        .layer("Controller").definedBy(basePackage + ".controllers..")
                        .layer("Controller").definedBy(basePackage + ".controllers..")
                        .layer("Service").definedBy(basePackage + ".services..")
                        .layer("Repository").definedBy(basePackage + ".repository..")
                        .layer("Common").definedBy(basePackage + ".common..")

                        .whereLayer("Controller").mayNotBeAccessedByAnyLayer()
                        .whereLayer("Service").mayOnlyBeAccessedByLayers("Controller", "Common")
                        .whereLayer("Repository").mayOnlyBeAccessedByLayers("Service", "Repository");
        validatorCondition.check(new ClassFileImporter()
                .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                .importPackages(basePackage));
    }


}
