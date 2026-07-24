package triplog.backend.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.users.entity.Users;
import triplog.backend.users.exception.UsersException;
import triplog.backend.users.repository.UsersRepository;

import static triplog.backend.users.exception.UsersErrorCode.USER_NOT_FOUND;

/**
 * 랭킹 구성에 필요한 사용자 요약 정보를 조회하는 서비스 구현체입니다.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UsersRankingServiceImpl implements UsersRankingService {

    /**
     * 사용자 저장과 조회를 담당하는 Repository입니다.
     */
    private final UsersRepository usersRepository;

    /**
     * 사용자 ID로 닉네임과 프로필 이미지 URL을 조회합니다.
     *
     * @param usersId 조회할 사용자 ID
     * @return 랭킹 응답에 필요한 사용자 요약 정보
     * @throws UsersException 사용자를 찾을 수 없는 경우
     */
    @Override
    public UsersRankingInfo getRankingInfo(String usersId) {
        Users users = usersRepository.findById(usersId)
                .orElseThrow(() -> new UsersException(USER_NOT_FOUND));
        return new UsersRankingInfo(users.getNickname(), users.getProfileUrl());
    }
}
