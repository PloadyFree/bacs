package istu.bacs.web;

import istu.bacs.externalapi.ExternalApiAggregator;
import istu.bacs.externalapi.ExternalApiHelper;
import istu.bacs.model.*;
import istu.bacs.service.ContestService;
import istu.bacs.service.SubmissionService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import static java.util.Collections.singletonList;

@Controller
public class ContestController {
	
	private static final String VIEWS_CONTEST_LIST = "contests/contest-list";
	private static final String VIEWS_CONTEST_PROBLEMS = "contests/contest-problems";
	private static final String VIEWS_SUBMISSION_LIST = "contests/submissions/submission-list";
    private static final String VIEWS_SUBMISSION_VIEW = "contests/submissions/submission-view";
    private static final String VIEWS_SUBMIT_PAGE = "contests/submissions/submit-solution";

	private final ContestService contestService;
	private final SubmissionService submissionService;
	private final ExternalApiAggregator externalApi;
	
	public ContestController(ContestService contestService, SubmissionService submissionService, ExternalApiAggregator externalApi) {
		this.contestService = contestService;
		this.submissionService = submissionService;
        this.externalApi = externalApi;
    }
	
	@RequestMapping("/contests")
	public String getAllContests(Model model) {
		model.addAttribute("contests", contestService.findAll());
		return VIEWS_CONTEST_LIST;
	}
	
	@RequestMapping("/contest/{contestId}")
	public String getContest(Model model, @PathVariable Integer contestId) {
        Contest contest = contestService.findById(contestId);
        externalApi.updateProblemDetails(contest.getProblems());
        model.addAttribute("contest", contest);
		return VIEWS_CONTEST_PROBLEMS;
	}

    @RequestMapping("/contest/{contestId}/{problemNumber}")
    public String loadStatement(@PathVariable Integer contestId, @PathVariable Integer problemNumber) {
        Contest contest = contestService.findById(contestId);
        Problem problem = contest.getProblems().get(problemNumber - 1);
        return "redirect:" + externalApi.getStatementUrl(problem.getProblemId());
    }
	
	@RequestMapping("/contest/{contestId}/submissions")
	public String getAllSubmissionsForContest(Model model, @PathVariable Integer contestId, @AuthenticationPrincipal User user) {
        Contest contest = contestService.findById(contestId);
        externalApi.updateContest(contest);

        List<Submission> submissions = contest.getSubmissions();
        submissions.removeIf(s -> s.getAuthor().getUserId() != (int) user.getUserId());

        model.addAttribute("submissions", submissions);
		model.addAttribute("contestName", contest.getContestName());
		return VIEWS_SUBMISSION_LIST;
	}
	
	@GetMapping("/contest/{contestId}/submit")
	public String loadSubmissionForm(Model model, @PathVariable int contestId) {
        Contest contest = contestService.findById(contestId);

        Set<Language> allLanguages = EnumSet.noneOf(Language.class);
        contest.getProblems().forEach(p -> {
            String resource = ExternalApiHelper.extractResource(p.getProblemId());
            allLanguages.addAll(externalApi.getSupportedLanguages(resource));
        });

        model.addAttribute("submission", new Submission());
        model.addAttribute("languages", allLanguages);

        List<Problem> problems = contest.getProblems();
        externalApi.updateProblemDetails(problems);
        model.addAttribute("problems", problems);

		return VIEWS_SUBMIT_PAGE;
	}
	
	@PostMapping("/contest/{contestId}/submit")
	public String submit(@ModelAttribute Submission submission,
                         @PathVariable Integer contestId,
                         @RequestParam MultipartFile file,
                         @AuthenticationPrincipal User user) throws IOException {
		if (!file.getOriginalFilename().isEmpty())
			submission.setSolution(new String(file.getBytes()));
		submission.setAuthor(user);
        submission.setContest(contestService.findById(contestId));
        submission.setCreationTime(LocalDateTime.now());
        submissionService.submit(submission, false);
		return "redirect:/contest/{contestId}/submissions";
	}

    @RequestMapping("/submission/{submissionId}")
    public String getSubmission(@AuthenticationPrincipal User user, Model model, @PathVariable int submissionId) {
        Submission submission = submissionService.findById(submissionId);
        if (!submission.getAuthor().getUserId().equals(user.getUserId()))
            throw new SecurityException("Not enough rights to see this page");

        externalApi.updateSubmissionResults(singletonList(submission));
        externalApi.updateProblemDetails(singletonList(submission.getProblem()));

        model.addAttribute(submission);
        return VIEWS_SUBMISSION_VIEW;
    }
}