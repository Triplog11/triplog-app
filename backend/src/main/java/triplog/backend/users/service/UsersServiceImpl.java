package triplog.backend.users.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import triplog.backend.users.entity.LoginType;
import triplog.backend.users.entity.Users;
import triplog.backend.users.repository.UsersRepository;

import java.util.Optional;

/**
 * {@link UsersService}의 구현 클래스입니다.
 * <p>
 * 사용자(Users)와 관련된 비즈니스 로직을 처리하며,
 * Repository를 통해 사용자 데이터를 조회하고 관리합니다.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UsersServiceImpl implements UsersService {

    private final UsersRepository usersRepository;

    /**
     * 이메일과 로그인 타입으로 사용자를 조회합니다.
     *
     * @param email 사용자 이메일
     * @param loginType 로그인 타입
     * @return 조회된 사용자
     */
    @Override
    public Optional<Users> findByEmailAndLoginType(String email, LoginType loginType) {
        return usersRepository.findByEmailAndLoginType(email, loginType);
    }
}
