package istu.bacs.contest.dto;

import istu.bacs.contest.Contest;
import istu.bacs.problem.Problem;
import istu.bacs.problem.ProblemDetails;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProblemDto {

    private String name;
    private int indexInContest;

    private int timeLimitMillis;
    private int memoryLimitBytes;

    public ProblemDto(Problem problem, int index) {
        ProblemDetails details = problem.getDetails();

        name = details.getProblemName();

        timeLimitMillis = details.getTimeLimitMillis();
        memoryLimitBytes = details.getMemoryLimitBytes();
        indexInContest = index;
    }

    public static List<ProblemDto> forContest(Contest contest) {
        List<Problem> contestProblems = contest.getProblems();
        int problemCount = contestProblems.size();

        List<ProblemDto> problems = new ArrayList<>(problemCount);
        for (int i = 0; i < problemCount; i++) {
            ProblemDto problem = new ProblemDto(contestProblems.get(i), i);
            problems.add(problem);
        }

        return problems;
    }
}