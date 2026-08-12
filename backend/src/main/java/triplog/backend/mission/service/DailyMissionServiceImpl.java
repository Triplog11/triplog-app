package triplog.backend.mission.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import triplog.backend.mission.entity.Mission;
import triplog.backend.mission.repository.MissionRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * {@link DailyMissionService}의 기본 구현체입니다.
 * 난이도 규칙에 따라 일일 미션 세 개를 선택하고 생성합니다.
 */
@Service
@RequiredArgsConstructor
public class DailyMissionServiceImpl implements DailyMissionService {

    private final MissionRepository missionRepository;

    /**
     * 기준 시각에 해당하는 일일 미션 세 개를 중복 없이 생성합니다.
     * 동일 날짜에는 같은 템플릿이 선택되도록 날짜 기반 시드를 사용합니다.
     *
     * @param now 기준 시각
     */
    @Override
    @Transactional
    public void ensureDailyMissions(LocalDateTime now) {
        LocalDate date = now.toLocalDate();
        LocalDateTime dayStart = date.atStartOfDay();
        LocalDateTime dayEnd = date.atTime(LocalTime.MAX);

        for (DailyMissionTemplate template : selectTemplates(date)) {
            if (!missionRepository.existsByMissionWeekStartAndMissionName(dayStart, template.name())) {
                missionRepository.save(Mission.createDaily(
                        template.name(),
                        template.group(),
                        template.target().name(),
                        template.value(),
                        template.filter(),
                        dayStart,
                        dayEnd,
                        template.xp()
                ));
            }
        }
    }

    /**
     * EASY 한 개, NORMAL 한 개, 나머지 NORMAL 또는 HARD 한 개를 선택합니다.
     */
    private List<DailyMissionTemplate> selectTemplates(LocalDate date) {
        Random random = new Random(date.toEpochDay());
        List<DailyMissionTemplate> easy = templatesByGroup(1);
        List<DailyMissionTemplate> normal = templatesByGroup(2);
        List<DailyMissionTemplate> hard = templatesByGroup(3);
        Collections.shuffle(easy, random);
        Collections.shuffle(normal, random);
        Collections.shuffle(hard, random);

        DailyMissionTemplate selectedEasy = easy.getFirst();
        DailyMissionTemplate selectedNormal = normal.getFirst();
        List<DailyMissionTemplate> finalCandidates = new ArrayList<>(normal.subList(1, normal.size()));
        finalCandidates.addAll(hard);
        Collections.shuffle(finalCandidates, random);

        return List.of(selectedEasy, selectedNormal, finalCandidates.getFirst());
    }

    /**
     * 난이도 그룹에 해당하는 템플릿을 복사하여 반환합니다.
     */
    private List<DailyMissionTemplate> templatesByGroup(int group) {
        return new ArrayList<>(DailyMissionPool.templates().stream()
                .filter(template -> template.group() == group)
                .toList());
    }
}
