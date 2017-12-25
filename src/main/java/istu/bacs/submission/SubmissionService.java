package istu.bacs.submission;

import java.util.List;
import java.util.function.Consumer;

public interface SubmissionService {
    Submission findById(int submissionId);
    List<Submission> findAll();
    List<Submission> findAllByContest(int contestId);
    List<Submission> findAllByContestAndAuthor(int contestId, int authorUserId);
    void submit(Submission submission);
    void save(Submission submission);

    void subscribeOnSolutionSubmitted(Consumer<Submission> function);
    void solutionSubmitted(Submission submission);

    void subscribeOnSolutionTested(Consumer<Submission> function);
    void solutionTested(Submission submission);
}