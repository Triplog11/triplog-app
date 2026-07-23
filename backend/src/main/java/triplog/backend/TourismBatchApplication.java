package triplog.backend;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.job.JobExecution;
import org.springframework.batch.core.job.JobInstance;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.util.Arrays;

/**
 * TourAPI Batch Job만 실행하고 결과에 맞는 종료 코드를 반환하는 무웹 실행 진입점입니다.
 * <p>
 * 일반 API 서버와 동일한 애플리케이션 구성을 사용하되 웹 서버를 시작하지 않습니다.
 * 지정한 Spring Batch Job이 끝나면 성공은 {@code 0}, 실패는 {@code 1}로 프로세스를 종료합니다.
 */
@Slf4j
public final class TourismBatchApplication {

    private static final int EXIT_SUCCESS = 0;
    private static final int EXIT_FAILURE = 1;

    private TourismBatchApplication() {
    }

    /**
     * 배치 애플리케이션을 실행하고 Job 결과를 운영체제 종료 코드로 반환합니다.
     *
     * @param args Spring Boot 옵션과 Batch Job 파라미터
     */
    public static void main(String[] args) {
        System.exit(run(args));
    }

    static int run(String[] args) {
        SpringApplication application = new SpringApplication(BackendApplication.class);
        application.setWebApplicationType(WebApplicationType.NONE);

        ConfigurableApplicationContext context = null;
        try {
            context = application.run(withBatchEnabled(args));
            return determineExitCode(context);
        } catch (RuntimeException exception) {
            log.error("TourAPI 배치 애플리케이션 실행에 실패했습니다.", exception);
            return EXIT_FAILURE;
        } finally {
            if (context != null) {
                context.close();
            }
        }
    }

    private static String[] withBatchEnabled(String[] args) {
        String[] batchArgs = Arrays.copyOf(args, args.length + 1);
        batchArgs[args.length] = "--spring.batch.job.enabled=true";
        return batchArgs;
    }

    private static int determineExitCode(ConfigurableApplicationContext context) {
        Environment environment = context.getEnvironment();
        String jobName = environment.getProperty("spring.batch.job.name");
        if (!StringUtils.hasText(jobName)) {
            log.error("실행할 Spring Batch Job 이름이 지정되지 않았습니다.");
            return EXIT_FAILURE;
        }

        JobRepository jobRepository = context.getBean(JobRepository.class);
        JobInstance jobInstance = jobRepository.getLastJobInstance(jobName);
        if (jobInstance == null) {
            log.error("실행 이력을 찾을 수 없습니다: jobName={}", jobName);
            return EXIT_FAILURE;
        }

        JobExecution execution = jobRepository.getLastJobExecution(jobInstance);
        if (execution == null) {
            log.error("Job 실행 결과를 찾을 수 없습니다: jobName={}", jobName);
            return EXIT_FAILURE;
        }
        boolean succeeded = execution.getStatus() == BatchStatus.COMPLETED
                && ExitStatus.COMPLETED.getExitCode().equals(execution.getExitStatus().getExitCode());
        log.info(
                "TourAPI 배치 프로세스 종료: jobName={}, batchStatus={}, exitStatus={}, processExitCode={}",
                jobName,
                execution.getStatus(),
                execution.getExitStatus().getExitCode(),
                succeeded ? EXIT_SUCCESS : EXIT_FAILURE
        );
        return succeeded ? EXIT_SUCCESS : EXIT_FAILURE;
    }
}
