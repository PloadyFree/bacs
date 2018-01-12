package istu.bacs.contest.dto;

import istu.bacs.contest.Contest;
import istu.bacs.contest.ContestProblem;
import istu.bacs.problem.Problem;
import lombok.Data;

import java.util.List;

import static java.util.stream.Collectors.toList;

@Data
public class ProblemDto {

    private String index;
    private String name;
    private String statementUrl;

    private int timeLimitMillis;
    private int memoryLimitBytes;

    public ProblemDto(ContestProblem contestProblem) {
        Problem problem = contestProblem.getProblem();

        index = contestProblem.getProblemIndex();
        name = problem.getProblemName();
        statementUrl = problem.getStatementUrl();

        timeLimitMillis = problem.getTimeLimitMillis();
        memoryLimitBytes = problem.getMemoryLimitBytes();
    }

    public static List<ProblemDto> forContest(Contest contest) {
        return contest.getProblems().stream()
                .map(ProblemDto::new)
                .collect(toList());
    }
}