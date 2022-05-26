import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class SimpleTest {

    @Nested
    @DisplayName("Exception 테스트")
    class ExceptionTest {

        @Nested
        @DisplayName("Checked Exception은")
        class ifCheckedExceptionThrown {

            @Test
            @DisplayName("상위로 throw 하려면 throws 키워드가 필요하다")
            void ItNeedsThrowsKeywordToThrowToParentMethod() {
                assertThatThrownBy(() -> throwNewCheckedException())
                        .isInstanceOf(Exception.class)
                        .isInstanceOf(MyCheckedException.class);

            }

            @Test
            @DisplayName("catch에서 추가적인 로직을 넣고 상위로 throw 할 수 있다")
            void ItCanAddStepsInCatchBlockAndThrowToParentMethod() {
                assertThatThrownBy(() -> catchCheckedExceptionThenThrow())
                        .isInstanceOf(Exception.class)
                        .isInstanceOf(MyCheckedException.class);
            }

            @Test
            @DisplayName("catch에서 throw하지 않는다면 throws 키워드가 필요 없고 상위로 던지지 않는다")
            void ItJustCatchAndNotThrowToParentMethod() {
                catchCheckedException();
                assertThatNoException();
            }

            void throwNewCheckedException() throws MyCheckedException {
                throw new MyCheckedException();
            }

            void catchCheckedExceptionThenThrow() throws MyCheckedException {
                try {
                    throw new MyCheckedException();
                } catch (MyCheckedException e) {
                    System.out.println("Step");
                    throw e;
                }
            }

            void catchCheckedException() {
                try {
                    throw new MyCheckedException();
                } catch (MyCheckedException e) {
                    System.out.println("Step");
                }
            }

            class MyCheckedException extends Exception { }
        }

        @Nested
        @DisplayName("Unchecked Exception은")
        class ifUncheckedExceptionThrown {

            @Test
            @DisplayName("throws 키워드 없이도 상위로 throw할 수 있다.")
            void ItCanThrowToParentWithoutThrowsKeyword() {
                assertThatThrownBy(() -> throwUncheckedException())
                        .isInstanceOf(Exception.class)
                        .isInstanceOf(MyUncheckedException.class);
            }

            @Test
            @DisplayName("catch 후 throw 하지 않으면 상위로 던져지지 않는다")
            void ItNotThrowToParentWithoutThrow() {
                throwUncheckedException3();
                assertThatNoException();
            }

            @Test
            @DisplayName("catch 후 throw 하면 상위로 던질 수 있다")
            void ItThrowsToParent() {
                assertThatThrownBy(() -> throwUncheckedException2())
                        .isInstanceOf(Exception.class)
                        .isInstanceOf(MyUncheckedException.class);
            }

            void throwUncheckedException() {
                throw new MyUncheckedException();
            }

            void throwUncheckedException2() throws MyUncheckedException {
                try {
                    throw new MyUncheckedException();
                } catch (MyUncheckedException e) {
                    System.out.println("Step");
                    throw e;
                }
            }

            void throwUncheckedException3() {
                try {
                    throw new MyUncheckedException();
                } catch (MyUncheckedException e) {
                    System.out.println("Step");
                }
            }

            class MyUncheckedException extends RuntimeException { }
        }
    }

}
