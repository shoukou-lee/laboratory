package iam.shoukou.jpaexample.room;

import iam.shoukou.jpaexample.model.OpenRoom;
import iam.shoukou.jpaexample.model.Room;
import iam.shoukou.jpaexample.repository.RoomRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("mysql-test")
public class RoomTest {

    @Autowired
    RoomRepository roomRepository;

    @BeforeEach
    void init() {

    }

    @AfterEach
    void tearDown() {
        roomRepository.deleteAll();
    }

    @Test
    @DisplayName("엔티티 상속 기능 사용해보기")
    void extendedRoomTest() {
        // given
        for (int i = 0; i < 5; i++) {
            Room room = new OpenRoom("openRoom", true);
            roomRepository.save(room);
        }

        // when
        List<OpenRoom> allOpenRoom = roomRepository.findAllOpenRoom();

        // then
        for (OpenRoom openRoom : allOpenRoom) {
            assertThat(openRoom.getType()).isEqualTo("OPEN");
        }
    }

    @Test
    @DisplayName("Discriminator를 업데이트하려고 시도해도 테이블의 저장값은 바뀌지 않는다")
    void modifyDiscriminator() {
        // given
        Room room = new OpenRoom("openRoom", true);
        roomRepository.save(room);

        // when
        room.setType("PRIV");
        room.setName("privRoom");
        Room save = roomRepository.save(room);

        // then
        Room ret = roomRepository.findByName("privRoom")
                .orElseThrow(() -> new RuntimeException());

        assertThat(ret.getType()).isEqualTo("OPEN");
    }

}
