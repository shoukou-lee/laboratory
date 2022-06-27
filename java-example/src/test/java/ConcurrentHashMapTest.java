import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class ConcurrentHashMapTest {
    Map<String, String> map = new ConcurrentHashMap<>();
    private final String KEY = "concurrent";
    private final String VALUE = "Hashmap!";

    @BeforeEach
    void init() {
        map.put(KEY, VALUE);
    }

    @AfterEach
    void tearDown() {
        map.clear();
    }

    @Nested
    @DisplayName("containsKey 메서드는")
    class ContainsKeyTest {

        @Nested
        @DisplayName("key 인자로 null이 들어오면")
        class ifNullPassIntoKeyArg {
            @Test
            @DisplayName("NPE를 던진다")
            void itThrowsNPE() {
                assertThatThrownBy(() -> map.containsKey(null))
                        .isInstanceOf(NullPointerException.class);
            }
        }

        @Nested
        @DisplayName("key 인자로 map에 없는 non-null key가 들어오면")
        class ifNonNullPassIntoKeyArgButNotExist {
            @Test
            @DisplayName("false를 리턴한다")
            void itReturnsFalse() {
                boolean ret = map.containsKey("NotExistKey");
                assertThat(ret).isFalse();
            }
        }

        @Nested
        @DisplayName("key 인자로 map에 이미 존재하는 non-null key가 들어오면")
        class ifNonNullPassIntoKeyArgAndExist {
            @Test
            @DisplayName("true를 리턴한다")
            void itReturnsTrue() {
                boolean ret = map.containsKey(KEY);
                assertThat(ret).isTrue();
            }
        }
    }


    @Nested
    @DisplayName("get 메서드는")
    class getTest {

        @Nested
        @DisplayName("key 인자로 null이 들어오면")
        class ifNullPassIntoKeyArg {
            @Test
            @DisplayName("NPE를 던진다")
            void itThrowsNPE() {
                assertThatThrownBy(() -> map.get(null))
                        .isInstanceOf(NullPointerException.class);
            }
        }

        @Nested
        @DisplayName("key 인자로 map에 없는 non-null key가 들어오면")
        class ifNonNullPassIntoKeyArgButNotExist {
            @Test
            @DisplayName("null을 리턴한다")
            void itReturnsNull() {
                String ret = map.get("NotExistKey");
                assertThat(ret).isNull();
            }
        }

        @Nested
        @DisplayName("key 인자로 map에 이미 존재하는 non-null key가 들어오면")
        class ifNonNullPassIntoKeyArgAndExist {
            @Test
            @DisplayName("매핑된 value를 리턴한다")
            void itReturnsMappedValue() {
                String ret = map.get(KEY);
                assertThat(ret).isEqualTo(VALUE);
            }
        }
    }

    @Nested
    @DisplayName("put 메서드는")
    class putTest {

        @Nested
        @DisplayName("key 인자로 null이 들어오면")
        class ifNullPassIntoKeyArg {
            @Test
            @DisplayName("NPE를 던진다")
            void itThrowsNPE() {
                final String NEW_KEY = null;
                final String NEW_VALUE = "newValue";

                assertThatThrownBy(() -> map.put(NEW_KEY, NEW_VALUE))
                        .isInstanceOf(NullPointerException.class);
            }
        }

        @Nested
        @DisplayName("value 인자로 null이 들어오면")
        class ifNullPassIntoValueArg {
            @Test
            @DisplayName("NPE를 던진다")
            void itThrowsNPE() {
                final String NEW_KEY = "newKey";
                final String NEW_VALUE = null;

                assertThatThrownBy(() -> map.put(NEW_KEY, NEW_VALUE))
                        .isInstanceOf(NullPointerException.class);
            }
        }

        @Nested
        @DisplayName("key 인자로 map에 없는 non-null key가 들어오면")
        class ifNonNullPassKeyArgButNotExist {
            @Test
            @DisplayName("null이 리턴된 후, 새 key-value pair가 추가된다")
            void itAddsNewKeyValuePairAndReturnsNull() {
                final String NEW_KEY = "newKey";
                final String NEW_VALUE = "newValue";
                int size = map.size();

                String ret = map.put(NEW_KEY, NEW_VALUE);
                assertThat(ret).isNull();
                assertThat(map.size()).isEqualTo(size + 1);
                assertThat(map.get(NEW_KEY)).isEqualTo(NEW_VALUE);
            }
        }

        @Nested
        @DisplayName("key 인자로 map에 이미 존재하는 non-null key가 들어오면")
        class ifNonNullPassIntoKeyArgAndExist {
            @Test
            @DisplayName("key에 대응하는 value가 리턴된 후, 새 value로 업데이트된다")
            void itReturnsPrevValueAndUpdatesToNewValue() {
                final String NEW_VALUE = "newValue";
                int size = map.size();

                String ret = map.put(KEY, NEW_VALUE);
                assertThat(ret).isEqualTo(VALUE);
                assertThat(map.size()).isEqualTo(size);
                assertThat(map.get(KEY)).isEqualTo(NEW_VALUE);
            }
        }
    }

    @Nested
    @DisplayName("remove 메서드는")
    class removeTest {

        @Nested
        @DisplayName("key 인자로 null이 들어오면")
        class ifNullPassIntoKeyArg {
            @Test
            @DisplayName("NPE를 던진다")
            void itThrowsNPE() {
                assertThatThrownBy(() -> map.remove(null))
                        .isInstanceOf(NullPointerException.class);
            }
        }

        @Nested
        @DisplayName("key 인자로 map에 없는 non-null key가 들어오면")
        class ifNonNullPassKeyArgButNotExist {
            @Test
            @DisplayName("null을 리턴한다")
            void itReturnsNull() {
                final String NOT_EXIST_KEY = "notExistKey";
                int size = map.size();

                String ret = map.remove(NOT_EXIST_KEY);
                assertThat(ret).isNull();
                assertThat(map.size()).isEqualTo(size);
            }
        }

        @Nested
        @DisplayName("key 인자로 map에 이미 존재하는 non-null key가 들어오면")
        class ifNonNullPassIntoKeyArgAndExist {
            @Test
            @DisplayName("해당 key의 value가 리턴되고 map에서 key가 삭제된다")
            void itReturnsMappedValueAndKeyRemoved() {
                int size = map.size();

                String ret = map.remove(KEY);
                assertThat(ret).isEqualTo(VALUE);
                assertThat(map.size()).isEqualTo(size - 1);
                assertThat(map.get(KEY)).isNull();
            }
        }
    }

}
