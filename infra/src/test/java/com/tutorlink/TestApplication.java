package com.tutorlink;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * 테스트 환경에서 Spring Context를 로드하기 위한 가짜 Spring Boot Application 클래스입니다.
 *
 * @DataJpaTest 와 같은 슬라이스 테스트는 @SpringBootConfiguration 을 찾으려고 시도하는데,
 * 독립 모듈에는 해당 설정이 없으므로 이 클래스가 그 역할을 대신합니다.
 * 실제 로직은 필요 없으며, 어노테이션만으로 충분합니다.
 */
@SpringBootApplication
public class TestApplication {
    public void main(String[] args) {
        // 이 main 메서드는 실제로 실행되지 않습니다.
    }
}