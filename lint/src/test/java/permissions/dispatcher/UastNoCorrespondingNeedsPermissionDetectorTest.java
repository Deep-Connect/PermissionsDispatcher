package permissions.dispatcher;

import org.intellij.lang.annotations.Language;
import org.junit.Test;
import permissions.dispatcher.uast.UastNoCorrespondingNeedsPermissionDetector;

import static com.android.tools.lint.checks.infrastructure.TestFiles.java;
import static com.android.tools.lint.checks.infrastructure.TestLintTask.lint;
import static permissions.dispatcher.Utils.PACKAGE;
import static permissions.dispatcher.Utils.SOURCE_PATH;
import static permissions.dispatcher.Utils.getOnNeedsPermission;
import static permissions.dispatcher.Utils.getOnRationaleAnnotation;

public final class UastNoCorrespondingNeedsPermissionDetectorTest {

    @Test
    public void noNeedsPermissionAnnotationNoErrors() throws Exception {
        @Language("JAVA") String onNeeds = getOnNeedsPermission();

        @Language("JAVA") String onShow = getOnRationaleAnnotation();

        @Language("JAVA") String foo = ""
                + PACKAGE
                + "public class Foo {\n"
                + "@NeedsPermission(\"Camera\")\n"
                + "public void showCamera() {\n"
                + "}\n"
                + "@OnShowRationale(\"Camera\")\n"
                + "public void someMethod() {\n"
                + "}\n"
                + "}";

        lint()
                .files(
                        java(SOURCE_PATH + "NeedsPermission.java", onNeeds),
                        java(SOURCE_PATH + "OnShowRationale.java", onShow),
                        java(SOURCE_PATH + "Foo.java", foo))
                .issues(UastNoCorrespondingNeedsPermissionDetector.ISSUE)
                .run()
                .expectClean();
    }

    @Test
    public void noNeedsPermissionAnnotation() throws Exception {

        @Language("JAVA") String onShow = getOnRationaleAnnotation();

        @Language("JAVA") String foo = ""
                + PACKAGE
                + "public class Foo {\n"
                + "@OnShowRationale(\"Camera\")\n"
                + "public void someMethod() {\n"
                + "}\n"
                + "}";

        String expectedText = ""
                + SOURCE_PATH + "Foo.java:3: Error: Useless @OnShowRationale declaration "
                + "["
                + UastNoCorrespondingNeedsPermissionDetector.ISSUE.getId()
                + "]\n"
                + "@OnShowRationale(\"Camera\")\n"
                + "~~~~~~~~~~~~~~~~~~~~~~~~~~\n"
                + "1 errors, 0 warnings\n";

        lint()
                .files(
                        java(SOURCE_PATH + "OnShowRationale.java", onShow),
                        java(SOURCE_PATH + "Foo.java", foo))
                .issues(UastNoCorrespondingNeedsPermissionDetector.ISSUE)
                .run()
                .expect(expectedText)
                .expectErrorCount(1)
                .expectWarningCount(0);
    }
}
