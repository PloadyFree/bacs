package istu.bacs.externalapi.sybon;

import lombok.Data;

import java.util.List;

@Data
public class SybonTestGroupResult {
    private boolean executed;
    private List<SybonTestResult> testResults;
}